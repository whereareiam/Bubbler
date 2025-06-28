package me.whereareiam.socialismus.module.bubbler.common.animation.mode;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3f;
import com.google.inject.Inject;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.api.output.Scheduler;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.*;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.PassengerPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.DestroyEntitiesPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.TextDisplayPacket;
import me.whereareiam.socialismus.module.bubbler.common.animation.BubbleQueue;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base implementation that provides queue handling and common packet utilities
 * for different bubble animations.
 */
public abstract class BaseBubbleAnimation extends BubbleAnimation {

protected final ConcurrentHashMap<DummyPlayer, BubbleQueue> playerQueues = new ConcurrentHashMap<>();

@Inject
protected BaseBubbleAnimation(Scheduler scheduler) {
super(scheduler);
}

@Override
public void display(BubbleMessage bubbleMessage) {
Logger.debug("Displaying bubble message for: " + bubbleMessage.getSender().getUsername());

BubbleQueue queue = playerQueues.computeIfAbsent(bubbleMessage.getSender(), p -> new BubbleQueue());
queue.addMessage(bubbleMessage);

if (!queue.isProcessing()) {
processNextMessageInQueue(bubbleMessage.getSender(), queue);
}
}

protected void processNextMessageInQueue(DummyPlayer sender, BubbleQueue queue) {
queue.processNextGroup(
(message, group) -> showGroupAndScheduleRemoval(sender, message, group, queue),
() -> processNextMessageInQueue(sender, queue)
);
}

protected void showGroupAndScheduleRemoval(DummyPlayer sender, BubbleMessage bubbleMessage, BubbleGroup group, BubbleQueue queue) {
Logger.debug("Showing group for bubbleMessage from " + bubbleMessage.getSender().getUsername());

Set<DummyPlayer> recipients = bubbleMessage.getRecipients();
Bubble bubble = bubbleMessage.getBubble();
float headLineGap = bubble.getDisplay().getHeadLineGap();
float lineSpacing = bubble.getDisplay().getLineSpacing();

Map<DummyPlayer, List<Integer>> recipientEntityMap = new HashMap<>();

List<Integer> entityIds = new ArrayList<>();
List<BubbleLine> lines = group.getLines();

spawnLines(sender, recipients, bubble, lines, headLineGap, lineSpacing, recipientEntityMap, entityIds);

long delayMs = group.calculateDisplayTime() * 1000L;
scheduler.schedule(
DelayedRunnableTask.builder()
.module("bubbler")
.delay(delayMs)
.runnable(() -> {
destroyGroupEntities(recipientEntityMap);

queue.setProcessing(false);

if (!bubbleMessage.getGroups().isEmpty()) {
processNextMessageInQueue(sender, queue);
} else {
queue.getMessages().poll();
processNextMessageInQueue(sender, queue);
}
})
.build()
);
}

protected TextDisplayPacket createTextDisplayPacket(Bubble bubble, BubbleLine line, float yOffset) {
Bubble.Style style = bubble.getStyle();

Vector3f scale = style.getScale() != null ? style.getScale().toVector3f() : new Vector3f(1.0F, 1.0F, 1.0F);

return TextDisplayPacket.builder()
.text(line.getContent())
.type(style.getDisplay())
.backgroundColor(style.getBackground().getColor())
.transparency(style.getBackground().getTransparency())
.alignment(style.getText().getAlignment())
.hasShadow(style.getText().isShadow())
.isSeeThrough(style.isSeeThrough())
.translation(new Vector3f(0, yOffset, 0))
.scale(scale)
.build();
}

protected PassengerPacket createPassengerPacket(User user, List<Integer> entityIds) {
Logger.debug("Creating passenger packet for user: " + user.getProfile().getName());
return PassengerPacket.builder()
.passengerIds(entityIds.stream().mapToInt(Integer::intValue).toArray())
.vehicleId(user.getEntityId())
.build();
}

protected void destroyGroupEntities(Map<DummyPlayer, List<Integer>> entities) {
Logger.debug("Destroying displayed entities for " + entities.size() + " recipients");
entities.forEach((recipient, entityIds) -> {
User user = PacketEvents.getAPI().getPlayerManager().getUser(recipient.getAudience());
if (user != null) {
DestroyEntitiesPacket.builder()
.entityIds(entityIds.stream().mapToInt(Integer::intValue).toArray())
.build()
.send(user);
}
});
}

protected User getUser(DummyPlayer dummyPlayer) {
return PacketEvents.getAPI().getPlayerManager().getUser(dummyPlayer.getAudience());
}

protected abstract void spawnLines(
DummyPlayer sender,
Set<DummyPlayer> recipients,
Bubble bubble,
List<BubbleLine> lines,
float headLineGap,
float lineSpacing,
Map<DummyPlayer, List<Integer>> recipientEntityMap,
List<Integer> entityIds
);
}

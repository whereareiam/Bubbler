package me.whereareiam.socialismus.module.bubbler.common.animation.mode;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.output.Scheduler;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleLine;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.PassengerPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.TextDisplayPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
public class PopoutBubbleAnimation extends BaseBubbleAnimation {

@Inject
public PopoutBubbleAnimation(Scheduler scheduler) {
super(scheduler);
}

@Override
protected void spawnLines(
DummyPlayer sender,
Set<DummyPlayer> recipients,
Bubble bubble,
List<BubbleLine> lines,
float headLineGap,
float lineSpacing,
Map<DummyPlayer, List<Integer>> recipientEntityMap,
List<Integer> entityIds
) {
for (int i = 0; i < lines.size(); i++) {
BubbleLine line = lines.get(i);
float yOffset = headLineGap + (i * lineSpacing);
long delay = i * 200L;
scheduler.schedule(
DelayedRunnableTask.builder()
.module("bubbler")
.delay(delay)
.runnable(() -> {
TextDisplayPacket packet = createTextDisplayPacket(bubble, line, yOffset);
recipients.forEach(r -> packet.send(getUser(r)));
entityIds.add(packet.getEntityId());
PassengerPacket passenger = createPassengerPacket(getUser(sender), entityIds);
recipients.forEach(r -> passenger.send(getUser(r)));
recipientEntityMap.put(sender, new ArrayList<>(entityIds));
})
.build()
);
}
}
}

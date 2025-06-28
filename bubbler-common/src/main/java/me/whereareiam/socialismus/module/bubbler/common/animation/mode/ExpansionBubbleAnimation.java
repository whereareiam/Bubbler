package me.whereareiam.socialismus.module.bubbler.common.animation.mode;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.output.Scheduler;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.module.bubbler.api.model.Vector;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleLine;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.PassengerPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.DisplayMetadataPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.TextDisplayPacket;
import com.github.retrooper.packetevents.util.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
public class ExpansionBubbleAnimation extends BaseBubbleAnimation {

@Inject
public ExpansionBubbleAnimation(Scheduler scheduler) {
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
Vector styleScale = bubble.getStyle().getScale();
Vector3f targetScale = styleScale != null ? styleScale.toVector3f() : new Vector3f(1.0F, 1.0F, 1.0F);

for (int i = 0; i < lines.size(); i++) {
BubbleLine line = lines.get(i);
float yOffset = headLineGap + (i * lineSpacing);

Vector3f initialScale = new Vector3f(0.1F, 0.1F, 0.1F);

TextDisplayPacket textPacket = TextDisplayPacket.builder()
.text(line.getContent())
.type(bubble.getStyle().getDisplay())
.backgroundColor(bubble.getStyle().getBackground().getColor())
.transparency(bubble.getStyle().getBackground().getTransparency())
.alignment(bubble.getStyle().getText().getAlignment())
.hasShadow(bubble.getStyle().getText().isShadow())
.isSeeThrough(bubble.getStyle().isSeeThrough())
.translation(new Vector3f(0, yOffset, 0))
.scale(initialScale)
.build();
recipients.forEach(r -> textPacket.send(getUser(r)));
entityIds.add(textPacket.getEntityId());

PassengerPacket passengerPacket = createPassengerPacket(getUser(sender), entityIds);
recipients.forEach(r -> passengerPacket.send(getUser(r)));
recipientEntityMap.put(sender, new ArrayList<>(entityIds));

DisplayMetadataPacket metaPacket = DisplayMetadataPacket.builder()
.entityId(textPacket.getEntityId())
.scale(targetScale)
.build();
scheduler.schedule(
DelayedRunnableTask.builder()
.module("bubbler")
.delay(100L)
.runnable(() -> recipients.forEach(u -> metaPacket.send(getUser(u))))
.build()
);
}
}
}

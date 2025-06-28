package me.whereareiam.socialismus.module.bubbler.common.animation.mode;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.output.Scheduler;
import me.whereareiam.socialismus.api.util.ComponentUtil;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleLine;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.PassengerPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.TextDisplayPacket;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
public class StaticBubbleAnimation extends BaseBubbleAnimation {

@Inject
public StaticBubbleAnimation(Scheduler scheduler) {
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

TextDisplayPacket textPacket = createTextDisplayPacket(bubble, line, yOffset);
recipients.forEach(r -> textPacket.send(getUser(r)));
entityIds.add(textPacket.getEntityId());
}

if (!entityIds.isEmpty()) {
PassengerPacket passengerPacket = createPassengerPacket(getUser(sender), entityIds);
recipients.forEach(r -> passengerPacket.send(getUser(r)));
recipientEntityMap.put(sender, entityIds);
}
}
}

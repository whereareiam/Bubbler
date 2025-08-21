package me.whereareiam.socialismus.module.bubbler.common.animation.mode.queue;

import com.google.inject.Inject;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.position.Position;
import me.whereareiam.socialismus.api.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.api.output.Scheduler;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleGroup;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.PassengerPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.TextDisplayPacket;
import me.whereareiam.socialismus.module.bubbler.common.animation.type.queue.AbstractQueuedAnimation;
import me.whereareiam.socialismus.module.bubbler.common.animation.type.queue.BubbleQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class StaticBubbleAnimation extends AbstractQueuedAnimation {
	@Inject
	public StaticBubbleAnimation(Scheduler scheduler) {
		super(scheduler);
	}

	@Override
	protected void playGroup(
			DummyPlayer sender,
			BubbleMessage msg,
			BubbleGroup group,
			BubbleQueue queue
	) {
		Bubble bubble = msg.getBubble();
		Set<DummyPlayer> recipients = msg.getRecipients();
		float headGap = bubble.getDisplay().getHeadLineGap();
		float spacing = bubble.getDisplay().getLineSpacing();

		List<Integer> entityIds = new ArrayList<>();
		Position eyePos = sender.getEyePosition();

		for (int i = 0; i < group.getLines().size(); i++) {
			float y = headGap + i * spacing;
			TextDisplayPacket p = textPacket(bubble, group.getLines().get(i), y, eyePos);
			recipients.forEach(r -> p.send(user(r)));
			entityIds.add(p.getEntityId());
		}

		if (!entityIds.isEmpty()) {
			int[] arr = entityIds.stream().mapToInt(Integer::intValue).toArray();
			PassengerPacket passenger = passengerPacket(user(sender), arr);
			recipients.forEach(r -> passenger.send(user(r)));
		}

		long delayMs = group.calculateDisplayTime() * 1_000L;
		scheduler.schedule(
				DelayedRunnableTask.builder()
						.module("bubbler")
						.delay(delayMs)
						.runnable(() -> {
							recipients.forEach(r -> destroyEntities(Map.of(r, entityIds)));
							queue.setProcessing(false);
							nextGroup(sender, queue);
						})
						.build()
		);
	}
}

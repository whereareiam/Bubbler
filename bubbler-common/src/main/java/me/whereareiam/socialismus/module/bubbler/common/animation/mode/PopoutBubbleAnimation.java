package me.whereareiam.socialismus.module.bubbler.common.animation.mode;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.position.Position;
import me.whereareiam.socialismus.api.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.api.output.Scheduler;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleGroup;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleLine;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.PassengerPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.TextDisplayPacket;
import me.whereareiam.socialismus.module.bubbler.common.animation.type.queue.AbstractQueuedAnimation;
import me.whereareiam.socialismus.module.bubbler.common.animation.type.queue.BubbleQueue;

import java.util.*;

@Singleton
public final class PopoutBubbleAnimation extends AbstractQueuedAnimation {
	private final Provider<BubblerSettings> settings;

	@Inject
	public PopoutBubbleAnimation(Scheduler scheduler, Provider<BubblerSettings> settings) {
		super(scheduler);

		this.settings = settings;
	}

	@Override
	protected void playGroup(
			DummyPlayer sender,
			BubbleMessage msg,
			BubbleGroup group,
			BubbleQueue queue
	) {
		List<BubbleLine> lines = new ArrayList<>(group.getLines());
		Collections.reverse(lines);

		step(sender, msg, lines, 0, Collections.emptyMap(), queue);
	}

	private void step(
			DummyPlayer sender,
			BubbleMessage msg,
			List<BubbleLine> lines,
			int index,
			Map<DummyPlayer, List<Integer>> prevEntities,
			BubbleQueue queue
	) {
		if (!prevEntities.isEmpty()) {
			destroyEntities(prevEntities);
		}

		if (index >= lines.size()) {
			queue.setProcessing(false);
			nextGroup(sender, queue);
			return;
		}

		Bubble bubble = msg.getBubble();
		int maxVisible = bubble.getDisplay().getMaxLinesCount();
		float headGap = bubble.getDisplay().getHeadLineGap();
		float lineSpacing = bubble.getDisplay().getLineSpacing();
		int start = Math.max(0, index - maxVisible + 1);
		List<BubbleLine> slice = lines.subList(start, index + 1);

		Position eyePos = sender.getEyePosition();
		Map<DummyPlayer, List<Integer>> map = new HashMap<>();
		List<Integer> currentIds = new ArrayList<>();

		for (int i = 0; i < slice.size(); i++) {
			float y = headGap + i * lineSpacing;
			TextDisplayPacket pkt =
					textPacket(bubble, slice.get(i), y, eyePos);

			msg.getRecipients().forEach(r -> pkt.send(user(r)));
			currentIds.add(pkt.getEntityId());
		}

		if (!currentIds.isEmpty()) {
			int[] idArr = currentIds.stream().mapToInt(Integer::intValue).toArray();
			PassengerPacket passenger = passengerPacket(user(sender), idArr);
			msg.getRecipients().forEach(r -> passenger.send(user(r)));
			map.put(sender, currentIds);
		}

		long delayMs = lines.get(index).getDisplayTime() * settings.get().getAnimation().getPopoutDelay();
		scheduler.schedule(
				DelayedRunnableTask.builder()
						.module("bubbler")
						.delay(delayMs)
						.runnable(() ->
								step(sender, msg, lines, index + 1, map, queue))
						.build()
		);
	}
}

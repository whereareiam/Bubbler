package me.whereareiam.socialismus.module.bubbler.common.animation.mode.queue.stacked;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3f;
import com.google.inject.Provider;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.api.output.Scheduler;
import me.whereareiam.socialismus.module.bubbler.api.model.Vector;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleGroup;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleLine;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.PassengerPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.TextDisplayPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.metadata.TranslationMetadataPacket;
import me.whereareiam.socialismus.module.bubbler.common.animation.type.queue.AbstractQueuedAnimation;
import me.whereareiam.socialismus.module.bubbler.common.animation.type.queue.BubbleQueue;

import java.util.*;

abstract class StackedBubbleAnimation extends AbstractQueuedAnimation {
	private static final long TICK_MS = 50L;

	private final Provider<BubblerSettings> settings;

	protected StackedBubbleAnimation(Scheduler scheduler, Provider<BubblerSettings> settings) {
		super(scheduler);
		this.settings = settings;
	}

	protected abstract Vector3f initialScale(Bubble bubble);

	protected abstract void spawnAnimation(int entityId, Bubble bubble, Collection<DummyPlayer> recipients);

	protected abstract void removalAnimation(DummyPlayer sender,
	                                         int entityId,
	                                         Bubble bubble,
	                                         Collection<DummyPlayer> recipients,
	                                         Runnable after);

	protected abstract long extraSpawnDelayMs(Bubble bubble);

	@Override
	protected final void playGroup(DummyPlayer sender, BubbleMessage msg, BubbleGroup group, BubbleQueue queue) {
		List<BubbleLine> lines = new ArrayList<>(group.getLines());
		Collections.reverse(lines);
		step(sender, msg, lines, 0, new HashMap<>(), queue);
	}

	private void step(
			DummyPlayer sender,
			BubbleMessage msg,
			List<BubbleLine> lines,
			int index,
			Map<DummyPlayer, List<Integer>> entities,
			BubbleQueue queue
	) {
		Bubble bubble = msg.getBubble();
		float headGap = bubble.getDisplay().getHeadLineGap();
		float spacing = bubble.getDisplay().getLineSpacing();
		int maxLines = bubble.getDisplay().getMaxLinesCount();

		List<Integer> ids = entities.computeIfAbsent(sender, $ -> new ArrayList<>());

		if (index >= lines.size()) {
			if (ids.isEmpty()) {
				queue.setProcessing(false);
				nextGroup(sender, queue);
				return;
			}
			int eid = ids.get(0);
			removalAnimation(sender, eid, bubble, msg.getRecipients(), () -> {
				ids.remove(Integer.valueOf(eid));
				updateTranslations(ids, headGap, spacing, msg.getRecipients());
				resendPassengers(sender, ids, msg.getRecipients());
				schedule(settings.get().getAnimation().getPopoutDelay(),
						() -> step(sender, msg, lines, index, entities, queue));
			});
			return;
		}

		TextDisplayPacket spawn = textPacket(bubble, lines.get(index), headGap, sender.getEyePosition())
				.toBuilder()
				.scale(initialScale(bubble))
				.build();

		msg.getRecipients().forEach(r -> {
			User u = user(r);
			spawn.send(u);
			spawnAnimation(spawn.getEntityId(), bubble, List.of(r));
		});

		ids.add(spawn.getEntityId());
		updateTranslations(ids, headGap, spacing, msg.getRecipients());

		if (ids.size() > maxLines) {
			int overflow = ids.get(0);
			removalAnimation(sender, overflow, bubble, msg.getRecipients(), () -> {
				ids.remove(Integer.valueOf(overflow));
				updateTranslations(ids, headGap, spacing, msg.getRecipients());
				resendPassengers(sender, ids, msg.getRecipients());
			});
		}

		resendPassengers(sender, ids, msg.getRecipients());

		long readMs = lines.get(index).getDisplayTime()
				* settings.get().getAnimation().getPopoutDelay();
		long nextMs = extraSpawnDelayMs(bubble) + readMs;
		schedule(nextMs, () ->
				step(sender, msg, lines, index + 1, entities, queue));
	}

	private void updateTranslations(List<Integer> ids, float headGap, float spacing, Collection<DummyPlayer> rec) {
		for (int i = ids.size() - 1, level = 0; i >= 0; i--, level++) {
			Vector3f t = new Vector3f(0F, headGap + level * spacing, 0F);
			int id = ids.get(i);
			rec.forEach(r -> TranslationMetadataPacket.builder()
					.entityId(id)
					.translation(t)
					.build()
					.send(user(r)));
		}
	}

	private void resendPassengers(DummyPlayer sender, List<Integer> ids, Collection<DummyPlayer> rec) {
		int[] arr = ids.stream().mapToInt(Integer::intValue).toArray();
		PassengerPacket p = passengerPacket(user(sender), arr);
		rec.forEach(r -> p.send(user(r)));
	}

	protected Vector3f toVec(Vector v) {
		return new Vector3f(v.getX(), v.getY(), v.getZ());
	}

	protected void schedule(long delayMs, Runnable r) {
		scheduler.schedule(DelayedRunnableTask.builder()
				.module("bubbler")
				.delay(delayMs)
				.runnable(r)
				.build());
	}

	protected long tickMs() {
		return TICK_MS;
	}
}
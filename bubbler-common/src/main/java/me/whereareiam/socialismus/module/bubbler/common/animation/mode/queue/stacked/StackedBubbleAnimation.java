package me.whereareiam.socialismus.module.bubbler.common.animation.mode.queue.stacked;

import com.github.retrooper.packetevents.protocol.player.User;
import com.google.inject.Provider;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.position.Position;
import me.whereareiam.socialismus.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleGroup;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleLine;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;
import me.whereareiam.socialismus.module.bubbler.api.renderer.BubbleRenderer;
import me.whereareiam.socialismus.module.bubbler.api.renderer.RenderedLine;
import me.whereareiam.socialismus.module.bubbler.common.animation.type.queue.AbstractQueuedAnimation;
import me.whereareiam.socialismus.module.bubbler.common.animation.type.queue.BubbleQueue;
import me.whereareiam.socialismus.module.bubbler.common.renderer.ArmorStandRenderer;
import me.whereareiam.socialismus.module.bubbler.common.renderer.BubbleRendererFactory;
import me.whereareiam.socialismus.service.Scheduler;

import java.util.*;

abstract class StackedBubbleAnimation extends AbstractQueuedAnimation {
	protected static final long TICK_MS = 50L;

	protected final Provider<BubblerSettings> settings;

	protected StackedBubbleAnimation(
			Scheduler scheduler,
			BubbleRendererFactory rendererFactory,
			Provider<BubblerSettings> settings
	) {
		super(scheduler, rendererFactory);
		this.settings = settings;
	}

	protected abstract boolean useSpawnAnimation();

	protected abstract boolean useRemovalAnimation();

	protected abstract long extraSpawnDelayMs(Bubble bubble);

	@Override
	protected final void playGroup(SocialismusPlayer sender, BubbleMessage msg, BubbleGroup group, BubbleQueue queue) {
		List<BubbleLine> lines = new ArrayList<>(group.getLines());
		Collections.reverse(lines);

		BubbleRenderer renderer = getRenderer();
		if (renderer.supportsScaleAnimation()) {
			stepTextDisplay(sender, msg, lines, 0, new ArrayList<>(), queue);
		} else {
			stepArmorStand(sender, msg, lines, 0, new ArrayList<>(), queue);
		}
	}

	private void stepTextDisplay(
			SocialismusPlayer sender,
			BubbleMessage msg,
			List<BubbleLine> lines,
			int index,
			List<RenderedLine> renderedLines,
			BubbleQueue queue
	) {
		Bubble bubble = msg.getBubble();
		float headGap = bubble.getDisplay().getHeadLineGap();
		float spacing = bubble.getDisplay().getLineSpacing();
		int maxLines = bubble.getDisplay().getMaxLinesCount();
		Position eyePos = sender.getEyePosition();
		Collection<User> recipients = users(msg.getRecipients());
		BubbleRenderer renderer = getRenderer();

		if (index >= lines.size()) {
			if (renderedLines.isEmpty()) {
				queue.setProcessing(false);
				nextGroup(sender, queue);
				return;
			}

			RenderedLine toRemove = renderedLines.get(0);
			if (useRemovalAnimation()) {
				renderer.animateRemoval(toRemove, bubble, recipients, () -> {
					renderedLines.remove(0);
					updateTextDisplayPositions(renderedLines, headGap, spacing, recipients, renderer);
					reattachAllAsPassengers(sender, renderedLines, recipients, renderer);
					schedule(settings.get().getAnimation().getPopoutDelay(),
							() -> stepTextDisplay(sender, msg, lines, index, renderedLines, queue));
				});
			} else {
				renderer.destroy(toRemove, recipients);
				renderedLines.remove(0);
				updateTextDisplayPositions(renderedLines, headGap, spacing, recipients, renderer);
				reattachAllAsPassengers(sender, renderedLines, recipients, renderer);
				schedule(settings.get().getAnimation().getPopoutDelay(),
						() -> stepTextDisplay(sender, msg, lines, index, renderedLines, queue));
			}
			return;
		}

		RenderedLine line = renderer.spawnLine(bubble, lines.get(index), headGap, eyePos);
		if (useSpawnAnimation()) {
			renderer.applyInitialScale(line, bubble, recipients);
		}
		renderer.sendSpawn(line, recipients);

		if (useSpawnAnimation()) {
			renderer.animateSpawn(line, bubble, recipients);
		}

		renderedLines.add(line);
		updateTextDisplayPositions(renderedLines, headGap, spacing, recipients, renderer);

		if (renderedLines.size() > maxLines) {
			RenderedLine overflow = renderedLines.get(0);
			if (useRemovalAnimation()) {
				renderer.animateRemoval(overflow, bubble, recipients, () -> {
					renderedLines.remove(0);
					updateTextDisplayPositions(renderedLines, headGap, spacing, recipients, renderer);
					reattachAllAsPassengers(sender, renderedLines, recipients, renderer);
				});
			} else {
				renderer.destroy(overflow, recipients);
				renderedLines.remove(0);
				updateTextDisplayPositions(renderedLines, headGap, spacing, recipients, renderer);
			}
		}

		reattachAllAsPassengers(sender, renderedLines, recipients, renderer);

		long readMs = lines.get(index).getDisplayTime() * settings.get().getAnimation().getPopoutDelay();
		long nextMs = extraSpawnDelayMs(bubble) + readMs;
		schedule(nextMs, () -> stepTextDisplay(sender, msg, lines, index + 1, renderedLines, queue));
	}

	private void stepArmorStand(
			SocialismusPlayer sender,
			BubbleMessage msg,
			List<BubbleLine> lines,
			int index,
			List<RenderedLine> renderedLines,
			BubbleQueue queue
	) {
		Bubble bubble = msg.getBubble();
		int maxLines = bubble.getDisplay().getMaxLinesCount();
		Position eyePos = sender.getEyePosition();
		Collection<User> recipients = users(msg.getRecipients());
		ArmorStandRenderer renderer = rendererFactory.getArmorStandRenderer();

		if (index >= lines.size()) {
			if (renderedLines.isEmpty()) {
				queue.setProcessing(false);
				nextGroup(sender, queue);
				return;
			}

			RenderedLine toRemove = renderedLines.get(0);
			renderer.destroy(toRemove, recipients);
			renderedLines.remove(0);

			schedule(settings.get().getAnimation().getPopoutDelay(),
					() -> stepArmorStand(sender, msg, lines, index, renderedLines, queue));
			return;
		}

		RenderedLine line = renderer.spawnLineWithSpacer(lines.get(index), eyePos);
		renderer.sendSpawn(line, recipients);
		renderer.attachArmorStandOnly(user(sender).getEntityId(), line, recipients);

		if (!renderedLines.isEmpty()) {
			RenderedLine previousBottom = renderedLines.get(renderedLines.size() - 1);
			renderer.rechainPassenger(renderer.getPassengerAnchorId(line), previousBottom, recipients);
		}

		renderedLines.add(line);

		if (renderedLines.size() > maxLines) {
			RenderedLine overflow = renderedLines.get(0);
			renderer.destroy(overflow, recipients);
			renderedLines.remove(0);
		}

		long readMs = lines.get(index).getDisplayTime() * settings.get().getAnimation().getPopoutDelay();
		long nextMs = extraSpawnDelayMs(bubble) + readMs;
		schedule(nextMs, () -> stepArmorStand(sender, msg, lines, index + 1, renderedLines, queue));
	}

	private void updateTextDisplayPositions(
			List<RenderedLine> lines,
			float headGap,
			float spacing,
			Collection<User> recipients,
			BubbleRenderer renderer
	) {
		for (int i = lines.size() - 1, level = 0; i >= 0; i--, level++) {
			float yOffset = headGap + level * spacing;
			renderer.updatePosition(lines.get(i), yOffset, null, recipients);
		}
	}

	private void reattachAllAsPassengers(
			SocialismusPlayer sender,
			List<RenderedLine> lines,
			Collection<User> recipients,
			BubbleRenderer renderer
	) {
		int vehicleId = user(sender).getEntityId();
		for (RenderedLine line : lines) {
			renderer.attachAsPassenger(vehicleId, line, recipients);
		}
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

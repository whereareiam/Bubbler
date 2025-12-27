package me.whereareiam.socialismus.module.bubbler.common.animation.mode.queue;

import com.github.retrooper.packetevents.protocol.player.User;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.position.Position;
import me.whereareiam.socialismus.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleGroup;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleLine;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.renderer.BubbleRenderer;
import me.whereareiam.socialismus.module.bubbler.api.renderer.RenderedLine;
import me.whereareiam.socialismus.module.bubbler.common.animation.type.queue.AbstractQueuedAnimation;
import me.whereareiam.socialismus.module.bubbler.common.animation.type.queue.BubbleQueue;
import me.whereareiam.socialismus.module.bubbler.common.renderer.ArmorStandRenderer;
import me.whereareiam.socialismus.module.bubbler.common.renderer.BubbleRendererFactory;
import me.whereareiam.socialismus.service.Scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Singleton
public final class StaticBubbleAnimation extends AbstractQueuedAnimation {

	@Inject
	public StaticBubbleAnimation(Scheduler scheduler, BubbleRendererFactory rendererFactory) {
		super(scheduler, rendererFactory);
	}

	@Override
	protected void playGroup(
			SocialismusPlayer sender,
			BubbleMessage msg,
			BubbleGroup group,
			BubbleQueue queue
	) {
		Bubble bubble = msg.getBubble();
		Collection<User> recipients = users(msg.getRecipients());
		float headGap = bubble.getDisplay().getHeadLineGap();
		float spacing = bubble.getDisplay().getLineSpacing();
		Position eyePos = sender.getEyePosition();

		BubbleRenderer renderer = getRenderer();
		List<RenderedLine> lines = new ArrayList<>();

		if (renderer.supportsScaleAnimation()) {
			spawnTextDisplayLines(bubble, group, headGap, spacing, eyePos, renderer, recipients, lines, sender);
		} else {
			spawnArmorStandLines(bubble, group, eyePos, recipients, lines, sender);
		}

		long delayMs = group.calculateDisplayTime() * 1_000L;
		scheduler.schedule(
				DelayedRunnableTask.builder()
						.module("bubbler")
						.delay(delayMs)
						.runnable(() -> {
							renderer.destroy(lines, recipients);
							queue.setProcessing(false);
							nextGroup(sender, queue);
						})
						.build()
		);
	}

	private void spawnTextDisplayLines(
			Bubble bubble,
			BubbleGroup group,
			float headGap,
			float spacing,
			Position eyePos,
			BubbleRenderer renderer,
			Collection<User> recipients,
			List<RenderedLine> lines,
			SocialismusPlayer sender
	) {
		List<BubbleLine> bubbleLines = group.getLines();

		for (int i = 0; i < bubbleLines.size(); i++) {
			float yOffset = headGap + i * spacing;
			RenderedLine line = renderer.spawnLine(bubble, bubbleLines.get(i), yOffset, eyePos);
			renderer.sendSpawn(line, recipients);
			lines.add(line);
		}

		int vehicleId = user(sender).getEntityId();
		for (RenderedLine line : lines) {
			renderer.attachAsPassenger(vehicleId, line, recipients);
		}
	}

	private void spawnArmorStandLines(
			Bubble bubble,
			BubbleGroup group,
			Position eyePos,
			Collection<User> recipients,
			List<RenderedLine> lines,
			SocialismusPlayer sender
	) {
		ArmorStandRenderer asRenderer = rendererFactory.getArmorStandRenderer();
		List<BubbleLine> bubbleLines = group.getLines();
		int previousVehicle = user(sender).getEntityId();

		for (int i = 0; i < bubbleLines.size(); i++) {
			RenderedLine line;
			if (i == 0) {
				line = asRenderer.spawnLine(bubble, bubbleLines.get(i), 0, eyePos);
			} else {
				line = asRenderer.spawnLineWithSpacer(bubbleLines.get(i), eyePos);
			}

			asRenderer.sendSpawn(line, recipients);
			asRenderer.attachAsPassenger(previousVehicle, line, recipients);
			lines.add(line);

			previousVehicle = asRenderer.getPassengerAnchorId(line);
		}
	}
}

package me.whereareiam.socialismus.module.bubbler.common.animation.mode.queue.stacked;

import com.github.retrooper.packetevents.util.Vector3f;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.Scheduler;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.metadata.InterpolationMetadataPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.metadata.ScaleMetadataPacket;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Singleton
public final class ExpansionBubbleAnimation extends StackedBubbleAnimation {
	private final Provider<BubblerSettings> settings;

	@Inject
	public ExpansionBubbleAnimation(Scheduler scheduler, Provider<BubblerSettings> settings) {
		super(scheduler, settings);
		this.settings = settings;
	}

	@Override
	protected Vector3f initialScale(Bubble b) {
		float s = settings.get().getAnimation().getExpansion().getStartScale();
		return new Vector3f(s, s, s);
	}

	@Override
	protected void spawnAnimation(int id, Bubble b, Collection<DummyPlayer> recipients) {
		long durMs = settings.get().getAnimation().getExpansion().getDuration();
		int ticks = Math.max(1, (int) Math.ceil(durMs / (double) tickMs()));

		recipients.forEach(r -> InterpolationMetadataPacket.builder()
				.entityId(id)
				.startDelayTicks(0)
				.transformDurationTicks(ticks)
				.positionRotationDurationTicks(0)
				.build()
				.send(user(r)));

		schedule(tickMs(), () ->
				recipients.forEach(r ->
						ScaleMetadataPacket.builder()
								.entityId(id)
								.scale(toVec(b.getStyle().getScale()))
								.build()
								.send(user(r))));
	}

	@Override
	protected void removalAnimation(DummyPlayer sender, int id, Bubble b, Collection<DummyPlayer> rec, Runnable after) {
		rec.forEach(r -> destroyEntities(Map.of(r, List.of(id))));
		after.run();
	}

	@Override
	protected long extraSpawnDelayMs(Bubble b) {
		return tickMs() + settings.get().getAnimation().getExpansion().getDuration();
	}
}

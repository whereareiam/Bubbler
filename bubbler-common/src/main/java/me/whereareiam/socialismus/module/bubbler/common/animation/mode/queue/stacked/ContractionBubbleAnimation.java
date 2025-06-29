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
public final class ContractionBubbleAnimation extends StackedBubbleAnimation {
	private final Provider<BubblerSettings> settings;

	@Inject
	public ContractionBubbleAnimation(Scheduler scheduler,
	                                  Provider<BubblerSettings> settings) {
		super(scheduler, settings);
		this.settings = settings;
	}

	@Override
	protected Vector3f initialScale(Bubble b) {
		return toVec(b.getStyle().getScale());
	}

	@Override
	protected void spawnAnimation(int id, Bubble b, Collection<DummyPlayer> rec) {
	}

	@Override
	protected void removalAnimation(DummyPlayer sender, int id, Bubble b, Collection<DummyPlayer> recipients, Runnable after) {
		long durationMs = settings.get().getAnimation().getContraction().getDuration();
		int ticks = Math.max(1, (int) Math.ceil(durationMs / (double) tickMs()));
		float end = settings.get().getAnimation().getContraction().getEndScale();

		recipients.forEach(r -> {
			InterpolationMetadataPacket.builder()
					.entityId(id)
					.startDelayTicks(0)
					.transformDurationTicks(ticks)
					.positionRotationDurationTicks(0)
					.build()
					.send(user(r));

			schedule(tickMs(), () -> ScaleMetadataPacket.builder()
					.entityId(id)
					.scale(new Vector3f(end, end, end))
					.build()
					.send(user(r)));
		});

		schedule(durationMs + tickMs(), () -> {
			destroyEntities(Map.of(sender, List.of(id)));
			after.run();
		});
	}

	@Override
	protected long extraSpawnDelayMs(Bubble b) {
		return 0;
	}
}

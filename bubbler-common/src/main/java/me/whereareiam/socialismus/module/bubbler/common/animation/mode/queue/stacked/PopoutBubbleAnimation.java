package me.whereareiam.socialismus.module.bubbler.common.animation.mode.queue.stacked;

import com.github.retrooper.packetevents.util.Vector3f;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.Scheduler;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Singleton
public final class PopoutBubbleAnimation extends StackedBubbleAnimation {

	@Inject
	public PopoutBubbleAnimation(Scheduler scheduler, Provider<BubblerSettings> settings) {
		super(scheduler, settings);
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
		recipients.forEach(r -> destroyEntities(Map.of(r, List.of(id))));
		after.run();
	}

	@Override
	protected long extraSpawnDelayMs(Bubble b) {
		return 0;
	}
}

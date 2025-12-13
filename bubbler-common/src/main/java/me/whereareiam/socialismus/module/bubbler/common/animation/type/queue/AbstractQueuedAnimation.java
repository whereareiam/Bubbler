package me.whereareiam.socialismus.module.bubbler.common.animation.type.queue;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3f;
import lombok.NonNull;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.position.Position;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.*;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.PassengerPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.DestroyEntitiesPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.TextDisplayPacket;
import me.whereareiam.socialismus.module.bubbler.common.util.PacketUtil;
import me.whereareiam.socialismus.service.Scheduler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractQueuedAnimation extends BubbleAnimation {

	private final Map<SocialismusPlayer, BubbleQueue> queues = new ConcurrentHashMap<>();

	protected AbstractQueuedAnimation(Scheduler scheduler) {
		super(scheduler);
	}

	@Override
	public final void display(@NonNull BubbleMessage message) {
		SocialismusPlayer sender = message.getSender();
		BubbleQueue queue = queues.computeIfAbsent(sender, $ -> new BubbleQueue());

		queue.addMessage(message);

		if (!queue.isProcessing()) {
			nextGroup(sender, queue);
		}
	}

	protected final void nextGroup(SocialismusPlayer sender, BubbleQueue queue) {
		queue.processNextGroup(
				(msg, grp) -> playGroup(sender, msg, grp, queue),
				() -> nextGroup(sender, queue)
		);
	}

	/**
	 * Sub-classes implement their visual behaviour here.
	 * <p>
	 * The call **must** end with {@code queue.setProcessing(false)} and then
	 * either call {@link #nextGroup(SocialismusPlayer, BubbleQueue)} again or remove
	 * the just-finished message if all groups are done.
	 */
	protected abstract void playGroup(
			SocialismusPlayer sender,
			BubbleMessage message,
			BubbleGroup group,
			BubbleQueue queue
	);

	protected TextDisplayPacket textPacket(
			Bubble bubble, BubbleLine line, float yOffset, Position eyePos
	) {
		Bubble.Style s = bubble.getStyle();
		return TextDisplayPacket.builder()
				.position(PacketUtil.toVector3d(eyePos))
				.text(line.getContent())
				.type(s.getDisplay())
				.backgroundColor(s.getBackground().getColor())
				.transparency(s.getBackground().getTransparency())
				.alignment(s.getText().getAlignment())
				.hasShadow(s.getText().isShadow())
				.isSeeThrough(s.isSeeThrough())
				.translation(new Vector3f(0, yOffset, 0))
				.build();
	}

	protected PassengerPacket passengerPacket(User user, int[] entityIds) {
		return PassengerPacket.builder()
				.vehicleId(user.getEntityId())
				.passengerIds(entityIds)
				.build();
	}

	protected void destroyEntities(Map<SocialismusPlayer, List<Integer>> map) {
		map.forEach((player, ids) -> {
			User user = PacketEvents.getAPI()
					.getPlayerManager()
					.getUser(player.getAudience());
			DestroyEntitiesPacket.builder()
					.entityIds(ids.stream().mapToInt(Integer::intValue).toArray())
					.build()
					.send(user);
		});
	}

	protected User user(SocialismusPlayer dummy) {
		return PacketEvents.getAPI()
				.getPlayerManager()
				.getUser(dummy.getAudience());
	}
}

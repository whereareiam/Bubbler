package me.whereareiam.socialismus.module.bubbler.common.animation.mode;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3f;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.position.Position;
import me.whereareiam.socialismus.api.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.api.output.Scheduler;
import me.whereareiam.socialismus.api.util.ComponentUtil;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.*;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.PassengerPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.DestroyEntitiesPacket;
import me.whereareiam.socialismus.module.bubbler.api.model.packet.type.entity.display.TextDisplayPacket;
import me.whereareiam.socialismus.module.bubbler.common.animation.BubbleQueue;
import me.whereareiam.socialismus.module.bubbler.common.util.PacketUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class StaticBubbleAnimation extends BubbleAnimation {
	private final ConcurrentHashMap<DummyPlayer, BubbleQueue> playerQueues = new ConcurrentHashMap<>();

	@Inject
	public StaticBubbleAnimation(Scheduler scheduler) {
		super(scheduler);
	}

	@Override
	public void display(BubbleMessage bubbleMessage) {
		Logger.debug("Displaying bubble message for: " + bubbleMessage.getSender().getUsername());

		BubbleQueue queue = playerQueues.computeIfAbsent(bubbleMessage.getSender(), p -> new BubbleQueue());
		queue.addMessage(bubbleMessage);

		if (!queue.isProcessing())
			processNextMessageInQueue(bubbleMessage.getSender(), queue);
	}

	private void processNextMessageInQueue(DummyPlayer sender, BubbleQueue queue) {
		queue.processNextGroup(
				(message, group) -> showGroupAndScheduleRemoval(sender, message, group, queue),
				() -> processNextMessageInQueue(sender, queue)
		);
	}

	private void showGroupAndScheduleRemoval(DummyPlayer sender, BubbleMessage bubbleMessage, BubbleGroup group, BubbleQueue queue) {
		Logger.debug("Showing group for bubbleMessage from " + bubbleMessage.getSender().getUsername());

		Set<DummyPlayer> recipients = bubbleMessage.getRecipients();
		Bubble bubble = bubbleMessage.getBubble();
		float headLineGap = bubble.getDisplay().getHeadLineGap();
		float lineSpacing = bubble.getDisplay().getLineSpacing();

		Map<DummyPlayer, List<Integer>> recipientEntityMap = new HashMap<>();

		List<Integer> entityIds = new ArrayList<>();
		List<BubbleLine> lines = group.getLines();

		for (int i = 0; i < lines.size(); i++) {
			BubbleLine line = lines.get(i);
			float yOffset = headLineGap + (i * lineSpacing);
			Position position = sender.getEyePosition();

			Logger.debug("Creating TextDisplayPacket for line: " + ComponentUtil.toPlain(line.getContent()) + " [" + i + "]");
			TextDisplayPacket textPacket = createTextDisplayPacket(bubble, line, yOffset, position);
			recipients.forEach(r -> textPacket.send(getUser(r)));
			entityIds.add(textPacket.getEntityId());
		}

		if (!entityIds.isEmpty()) {
			PassengerPacket passengerPacket = createPassengerPacket(getUser(sender), entityIds);
			recipients.forEach(r -> passengerPacket.send(getUser(r)));
			recipientEntityMap.put(sender, entityIds);
		}

		long delayMs = group.calculateDisplayTime() * 1000L;
		scheduler.schedule(
				DelayedRunnableTask.builder()
						.module("bubbler")
						.delay(delayMs)
						.runnable(() -> {
							destroyGroupEntities(recipientEntityMap);

							queue.setProcessing(false);

							if (!bubbleMessage.getGroups().isEmpty()) {
								processNextMessageInQueue(sender, queue);
							} else {
								queue.getMessages().poll();
								processNextMessageInQueue(sender, queue);
							}
						})
						.build()
		);
	}

	private TextDisplayPacket createTextDisplayPacket(
			Bubble bubble,
			BubbleLine line,
			float yOffset,
			Position position
	) {
		Bubble.Style style = bubble.getStyle();
		return TextDisplayPacket.builder()
				.position(PacketUtil.toVector3d(position))
				.text(line.getContent())
				.type(style.getDisplay())
				.backgroundColor(style.getBackground().getColor())
				.transparency(style.getBackground().getTransparency())
				.alignment(style.getText().getAlignment())
				.hasShadow(style.getText().isShadow())
				.isSeeThrough(style.isSeeThrough())
				.translation(new Vector3f(0, yOffset, 0))
				.build();
	}

	private PassengerPacket createPassengerPacket(User user, List<Integer> entityIds) {
		Logger.debug("Creating passenger packet for user: " + user.getProfile().getName());
		return PassengerPacket.builder()
				.passengerIds(entityIds.stream().mapToInt(Integer::intValue).toArray())
				.vehicleId(user.getEntityId())
				.build();
	}

	private void destroyGroupEntities(Map<DummyPlayer, List<Integer>> entities) {
		Logger.debug("Destroying displayed entities for " + entities.size() + " recipients");
		entities.forEach((recipient, entityIds) -> {
			User user = PacketEvents.getAPI().getPlayerManager().getUser(recipient.getAudience());
			if (user != null) {
				DestroyEntitiesPacket.builder()
						.entityIds(entityIds.stream().mapToInt(Integer::intValue).toArray())
						.build()
						.send(user);
			}
		});
	}

	private User getUser(DummyPlayer dummyPlayer) {
		return PacketEvents.getAPI().getPlayerManager().getUser(dummyPlayer.getAudience());
	}
}
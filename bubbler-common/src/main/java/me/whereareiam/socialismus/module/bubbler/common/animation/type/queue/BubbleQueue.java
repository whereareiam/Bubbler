package me.whereareiam.socialismus.module.bubbler.common.animation.type.queue;

import lombok.Getter;
import lombok.Setter;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleGroup;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

/**
 * A simple, reusable queue for bubble messages.
 * <p>
 * 'processNextGroup' is here to abstract away typical message-check logic:
 * - If queue is empty or already processing, do nothing
 * - If message is null or cancelled, skip it
 * - If no more groups remain, remove the message and proceed to the next
 * <p>
 * The actual handling of how to "show" a group is passed in via a callback.
 */
@Getter
@Setter
public class BubbleQueue {
	private final Queue<BubbleMessage> messages = new ConcurrentLinkedQueue<>();
	private boolean processing;

	/**
	 * Add a bubble message to the queue.
	 */
	public void addMessage(BubbleMessage message) {
		messages.add(message);
	}

	/**
	 * Checks and processes the next BubbleGroup in the head of the queue.
	 *
	 * @param groupHandler  how to handle/show the group once we pull it
	 * @param afterComplete what to do once the entire message is done
	 *                      (or cancelled/empty)—commonly re-trigger the
	 *                      process for the next message.
	 */
	public void processNextGroup(
			BiConsumer<BubbleMessage, BubbleGroup> groupHandler,
			Runnable afterComplete
	) {
		if (processing || messages.isEmpty()) {
			return;
		}

		setProcessing(true);

		// Check the head of the queue
		BubbleMessage message = messages.peek();
		if (message == null || message.isCancelled()) {
			// Remove invalid/cancelled message
			messages.poll();
			setProcessing(false);
			// Attempt next
			afterComplete.run();
			return;
		}

		// If no groups remain, remove the message and do afterComplete
		if (message.getGroups().isEmpty()) {
			messages.poll();
			setProcessing(false);
			afterComplete.run();
			return;
		}

		// Grab the next group and handle it
		BubbleGroup group = message.getGroups().poll();
		groupHandler.accept(message, group);
	}
}

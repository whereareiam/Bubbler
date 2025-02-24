package me.whereareiam.socialismus.module.bubbler.common;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.output.LoggingHelper;
import me.whereareiam.socialismus.module.bubbler.api.input.BubbleCoordinationService;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.common.animation.BubbleAnimationFactory;
import me.whereareiam.socialismus.module.bubbler.common.processor.BubbleMessageProcessor;

@Singleton
public class BubbleCoordinator implements BubbleCoordinationService {
	private final BubbleMessageProcessor bubbleMessageProcessor;
	private final BubbleAnimationFactory bubbleAnimationFactory;
	private final LoggingHelper loggingHelper;

	@Inject
	public BubbleCoordinator(BubbleMessageProcessor bubbleMessageProcessor, BubbleAnimationFactory bubbleAnimationFactory,
	                         LoggingHelper loggingHelper) {
		this.bubbleMessageProcessor = bubbleMessageProcessor;
		this.bubbleAnimationFactory = bubbleAnimationFactory;
		this.loggingHelper = loggingHelper;
	}

	@Override
	public void coordinate(BubbleMessage bubbleMessage) {
		loggingHelper.debug("Coordinating bubble message for: " + bubbleMessage.getSender().getUsername());
		bubbleMessage = bubbleMessageProcessor.process(bubbleMessage);

		loggingHelper.debug("Animating bubble message for: " + bubbleMessage.getSender().getUsername() + " with animation: "
				+ bubbleMessage.getBubble().getStyle().getAnimation());
		bubbleAnimationFactory
				.getAnimation(bubbleMessage.getBubble().getStyle().getAnimation())
				.display(bubbleMessage);
	}
}

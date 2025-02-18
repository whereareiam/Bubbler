package me.whereareiam.socialismus.module.bubbler.common;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.module.bubbler.api.input.BubbleCoordinationService;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.common.animation.BubbleAnimationFactory;
import me.whereareiam.socialismus.module.bubbler.common.processor.BubbleMessageProcessor;

@Singleton
public class BubbleCoordinator implements BubbleCoordinationService {
  private final BubbleMessageProcessor bubbleMessageProcessor;
  private final BubbleAnimationFactory bubbleAnimationFactory;

  @Inject
  public BubbleCoordinator(
      BubbleMessageProcessor bubbleMessageProcessor,
      BubbleAnimationFactory bubbleAnimationFactory) {
    this.bubbleMessageProcessor = bubbleMessageProcessor;
    this.bubbleAnimationFactory = bubbleAnimationFactory;
  }

  @Override
  public void coordinate(BubbleMessage bubbleMessage) {
    bubbleMessage = bubbleMessageProcessor.process(bubbleMessage);

    bubbleAnimationFactory
        .getAnimation(bubbleMessage.getBubble().getStyle().getAnimation())
        .display(bubbleMessage);
  }
}

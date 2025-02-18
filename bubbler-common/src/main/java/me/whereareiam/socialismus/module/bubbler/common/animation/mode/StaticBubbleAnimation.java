package me.whereareiam.socialismus.module.bubbler.common.animation.mode;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.*;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.scheduler.RunnableTask;
import me.whereareiam.socialismus.api.output.Scheduler;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleAnimation;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.common.BubbleQueue;

@Singleton
public class StaticBubbleAnimation extends BubbleAnimation {
  protected final Map<DummyPlayer, RunnableTask> activeBubbles = new HashMap<>();

  @Inject
  public StaticBubbleAnimation(Scheduler scheduler) {
    super(scheduler);
  }

  @Override
  public void display(BubbleMessage bubbleMessage) {
    BubbleQueue.add(bubbleMessage);
  }
}

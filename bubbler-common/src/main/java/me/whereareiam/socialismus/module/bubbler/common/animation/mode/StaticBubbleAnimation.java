package me.whereareiam.socialismus.module.bubbler.common.animation.mode;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.Scheduler;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleAnimation;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class StaticBubbleAnimation extends BubbleAnimation {
  private final ConcurrentHashMap<DummyPlayer, Queue<BubbleMessage>> bubbleMessages = new ConcurrentHashMap<>();

  @Inject
  public StaticBubbleAnimation(Scheduler scheduler) {
    super(scheduler);
  }

  @Override
  public void display(BubbleMessage bubbleMessage) {
  }
}

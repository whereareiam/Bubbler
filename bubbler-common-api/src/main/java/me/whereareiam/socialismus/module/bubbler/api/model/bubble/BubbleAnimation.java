package me.whereareiam.socialismus.module.bubbler.api.model.bubble;

import me.whereareiam.socialismus.api.output.Scheduler;

public abstract class BubbleAnimation {
  protected final Scheduler scheduler;

  protected BubbleAnimation(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public abstract void display(BubbleMessage bubbleMessage);

  protected long calculateTotalDisplayTime(BubbleMessage bubbleMessage) {
    return bubbleMessage.getLines().stream().mapToLong(BubbleLine::getDisplayTime).sum();
  }
}

package me.whereareiam.socialismus.module.bubbler.common.animation.mode;

import jakarta.inject.Inject;
import me.whereareiam.socialismus.api.output.Scheduler;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleAnimation;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;

public class PopoutBubbleAnimation extends BubbleAnimation {
    @Inject
    public PopoutBubbleAnimation(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    public void display(BubbleMessage bubbleMessage) {

    }
}

package me.whereareiam.socialismus.module.bubbler.common.animation.mode;

import jakarta.inject.Inject;
import me.whereareiam.socialismus.api.output.Scheduler;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleAnimation;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;

public class ExpansionBubbleAnimation extends BubbleAnimation {
    @Inject
    public ExpansionBubbleAnimation(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    public void display(BubbleMessage bubbleMessage) {

    }
}

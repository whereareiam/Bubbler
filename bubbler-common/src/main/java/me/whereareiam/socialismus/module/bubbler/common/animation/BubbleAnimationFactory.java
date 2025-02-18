package me.whereareiam.socialismus.module.bubbler.common.animation;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleAnimation;
import me.whereareiam.socialismus.module.bubbler.api.type.AnimationType;
import me.whereareiam.socialismus.module.bubbler.common.animation.mode.ExpansionBubbleAnimation;
import me.whereareiam.socialismus.module.bubbler.common.animation.mode.PopoutBubbleAnimation;
import me.whereareiam.socialismus.module.bubbler.common.animation.mode.StaticBubbleAnimation;

public class BubbleAnimationFactory {
  private final Injector injector;

  @Inject
  public BubbleAnimationFactory(Injector injector) {
    this.injector = injector;
  }

  public BubbleAnimation getAnimation(AnimationType animationType) {
    return switch (animationType) {
      case EXPANSION -> injector.getInstance(ExpansionBubbleAnimation.class);
      case POPOUT -> injector.getInstance(PopoutBubbleAnimation.class);
      case STATIC -> injector.getInstance(StaticBubbleAnimation.class);
    };
  }
}

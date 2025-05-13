package me.whereareiam.socialismus.module.bubbler.common.animation;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleAnimation;
import me.whereareiam.socialismus.module.bubbler.api.type.AnimationType;
import me.whereareiam.socialismus.module.bubbler.common.animation.mode.StaticBubbleAnimation;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class BubbleAnimationFactory {
	private final Injector injector;

	public BubbleAnimation getAnimation(AnimationType animationType) {
		return switch (animationType) {
			case EXPANSION -> throw new UnsupportedOperationException("Expansion animation is not supported yet");
			case POPOUT -> throw new UnsupportedOperationException("Popout animation is not supported yet");
			case STATIC -> injector.getInstance(StaticBubbleAnimation.class);
		};
	}
}

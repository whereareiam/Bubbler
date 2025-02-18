package me.whereareiam.socialismus.module.bubbler.api.model.bubble;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import net.kyori.adventure.text.Component;

@Getter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
public class InternalBubble extends Bubble {
    private Component content;

    public static InternalBubble from(Bubble bubble) {
        return InternalBubble.builder()
                .id(bubble.getId())
                .enabled(bubble.isEnabled())
                .display(bubble.getDisplay())
                .format(bubble.getFormat())
                .style(bubble.getStyle())
                .requirements(bubble.getRequirements())
                .build();
    }
}

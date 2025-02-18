package me.whereareiam.socialismus.module.bubbler.api.model.bubble;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class BubbleTransition {
    private Sound sound;
    private String particle;

    @Getter
    @ToString
    @NoArgsConstructor
    @SuperBuilder(toBuilder = true)
    public static class Sound {
        private String sound;
        private float volume;
        private float pitch;
    }
}

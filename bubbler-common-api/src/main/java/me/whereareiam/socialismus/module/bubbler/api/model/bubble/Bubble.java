package me.whereareiam.socialismus.module.bubbler.api.model.bubble;

import com.github.retrooper.packetevents.util.Vector3f;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.api.type.Participants;
import me.whereareiam.socialismus.module.bubbler.api.type.AlignmentType;
import me.whereareiam.socialismus.module.bubbler.api.type.AnimationType;
import me.whereareiam.socialismus.module.bubbler.api.type.DisplayType;
import me.whereareiam.socialismus.module.bubbler.api.type.TransitionType;

import java.util.Map;

@Getter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Bubble {
    private String id;
    private boolean enabled;
    private int priority;
    private Display display;
    private Format format;
    private Style style;

    private Map<TransitionType, BubbleTransition> transitions;
    private Map<Participants, RequirementGroup> requirements;

    @Getter
    @ToString
    @NoArgsConstructor
    @SuperBuilder(toBuilder = true)
    public static class Display {
        private int radius;
        private int maxLinesCount;
        private int maxLineWidth;
        private int maxWordLength;

        private double timePerSymbol;
        private double minimumTime;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @SuperBuilder(toBuilder = true)
    public static class Format {
        private String format;
        private String initialFormat;
        private String finalFormat;
        private String queuedFormat;
        private String separatorFormat;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @SuperBuilder(toBuilder = true)
    public static class Style {
        private AnimationType animation;
        private DisplayType display;
        private boolean transparency;
        private Vector3f scale;

        private BackgroundStyle background;
        private TextStyle text;

        @Getter
        @ToString
        @NoArgsConstructor
        @SuperBuilder(toBuilder = true)
        public static class BackgroundStyle {
            private String color;
            private int opacity;
        }

        @Getter
        @ToString
        @NoArgsConstructor
        @SuperBuilder(toBuilder = true)
        public static class TextStyle {
            private AlignmentType alignment;
            private boolean shadow;
        }
    }
}

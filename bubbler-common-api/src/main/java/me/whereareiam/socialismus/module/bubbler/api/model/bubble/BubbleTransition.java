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

	@Getter
	@ToString
	@NoArgsConstructor
	@SuperBuilder(toBuilder = true)
	public static class Sound {
		private String type;
		private float volume;
		private float pitch;
		private boolean fade;
	}
}

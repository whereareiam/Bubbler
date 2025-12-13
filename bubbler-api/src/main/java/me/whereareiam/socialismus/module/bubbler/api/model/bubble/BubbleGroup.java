package me.whereareiam.socialismus.module.bubbler.api.model.bubble;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
@AllArgsConstructor
public class BubbleGroup {
	private List<BubbleLine> lines;

	public long calculateDisplayTime() {
		return lines.stream().mapToLong(BubbleLine::getDisplayTime).sum();
	}
}

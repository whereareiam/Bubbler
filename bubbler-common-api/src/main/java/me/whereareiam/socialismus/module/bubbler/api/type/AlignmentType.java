package me.whereareiam.socialismus.module.bubbler.api.type;

import lombok.Getter;

@Getter
public enum AlignmentType {
	CENTER(0x00),
	LEFT(0x08),
	RIGHT(0x10);

	private final byte value;

	AlignmentType(int value) {
		this.value = (byte) value;
	}

}

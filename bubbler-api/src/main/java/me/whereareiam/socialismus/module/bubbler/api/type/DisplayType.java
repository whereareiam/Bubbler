package me.whereareiam.socialismus.module.bubbler.api.type;

import lombok.Getter;

@Getter
public enum DisplayType {
	FIXED(0x00),
	VERTICAL(0x01),
	HORIZONTAL(0x02),
	CENTER(0x03);

	private final byte value;

	DisplayType(int value) {
		this.value = (byte) value;
	}
}

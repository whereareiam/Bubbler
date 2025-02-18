package me.whereareiam.socialismus.module.bubbler.api.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DisplayType {
    FIXED(0), VERTICAL(1), HORIZONTAL(2), CENTER(3);

    private final int id;
}

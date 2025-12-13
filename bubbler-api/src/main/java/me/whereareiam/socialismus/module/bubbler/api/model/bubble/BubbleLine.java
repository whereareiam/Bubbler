package me.whereareiam.socialismus.module.bubbler.api.model.bubble;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import net.kyori.adventure.text.Component;

@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true)
public class BubbleLine {
  private Component content;
  private long displayTime;
}

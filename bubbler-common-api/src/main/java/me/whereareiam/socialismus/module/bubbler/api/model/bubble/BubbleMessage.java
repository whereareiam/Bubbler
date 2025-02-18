package me.whereareiam.socialismus.module.bubbler.api.model.bubble;

import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import net.kyori.adventure.text.Component;

@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true)
public class BubbleMessage {
  private final DummyPlayer sender;
  private Set<DummyPlayer> recipients;

  private Bubble bubble;
  private List<BubbleLine> lines;

  private Component content;
  private boolean cancelled;
}

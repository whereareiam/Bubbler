package me.whereareiam.socialismus.module.bubbler.api.model.bubble;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import net.kyori.adventure.text.Component;

import java.util.Queue;
import java.util.Set;

@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true)
public class BubbleMessage {
    private final DummyPlayer sender;
  private Set<DummyPlayer> recipients;

  private Bubble bubble;
  private Queue<BubbleGroup> groups;

  private Component content;
  private boolean cancelled;
}

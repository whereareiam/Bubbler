package me.whereareiam.socialismus.module.bubbler.api.model.config;

import lombok.Getter;
import lombok.ToString;
import me.whereareiam.commandant.model.CommandDefinition;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public class BubblerCommands {
    private Map<String, CommandDefinition> commands = new HashMap<>();
}

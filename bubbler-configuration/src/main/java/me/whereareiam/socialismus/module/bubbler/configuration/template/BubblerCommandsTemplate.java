package me.whereareiam.socialismus.module.bubbler.configuration.template;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.CommandEntity;
import me.whereareiam.socialismus.api.output.DefaultConfig;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerCommands;

import java.util.List;

@Singleton
public class BubblerCommandsTemplate implements DefaultConfig<BubblerCommands> {
    @Override
    public BubblerCommands getDefault() {
        BubblerCommands config = new BubblerCommands();

        // Default values
        CommandEntity bubble = CommandEntity.builder()
                .enabled(true)
                .aliases(List.of("bubble"))
                .permission("socialismus.admin")
                .description("Bubble message command")
                .usage("{command} {alias} <message>")
                .cooldown(CommandEntity.Cooldown.builder()
                        .enabled(true)
                        .duration(2)
                        .group("global")
                        .build()
                ).build();

        config.getCommands().put("bubble", bubble);

        return config;
    }
}

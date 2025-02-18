package me.whereareiam.socialismus.module.bubbler.command;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.registry.Registry;
import me.whereareiam.socialismus.api.model.CommandEntity;
import me.whereareiam.socialismus.api.output.command.CommandService;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerCommands;
import me.whereareiam.socialismus.module.bubbler.command.executor.BubbleCommand;

import java.util.Map;
import java.util.stream.Stream;

@Singleton
public class CommandRegistrar {
    private final Injector injector;
    private final CommandService commandService;
    private final Provider<BubblerCommands> commands;
    private final Registry<Map<String, CommandEntity>> commandRegistry;

    @Inject
    public CommandRegistrar(Injector injector, CommandService commandService, Provider<BubblerCommands> commands, Registry<Map<String, CommandEntity>> commandRegistry) {
        this.injector = injector;
        this.commandService = commandService;
        this.commands = commands;
        this.commandRegistry = commandRegistry;
    }

    public void registerCommands() {
        commandRegistry.register(commands.get().getCommands());

        Stream.of(
                injector.getInstance(BubbleCommand.class)
        ).forEach(commandService::registerCommand);
    }
}

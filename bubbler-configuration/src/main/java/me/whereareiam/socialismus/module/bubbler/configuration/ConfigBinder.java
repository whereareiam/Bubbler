package me.whereareiam.socialismus.module.bubbler.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerCommands;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerMessages;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;
import me.whereareiam.socialismus.module.bubbler.configuration.provider.BubblerCommandsProvider;
import me.whereareiam.socialismus.module.bubbler.configuration.provider.BubblerMessagesProvider;
import me.whereareiam.socialismus.module.bubbler.configuration.provider.BubblerSettingsProvider;
import me.whereareiam.socialismus.module.bubbler.configuration.provider.BubblesProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ConfigBinder extends AbstractModule {
    private final Path workingPath;

    public ConfigBinder(Path workingDirectory) {
        this.workingPath = workingDirectory;
    }

    @Override
    protected void configure() {
        bind(Path.class).annotatedWith(Names.named("workingPath")).toInstance(workingPath);
        bind(Path.class).annotatedWith(Names.named("bubblesPath")).toInstance(workingPath.resolve("bubbles"));
        createDirectories();

        bind(BubblerSettingsProvider.class).asEagerSingleton();
        bind(BubblerSettings.class).toProvider(BubblerSettingsProvider.class);

        bind(BubblerMessagesProvider.class).asEagerSingleton();
        bind(BubblerMessages.class).toProvider(BubblerMessagesProvider.class);

        bind(BubblerCommandsProvider.class).asEagerSingleton();
        bind(BubblerCommands.class).toProvider(BubblerCommandsProvider.class);

        bind(BubblesProvider.class).asEagerSingleton();
        bind(new TypeLiteral<List<Bubble>>() {}).toProvider(BubblesProvider.class);
    }

    private void createDirectories() {
        try {
            Files.createDirectories(workingPath);
            Files.createDirectories(workingPath.resolve("bubbles"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directories", e);
        }
    }
}

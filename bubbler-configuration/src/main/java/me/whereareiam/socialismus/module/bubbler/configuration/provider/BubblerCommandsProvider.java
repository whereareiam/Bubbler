package me.whereareiam.socialismus.module.bubbler.configuration.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.api.Reloadable;
import me.whereareiam.socialismus.api.input.registry.Registry;
import me.whereareiam.socialismus.api.output.config.ConfigurationLoader;
import me.whereareiam.socialismus.api.output.config.ConfigurationManager;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerCommands;
import me.whereareiam.socialismus.module.bubbler.configuration.template.BubblerCommandsTemplate;

import java.nio.file.Path;

@Singleton
public class BubblerCommandsProvider implements Provider<BubblerCommands>, Reloadable {
    private final Path workingPath;
    private final ConfigurationLoader configLoader;

    private BubblerCommands commands;

    @Inject
    public BubblerCommandsProvider(@Named("workingPath") Path workingPath, ConfigurationLoader configLoader, ConfigurationManager configManager,
                                   BubblerCommandsTemplate template, Registry<Reloadable> reloadableRegistry) {
        this.workingPath = workingPath;
        this.configLoader = configLoader;

        configManager.addTemplate(BubblerCommands.class, template);

        reloadableRegistry.register(this);
        get();
    }

    @Override
    public BubblerCommands get() {
        if (commands != null) return commands;

        load();

        return commands;
    }

    @Override
    public void reload() {
        load();
    }

    private void load() {
        commands = configLoader.load(workingPath.resolve("commands"), BubblerCommands.class);
    }
}

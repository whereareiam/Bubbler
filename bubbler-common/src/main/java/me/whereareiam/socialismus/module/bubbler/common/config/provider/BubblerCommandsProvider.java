package me.whereareiam.socialismus.module.bubbler.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerCommands;
import me.whereareiam.socialismus.module.bubbler.common.config.template.BubblerCommandsTemplate;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class BubblerCommandsProvider implements Provider<BubblerCommands>, Reloadable {
    private final Path workingPath;
    private BubblerCommands commands;

    @Inject
    public BubblerCommandsProvider(
            @Named("workingPath") Path workingPath,
            Registry<Reloadable> reloadableRegistry
    ) {
        this.workingPath = workingPath;

        Config.registerTemplate(BubblerCommandsTemplate.class);
        reloadableRegistry.register(this);
    }

    @Override
    public BubblerCommands get() {
        if (commands != null) return commands;
        commands = Config.update(workingPath.resolve("commands"), BubblerCommands.class);
        return commands;
    }

    @Override
    public void reload() {
        commands = Config.update(workingPath.resolve("commands"), BubblerCommands.class);
    }
}

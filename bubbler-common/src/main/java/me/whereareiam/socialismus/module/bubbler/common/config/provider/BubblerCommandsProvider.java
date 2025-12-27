package me.whereareiam.socialismus.module.bubbler.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerCommands;
import me.whereareiam.socialismus.module.bubbler.common.config.BubblerConfigProvider;
import me.whereareiam.socialismus.module.bubbler.common.config.template.BubblerCommandsTemplate;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class BubblerCommandsProvider extends BubblerConfigProvider<BubblerCommands> {
    @Inject
    public BubblerCommandsProvider(
            @Named("workingPath") Path workingPath,
            Registry<Reloadable> reloadableRegistry
    ) {
        super(workingPath, reloadableRegistry);
    }

    @Override
    protected BubblerCommands load() {
        return Config.update(getBasePath().resolve("commands"), BubblerCommands.class);
    }

    @Override
    protected void registerTemplate() {
        Config.registerTemplate(BubblerCommandsTemplate.class);
    }
}

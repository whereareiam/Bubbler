package me.whereareiam.socialismus.module.bubbler.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;
import me.whereareiam.socialismus.module.bubbler.common.config.template.BubblerSettingsTemplate;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class BubblerSettingsProvider implements Provider<BubblerSettings>, Reloadable {
    private final Path workingPath;
    private BubblerSettings settings;

    @Inject
    public BubblerSettingsProvider(
            @Named("workingPath") Path workingPath,
            Registry<Reloadable> reloadableRegistry
    ) {
        this.workingPath = workingPath;

        Config.registerTemplate(BubblerSettingsTemplate.class);
        reloadableRegistry.register(this);
    }

    @Override
    public BubblerSettings get() {
        if (settings != null) return settings;
        settings = Config.update(workingPath.resolve("settings"), BubblerSettings.class);
        return settings;
    }

    @Override
    public void reload() {
        settings = Config.update(workingPath.resolve("settings"), BubblerSettings.class);
    }
}

package me.whereareiam.socialismus.module.bubbler.configuration.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.api.Reloadable;
import me.whereareiam.socialismus.api.input.registry.Registry;
import me.whereareiam.socialismus.api.output.config.ConfigurationLoader;
import me.whereareiam.socialismus.api.output.config.ConfigurationManager;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;
import me.whereareiam.socialismus.module.bubbler.configuration.template.BubblerSettingsTemplate;

import java.nio.file.Path;

@Singleton
public class BubblerSettingsProvider implements Provider<BubblerSettings>, Reloadable {
    private final Path workingPath;
    private final ConfigurationLoader configLoader;

    private BubblerSettings settings;

    @Inject
    public BubblerSettingsProvider(@Named("workingPath") Path workingPath, ConfigurationLoader configLoader, ConfigurationManager configManager,
                                   BubblerSettingsTemplate template, Registry<Reloadable> reloadableRegistry) {
        this.workingPath = workingPath;
        this.configLoader = configLoader;

        configManager.addTemplate(BubblerSettings.class, template);

        reloadableRegistry.register(this);
        get();
    }

    @Override
    public BubblerSettings get() {
        if (settings != null) return settings;

        load();

        return settings;
    }

    @Override
    public void reload() {
        load();
    }

    private void load() {
        settings = configLoader.load(workingPath.resolve("settings"), BubblerSettings.class);
    }
}

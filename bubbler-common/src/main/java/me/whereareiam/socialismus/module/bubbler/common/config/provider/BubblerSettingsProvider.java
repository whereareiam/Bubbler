package me.whereareiam.socialismus.module.bubbler.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;
import me.whereareiam.socialismus.module.bubbler.common.config.BubblerConfigProvider;
import me.whereareiam.socialismus.module.bubbler.common.config.template.BubblerSettingsTemplate;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class BubblerSettingsProvider extends BubblerConfigProvider<BubblerSettings> {
    @Inject
    public BubblerSettingsProvider(
            @Named("workingPath") Path workingPath,
            Registry<Reloadable> reloadableRegistry
    ) {
        super(workingPath, reloadableRegistry);
    }

    @Override
    protected BubblerSettings load() {
        return Config.update(getBasePath().resolve("settings"), BubblerSettings.class);
    }

    @Override
    protected void registerTemplate() {
        Config.registerTemplate(BubblerSettingsTemplate.class);
    }
}

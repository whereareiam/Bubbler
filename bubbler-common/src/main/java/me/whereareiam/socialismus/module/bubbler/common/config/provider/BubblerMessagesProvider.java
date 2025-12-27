package me.whereareiam.socialismus.module.bubbler.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerMessages;
import me.whereareiam.socialismus.module.bubbler.common.config.BubblerConfigProvider;
import me.whereareiam.socialismus.module.bubbler.common.config.template.BubblerMessagesTemplate;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class BubblerMessagesProvider extends BubblerConfigProvider<BubblerMessages> {
    @Inject
    public BubblerMessagesProvider(
            @Named("workingPath") Path workingPath,
            Registry<Reloadable> reloadableRegistry
    ) {
        super(workingPath, reloadableRegistry);
    }

    @Override
    protected BubblerMessages load() {
        return Config.update(getBasePath().resolve("messages"), BubblerMessages.class);
    }

    @Override
    protected void registerTemplate() {
        Config.registerTemplate(BubblerMessagesTemplate.class);
    }
}

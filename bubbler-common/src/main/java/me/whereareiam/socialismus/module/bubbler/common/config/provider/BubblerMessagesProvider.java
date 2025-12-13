package me.whereareiam.socialismus.module.bubbler.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerMessages;
import me.whereareiam.socialismus.module.bubbler.common.config.template.BubblerMessagesTemplate;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class BubblerMessagesProvider implements Provider<BubblerMessages>, Reloadable {
    private final Path workingPath;
    private BubblerMessages messages;

    @Inject
    public BubblerMessagesProvider(
            @Named("workingPath") Path workingPath,
            Registry<Reloadable> reloadableRegistry
    ) {
        this.workingPath = workingPath;

        Config.registerTemplate(BubblerMessagesTemplate.class);
        reloadableRegistry.register(this);
    }

    @Override
    public BubblerMessages get() {
        if (messages != null) return messages;
        messages = Config.update(workingPath.resolve("messages"), BubblerMessages.class);
        return messages;
    }

    @Override
    public void reload() {
        messages = Config.update(workingPath.resolve("messages"), BubblerMessages.class);
    }
}

package me.whereareiam.socialismus.module.bubbler.configuration.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.api.Reloadable;
import me.whereareiam.socialismus.api.input.registry.Registry;
import me.whereareiam.socialismus.api.output.config.ConfigurationLoader;
import me.whereareiam.socialismus.api.output.config.ConfigurationManager;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerMessages;
import me.whereareiam.socialismus.module.bubbler.configuration.template.BubblerMessagesTemplate;

import java.nio.file.Path;

@Singleton
public class BubblerMessagesProvider implements Provider<BubblerMessages>, Reloadable {
    private final Path workingPath;
    private final ConfigurationLoader configLoader;

    private BubblerMessages messages;

    @Inject
    public BubblerMessagesProvider(@Named("workingPath") Path workingPath, ConfigurationLoader configLoader, ConfigurationManager configManager,
                                   BubblerMessagesTemplate template, Registry<Reloadable> reloadableRegistry) {
        this.workingPath = workingPath;
        this.configLoader = configLoader;

        configManager.addTemplate(BubblerMessages.class, template);

        reloadableRegistry.register(this);
        get();
    }

    @Override
    public BubblerMessages get() {
        if (messages != null) return messages;

        load();

        return messages;
    }

    @Override
    public void reload() {
        load();
    }

    private void load() {
        messages = configLoader.load(workingPath.resolve("messages"), BubblerMessages.class);
    }
}

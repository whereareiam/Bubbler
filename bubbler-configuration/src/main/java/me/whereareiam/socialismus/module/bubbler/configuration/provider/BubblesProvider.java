package me.whereareiam.socialismus.module.bubbler.configuration.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.api.Reloadable;
import me.whereareiam.socialismus.api.input.registry.Registry;
import me.whereareiam.socialismus.api.output.LoggingHelper;
import me.whereareiam.socialismus.api.output.config.ConfigurationLoader;
import me.whereareiam.socialismus.api.output.config.ConfigurationManager;
import me.whereareiam.socialismus.api.type.ConfigurationType;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.configuration.dynamic.BubblesConfig;
import me.whereareiam.socialismus.module.bubbler.configuration.template.BubbleTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Singleton
public class BubblesProvider implements Provider<List<Bubble>>, Reloadable {
    private final Path bubblesPath;
    private final LoggingHelper loggingHelper;
    private final ConfigurationLoader configLoader;
    private final ConfigurationType configurationType;

    private List<Bubble> bubbles;

    @Inject
    public BubblesProvider(@Named("bubblesPath") Path bubblesPath, LoggingHelper loggingHelper, ConfigurationLoader configLoader,
                           ConfigurationManager configManager, BubbleTemplate template, Registry<Reloadable> registry) {
        this.bubblesPath = bubblesPath;
        this.loggingHelper = loggingHelper;
        this.configLoader = configLoader;
        this.configurationType = configManager.getConfigurationType();

        configManager.addTemplate(BubblesConfig.class, template);

        registry.register(this);

        get();
    }

    @Override
    public List<Bubble> get() {
        if (bubbles != null) return bubbles;

        loadBubbles();

        return bubbles;
    }

    @Override
    public void reload() {
        loadBubbles();
    }

    private void loadBubbles() {
        bubbles = new ArrayList<>();
        try (Stream<Path> paths = Files.list(bubblesPath)) {
            paths.filter(path -> path.getFileName().toString().endsWith(configurationType.getExtension())).forEach(path -> {
                String fileName = path.getFileName().toString().replace(configurationType.getExtension(), "");

                if (Files.isDirectory(path) || fileName.isEmpty()) return;

                bubbles.addAll(addBubblesFromConfig(path.getParent().resolve(fileName)));
            });
        } catch (IOException e) {
            loggingHelper.severe("Failed to load bubbles configuration", e);
            bubbles = Collections.emptyList();
            return;
        }

        if (bubbles.isEmpty())
            bubbles.addAll(addBubblesFromConfig(bubblesPath.resolve("default")));

        bubbles.removeIf(bubble -> bubbles.stream().anyMatch(c -> c != bubble && c.getId().equals(bubble.getId())));
        bubbles.sort((bubble1, bubble2) -> Integer.compare(bubble2.getPriority(), bubble1.getPriority()));
    }

    private List<Bubble> addBubblesFromConfig(Path path) {
        BubblesConfig bubblesConfig = configLoader.load(path, BubblesConfig.class);
        return bubblesConfig.getBubbles().stream()
                .filter(Bubble::isEnabled)
                .toList();
    }
}
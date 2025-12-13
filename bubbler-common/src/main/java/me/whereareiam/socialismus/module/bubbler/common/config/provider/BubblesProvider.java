package me.whereareiam.socialismus.module.bubbler.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigurationTypeResolver;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.common.config.dynamic.BubblesConfig;
import me.whereareiam.socialismus.module.bubbler.common.config.template.BubblesConfigTemplate;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.type.ConfigurationType;

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
	private final ConfigurationType configurationType;

	private List<Bubble> bubbles;

	@Inject
	public BubblesProvider(
			@Named("bubblesPath") Path bubblesPath,
			ConfigurationTypeResolver typeResolver,
			Registry<Reloadable> registry
	) {
		this.bubblesPath = bubblesPath;
		this.configurationType = typeResolver.getConfigurationType();

		Config.registerTemplate(BubblesConfigTemplate.class);
		registry.register(this);
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
			paths.filter(Files::isRegularFile)
					.filter(path -> path.getFileName().toString().endsWith(configurationType.getExtension()))
					.forEach(path -> {
						String fileName = path.getFileName().toString();
						// Remove the configured extension
						fileName = fileName.substring(0, fileName.length() - configurationType.getExtension().length());

						if (fileName.isEmpty()) return;

						bubbles.addAll(addBubblesFromConfig(path.getParent().resolve(fileName)));
					});
		} catch (IOException e) {
			Logger.severe("Failed to load bubble configurations: " + e.getMessage());
			bubbles = Collections.emptyList();
			return;
		}

		if (bubbles.isEmpty())
			bubbles.addAll(addBubblesFromConfig(bubblesPath.resolve("default")));

		// Remove duplicates by ID
		bubbles.removeIf(bubble -> bubbles.stream()
				.anyMatch(c -> c != bubble && c.getId().equals(bubble.getId())));
	}

	private List<Bubble> addBubblesFromConfig(Path path) {
		BubblesConfig config = Config.update(path, BubblesConfig.class);
		return config.getBubbles().stream()
				.filter(Bubble::isEnabled)
				.toList();
	}
}
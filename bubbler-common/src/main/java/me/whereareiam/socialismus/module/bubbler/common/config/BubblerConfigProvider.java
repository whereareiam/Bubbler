package me.whereareiam.socialismus.module.bubbler.common.config;

import com.google.inject.Provider;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

/**
 * Adapter-layer base that wires {@link ConfigProvider} into our reload registry
 * and exposes the resolved base path for subclasses.
 */
public abstract class BubblerConfigProvider<T> extends ConfigProvider<T> implements Provider<T> {
	private final Path basePath;

	protected BubblerConfigProvider(Path basePath, Registry<Reloadable> reloadables) {
		this.basePath = basePath;
		reloadables.register(this);
	}

	protected Path getBasePath() {
		return basePath;
	}
}


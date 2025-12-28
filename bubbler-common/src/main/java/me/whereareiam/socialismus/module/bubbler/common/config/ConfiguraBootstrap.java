package me.whereareiam.socialismus.module.bubbler.common.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.module.bubbler.api.model.requirement.ActivatorRequirement;

/**
 * Registers Bubbler-specific polymorphic types with Configura.
 * This allows Bubbler's custom requirement types to be properly
 * deserialized from configuration files without modifying Socialismus core.
 */
@Singleton
public class ConfiguraBootstrap {
	@Inject
	public ConfiguraBootstrap() {
		// Register Bubbler's polymorphic requirement types
		Config.registerPolymorphic(Requirement.class)
				.inferByField("activators", ActivatorRequirement.class)
				.build();
	}
}

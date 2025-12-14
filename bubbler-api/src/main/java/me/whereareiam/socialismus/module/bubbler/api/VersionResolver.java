package me.whereareiam.socialismus.module.bubbler.api;

import me.whereareiam.socialismus.module.bubbler.api.model.packet.ProtocolVersion;
import me.whereareiam.socialismus.type.Version;

import java.util.Map;

/**
 * Utility for resolving version-specific values.
 * Helps packets find the correct index/type for the current protocol version.
 */
public final class VersionResolver {
	
	/**
	 * Resolves a value from a version map based on the current protocol version.
	 * Finds the highest version <= current version that has a mapping.
	 *
	 * @param versionMap Map of version to value
	 * @param defaultValue Value to return if no matching version found
	 * @return The resolved value for the current version
	 */
	public static <T> T resolve(Map<Version, T> versionMap, T defaultValue) {
		// Find the highest version <= current version that has a mapping
		Version bestVersion = ProtocolVersion.VERSION;
		while (bestVersion != null && !versionMap.containsKey(bestVersion)) {
			if (bestVersion.ordinal() == 0) return defaultValue;

			bestVersion = Version.values()[bestVersion.ordinal() - 1];
		}
		
		return bestVersion != null ? versionMap.get(bestVersion) : defaultValue;
	}
	
	/**
	 * Resolves a value from a version map, throwing if no mapping found.
	 */
	public static <T> T resolveOrThrow(Map<Version, T> versionMap) {
		T value = resolve(versionMap, null);
		if (value == null) {
			throw new IllegalStateException(
					"No mapping found for version " + ProtocolVersion.VERSION
			);
		}

		return value;
	}
}

package me.whereareiam.socialismus.module.bubbler.api;

import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.type.Version;

import java.util.Map;

/**
 * Utility for resolving version-specific values.
 * Helps packets find the correct index/type for the current protocol version.
 */
public final class VersionResolver {
	/**
	 * Returns the effective protocol version to resolve mappings against.
	 * <p>
	 * Servers newer than the host's known versions are reported as
	 * {@link Version#FUTURE}. Because {@code FUTURE} has a very low ordinal, a
	 * naive walk-down would treat such servers as the oldest possible version
	 * (or fail to resolve entirely). Instead we map {@code FUTURE} to the latest
	 * known version, so forward-compatible protocol layouts (e.g. display entity
	 * metadata, which is unchanged in newer releases) keep resolving correctly.
	 *
	 * @return the current server version, or the latest known version when the
	 *         server is newer than anything the host recognises
	 */
	public static Version current() {
		Version version = Constants.SERVER_VERSION;
		return version == Version.FUTURE ? Version.getLatest() : version;
	}

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
		Version bestVersion = current();
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
					"No mapping found for version " + Constants.SERVER_VERSION
			);
		}

		return value;
	}
}

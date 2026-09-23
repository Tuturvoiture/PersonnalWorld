package fr.galsaxx.invite;

import fr.galsaxx.PersonnalWorld;
import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * Helpers for personal-island dimension ids ({@code personnalworld:perso_<uuid>}).
 */
public final class IslandIds {
	public static final String PATH_PREFIX = "perso_";

	private IslandIds() {}

	public static String dimensionIdForPlayer(UUID playerUuid) {
		return PersonnalWorld.MOD_ID + ":" + PATH_PREFIX + playerUuid.toString();
	}

	public static Identifier identifierForPlayer(UUID playerUuid) {
		return Identifier.of(PersonnalWorld.MOD_ID, PATH_PREFIX + playerUuid.toString());
	}

	public static boolean isPersonalIsland(String dimensionId) {
		return dimensionId != null && dimensionId.startsWith(PersonnalWorld.MOD_ID + ":" + PATH_PREFIX);
	}

	public static Optional<UUID> creatorUuidFromDimensionId(String dimensionId) {
		if (!isPersonalIsland(dimensionId)) {
			return Optional.empty();
		}
		String path = dimensionId.substring((PersonnalWorld.MOD_ID + ":").length());
		if (!path.startsWith(PATH_PREFIX)) {
			return Optional.empty();
		}
		try {
			return Optional.of(UUID.fromString(path.substring(PATH_PREFIX.length())));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}

	/** File-safe key for access JSON (no colon). */
	public static String fileKey(String dimensionId) {
		return dimensionId.replace(':', '_').toLowerCase(Locale.ROOT);
	}

	public static String normalizeDimensionId(String raw) {
		if (raw == null) {
			return null;
		}
		String id = raw.trim();
		if (id.isEmpty()) {
			return null;
		}
		if (!id.contains(":")) {
			id = PersonnalWorld.MOD_ID + ":" + id;
		}
		return id;
	}
}

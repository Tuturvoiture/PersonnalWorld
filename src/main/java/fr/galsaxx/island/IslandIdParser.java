package fr.galsaxx.island;

import java.util.Optional;
import java.util.UUID;

/**
 * Parse des ids de dimension perso, sans Minecraft.
 * Slot 0 : {@code personnalworld:perso_<uuid>}.
 * Slot n ≥ 1 : {@code personnalworld:perso_<uuid>_<n>}.
 */
public final class IslandIdParser {
	public static final String NAMESPACE = "personnalworld";
	public static final String PATH_PREFIX = "perso_";
	public static final int UUID_LENGTH = 36;

	private IslandIdParser() {}

	public static String dimensionId(UUID owner, int slot) {
		String base = NAMESPACE + ":" + PATH_PREFIX + owner;
		if (slot <= 0) {
			return base;
		}
		return base + "_" + slot;
	}

	public static Optional<UUID> creatorUuid(String dimensionId) {
		String rest = suffixAfterPrefix(dimensionId);
		if (rest == null || rest.length() < UUID_LENGTH) {
			return Optional.empty();
		}
		String uuidPart = rest.substring(0, UUID_LENGTH);
		if (rest.length() > UUID_LENGTH && rest.charAt(UUID_LENGTH) != '_') {
			return Optional.empty();
		}
		try {
			return Optional.of(UUID.fromString(uuidPart));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}

	/** {@code -1} si l'id n'est pas une île perso. */
	public static int slotIndex(String dimensionId) {
		String rest = suffixAfterPrefix(dimensionId);
		if (rest == null || rest.length() < UUID_LENGTH) {
			return -1;
		}
		if (creatorUuid(dimensionId).isEmpty()) {
			return -1;
		}
		if (rest.length() == UUID_LENGTH) {
			return 0;
		}
		String tail = rest.substring(UUID_LENGTH + 1);
		if (tail.isEmpty()) {
			return -1;
		}
		try {
			int slot = Integer.parseInt(tail);
			return slot >= 1 ? slot : -1;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private static String suffixAfterPrefix(String dimensionId) {
		if (dimensionId == null) {
			return null;
		}
		String prefix = NAMESPACE + ":" + PATH_PREFIX;
		if (!dimensionId.startsWith(prefix)) {
			return null;
		}
		return dimensionId.substring(prefix.length());
	}
}

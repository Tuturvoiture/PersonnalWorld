package fr.galsaxx.invite;

import net.darchitect.access.DimensionRole;

/**
 * PersonnalWorld island roles.
 * <p>
 * {@link #BANNED} is mapped and recognized in schema/code but has <strong>no</strong>
 * player commands, UI, or PW enforcement in this release (future plan).
 */
public enum IslandRole {
	OWNER,
	CO_CREATOR,
	BUILDER,
	VISITOR,
	TEMP_VISITOR,
	/** Future: ban/unban — enum + DA mapping only for now. */
	BANNED;

	public boolean isPersistentWhitelist() {
		return this == CO_CREATOR || this == BUILDER || this == VISITOR || this == BANNED;
	}

	public boolean isAssignableByInviteCommand() {
		return this == CO_CREATOR || this == BUILDER || this == VISITOR;
	}

	public DimensionRole toDArchitect() {
		return switch (this) {
			case OWNER -> DimensionRole.OWNER;
			case CO_CREATOR -> DimensionRole.MODERATOR;
			case BUILDER -> DimensionRole.MEMBER;
			case VISITOR, TEMP_VISITOR -> DimensionRole.GUEST;
			case BANNED -> DimensionRole.BANNED;
		};
	}

	public static IslandRole fromDArchitect(DimensionRole role) {
		if (role == null) {
			return null;
		}
		return switch (role) {
			case OWNER -> OWNER;
			case MODERATOR -> CO_CREATOR;
			case MEMBER -> BUILDER;
			case GUEST -> VISITOR;
			case BANNED -> BANNED;
		};
	}

	public static IslandRole parseInviteRole(String raw) {
		if (raw == null) {
			return null;
		}
		String key = raw.trim().toLowerCase().replace('-', '_');
		return switch (key) {
			case "co_creator", "cocreator", "co-creator", "moderator" -> CO_CREATOR;
			case "builder", "member" -> BUILDER;
			case "visitor", "guest" -> VISITOR;
			default -> null;
		};
	}
}

package fr.galsaxx.invite;

import java.util.Objects;
import java.util.UUID;

/**
 * Snapshot of one island member — safe to send to UI / packets.
 */
public final class IslandMemberEntry {
	private final UUID uuid;
	private final String nameHint;
	private final IslandRole role;
	private final boolean temporary;
	private final String islandId;

	public IslandMemberEntry(UUID uuid, String nameHint, IslandRole role, boolean temporary, String islandId) {
		this.uuid = Objects.requireNonNull(uuid, "uuid");
		this.nameHint = nameHint == null ? "" : nameHint;
		this.role = Objects.requireNonNull(role, "role");
		this.temporary = temporary;
		this.islandId = islandId == null ? "" : islandId;
	}

	public UUID uuid() {
		return uuid;
	}

	public String nameHint() {
		return nameHint;
	}

	public IslandRole role() {
		return role;
	}

	public boolean temporary() {
		return temporary;
	}

	public String islandId() {
		return islandId;
	}
}

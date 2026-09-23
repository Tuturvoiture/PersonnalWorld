package fr.galsaxx.invite;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * In-memory access file for one island (PW source of truth).
 */
public final class AccessRecord {
	public static final int SCHEMA_VERSION = 1;

	private int schemaVersion = SCHEMA_VERSION;
	private long revision = 1L;
	private String dimensionId;
	private UUID ownerUuid;
	private String ownerNameHint = "";
	private String displayName = "";
	private boolean active = true;
	private boolean passive = false;
	private long updatedAtEpochMs;
	private final Map<UUID, Member> members = new LinkedHashMap<>();

	public AccessRecord() {}

	public AccessRecord copy() {
		AccessRecord c = new AccessRecord();
		c.schemaVersion = schemaVersion;
		c.revision = revision;
		c.dimensionId = dimensionId;
		c.ownerUuid = ownerUuid;
		c.ownerNameHint = ownerNameHint;
		c.displayName = displayName;
		c.active = active;
		c.passive = passive;
		c.updatedAtEpochMs = updatedAtEpochMs;
		for (Map.Entry<UUID, Member> e : members.entrySet()) {
			Member m = e.getValue();
			c.members.put(e.getKey(), new Member(m.uuid, m.role, m.nameHint));
		}
		return c;
	}

	public int schemaVersion() {
		return schemaVersion;
	}

	public void setSchemaVersion(int schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

	public long revision() {
		return revision;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}

	public void bumpRevision() {
		this.revision++;
		this.updatedAtEpochMs = System.currentTimeMillis();
	}

	public String dimensionId() {
		return dimensionId;
	}

	public void setDimensionId(String dimensionId) {
		this.dimensionId = dimensionId;
	}

	public UUID ownerUuid() {
		return ownerUuid;
	}

	public void setOwnerUuid(UUID ownerUuid) {
		this.ownerUuid = ownerUuid;
	}

	public String ownerNameHint() {
		return ownerNameHint;
	}

	public void setOwnerNameHint(String ownerNameHint) {
		this.ownerNameHint = ownerNameHint == null ? "" : ownerNameHint;
	}

	public String displayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName == null ? "" : displayName;
	}

	public boolean active() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean passive() {
		return passive;
	}

	public void setPassive(boolean passive) {
		this.passive = passive;
	}

	public long updatedAtEpochMs() {
		return updatedAtEpochMs;
	}

	public void setUpdatedAtEpochMs(long updatedAtEpochMs) {
		this.updatedAtEpochMs = updatedAtEpochMs;
	}

	public Map<UUID, Member> members() {
		return members;
	}

	public void putMember(UUID uuid, IslandRole role, String nameHint) {
		Objects.requireNonNull(uuid, "uuid");
		Objects.requireNonNull(role, "role");
		if (role == IslandRole.OWNER || role == IslandRole.TEMP_VISITOR) {
			throw new IllegalArgumentException("Cannot persist role " + role + " in members list");
		}
		members.put(uuid, new Member(uuid, role, nameHint == null ? "" : nameHint));
	}

	public void removeMember(UUID uuid) {
		members.remove(uuid);
	}

	public List<IslandMemberEntry> toMemberEntries(boolean includeOwner, boolean includeTemp, String islandId,
			Map<UUID, TempVisitorStore.Entry> temps) {
		List<IslandMemberEntry> out = new ArrayList<>();
		if (includeOwner && ownerUuid != null) {
			out.add(new IslandMemberEntry(ownerUuid, ownerNameHint, IslandRole.OWNER, false, islandId));
		}
		for (Member m : members.values()) {
			out.add(new IslandMemberEntry(m.uuid, m.nameHint, m.role, false, islandId));
		}
		if (includeTemp && temps != null) {
			for (TempVisitorStore.Entry t : temps.values()) {
				out.add(new IslandMemberEntry(t.uuid(), t.nameHint(), IslandRole.TEMP_VISITOR, true, islandId));
			}
		}
		return out;
	}

	public static final class Member {
		public final UUID uuid;
		public final IslandRole role;
		public final String nameHint;

		public Member(UUID uuid, IslandRole role, String nameHint) {
			this.uuid = uuid;
			this.role = role;
			this.nameHint = nameHint == null ? "" : nameHint;
		}
	}
}

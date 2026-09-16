package fr.galsaxx.invite;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import fr.galsaxx.PersonnalWorld;
import net.darchitect.access.DimensionRole;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JSON access files under {@code <world>/personnalworld/access/} — PW source of truth.
 */
public final class AccessFileStore {
	private static final AccessFileStore INSTANCE = new AccessFileStore();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final DateTimeFormatter QUARANTINE_TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

	private final Map<String, AccessRecord> cache = new ConcurrentHashMap<>();
	private Path accessRoot;

	private AccessFileStore() {}

	public static AccessFileStore get() {
		return INSTANCE;
	}

	public void bindServer(MinecraftServer server) {
		this.accessRoot = server.getSavePath(WorldSavePath.ROOT).resolve("personnalworld").resolve("access");
		try {
			Files.createDirectories(accessRoot);
			Files.createDirectories(corruptDir());
		} catch (IOException e) {
			PersonnalWorld.LOGGER.error("Cannot create access directory {}", accessRoot, e);
		}
	}

	public Path accessRoot() {
		return accessRoot;
	}

	private Path corruptDir() {
		return accessRoot.resolve("corrupt");
	}

	private Path fileFor(String dimensionId) {
		return accessRoot.resolve(IslandIds.fileKey(dimensionId) + ".json");
	}

	public Optional<AccessRecord> getCached(String dimensionId) {
		return Optional.ofNullable(cache.get(dimensionId));
	}

	/**
	 * Load from disk (or create/rebuild). Always validates. Never returns null.
	 */
	public AccessRecord loadOrRecover(String dimensionId, UUID fallbackOwner, String ownerNameHint) {
		if (accessRoot == null) {
			throw new IllegalStateException("AccessFileStore not bound to server");
		}
		AccessRecord cached = cache.get(dimensionId);
		if (cached != null) {
			return cached;
		}

		Path file = fileFor(dimensionId);
		if (Files.isRegularFile(file)) {
			try {
				String raw = Files.readString(file, StandardCharsets.UTF_8);
				AccessRecord parsed = parseJson(raw);
				AccessFileValidator.ValidationResult vr = AccessFileValidator.validate(parsed, dimensionId);
				if (vr.ok()) {
					cache.put(dimensionId, parsed);
					IslandDirectory.get().registerOrUpdate(parsed);
					return parsed;
				}
				quarantineAndLog(file, dimensionId, vr.summary());
			} catch (IOException | JsonParseException | IllegalArgumentException e) {
				quarantineAndLog(file, dimensionId, e.toString());
			}
		}

		AccessRecord rebuilt = rebuildFromDimension(dimensionId, fallbackOwner, ownerNameHint);
		writeAtomic(rebuilt);
		cache.put(dimensionId, rebuilt);
		IslandDirectory.get().registerOrUpdate(rebuilt);
		DArchitectAccess.applyRecord(rebuilt);
		return rebuilt;
	}

	/** Force re-read from disk (debug reload-access). */
	public AccessRecord reloadFromDisk(String dimensionId, UUID fallbackOwner, String ownerNameHint) {
		cache.remove(dimensionId);
		return loadOrRecover(dimensionId, fallbackOwner, ownerNameHint);
	}

	public AccessRecord ensureFresh(String dimensionId, UUID ownerUuid, String ownerNameHint) {
		return loadOrRecover(dimensionId, ownerUuid, ownerNameHint);
	}

	public synchronized void saveMutation(AccessRecord record) {
		record.bumpRevision();
		writeAtomic(record);
		cache.put(record.dimensionId(), record);
		IslandDirectory.get().registerOrUpdate(record);
		DArchitectAccess.applyRecord(record);
	}

	public void putInCache(AccessRecord record) {
		cache.put(record.dimensionId(), record);
		IslandDirectory.get().registerOrUpdate(record);
	}

	private void writeAtomic(AccessRecord record) {
		if (accessRoot == null) {
			PersonnalWorld.LOGGER.error("Cannot write access file: store not bound");
			return;
		}
		try {
			Files.createDirectories(accessRoot);
			Path target = fileFor(record.dimensionId());
			Path tmp = target.resolveSibling(target.getFileName().toString() + ".tmp");
			String json = toJson(record);
			Files.writeString(tmp, json, StandardCharsets.UTF_8);
			Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
		} catch (IOException e) {
			PersonnalWorld.LOGGER.error("Failed to write access file for {}", record.dimensionId(), e);
			try {
				Path target = fileFor(record.dimensionId());
				Path tmp = target.resolveSibling(target.getFileName().toString() + ".tmp");
				Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ignored) {
				// already logged
			}
		}
	}

	private void quarantineAndLog(Path file, String dimensionId, String reason) {
		PersonnalWorld.LOGGER.error(
				"[PersonnalWorld] Invalid access file for {} — reason: {} — quarantining {}",
				dimensionId, reason, file);
		try {
			Files.createDirectories(corruptDir());
			String stamp = LocalDateTime.now().format(QUARANTINE_TS);
			Path dest = corruptDir().resolve(IslandIds.fileKey(dimensionId) + "." + stamp + ".json");
			if (Files.exists(file)) {
				Files.move(file, dest, StandardCopyOption.REPLACE_EXISTING);
				PersonnalWorld.LOGGER.error("[PersonnalWorld] Quarantined access file → {}", dest);
			}
		} catch (IOException e) {
			PersonnalWorld.LOGGER.error("Failed to quarantine {}", file, e);
		}
	}

	private AccessRecord rebuildFromDimension(String dimensionId, UUID fallbackOwner, String ownerNameHint) {
		PersonnalWorld.LOGGER.warn("[PersonnalWorld] Rebuilding access file for {} from dimension roles / path UUID", dimensionId);
		AccessRecord record = new AccessRecord();
		record.setSchemaVersion(AccessRecord.SCHEMA_VERSION);
		record.setRevision(1L);
		record.setDimensionId(dimensionId);
		record.setUpdatedAtEpochMs(System.currentTimeMillis());
		record.setActive(true);
		record.setPassive(false);

		UUID owner = fallbackOwner;
		Map<UUID, DimensionRole> roles = DArchitectAccess.getRolesForDimension(dimensionId);
		for (Map.Entry<UUID, DimensionRole> e : roles.entrySet()) {
			if (e.getValue() == DimensionRole.OWNER) {
				owner = e.getKey();
				break;
			}
		}
		if (owner == null) {
			owner = IslandIds.creatorUuidFromDimensionId(dimensionId).orElse(null);
		}
		if (owner == null) {
			PersonnalWorld.LOGGER.error("[PersonnalWorld] Cannot determine owner for {}; using nil UUID", dimensionId);
			owner = new UUID(0L, 0L);
		}
		record.setOwnerUuid(owner);
		record.setOwnerNameHint(ownerNameHint == null ? "" : ownerNameHint);

		for (Map.Entry<UUID, DimensionRole> e : roles.entrySet()) {
			if (e.getKey().equals(owner)) {
				continue;
			}
			IslandRole pw = IslandRole.fromDArchitect(e.getValue());
			if (pw != null && pw.isPersistentWhitelist()) {
				record.putMember(e.getKey(), pw, "");
			}
		}
		return record;
	}

	public static AccessRecord createInitial(String dimensionId, UUID ownerUuid, String ownerNameHint) {
		AccessRecord record = new AccessRecord();
		record.setSchemaVersion(AccessRecord.SCHEMA_VERSION);
		record.setRevision(1L);
		record.setDimensionId(dimensionId);
		record.setOwnerUuid(ownerUuid);
		record.setOwnerNameHint(ownerNameHint == null ? "" : ownerNameHint);
		record.setDisplayName("");
		record.setActive(true);
		record.setPassive(false);
		record.setUpdatedAtEpochMs(System.currentTimeMillis());
		return record;
	}

	private static String toJson(AccessRecord record) {
		JsonObject root = new JsonObject();
		root.addProperty("schemaVersion", record.schemaVersion());
		root.addProperty("revision", record.revision());
		root.addProperty("dimensionId", record.dimensionId());
		root.addProperty("ownerUuid", record.ownerUuid().toString());
		root.addProperty("ownerNameHint", record.ownerNameHint());
		root.addProperty("displayName", record.displayName());
		root.addProperty("active", record.active());
		root.addProperty("passive", record.passive());
		root.addProperty("updatedAtEpochMs", record.updatedAtEpochMs());
		JsonArray members = new JsonArray();
		for (AccessRecord.Member m : record.members().values()) {
			JsonObject o = new JsonObject();
			o.addProperty("uuid", m.uuid.toString());
			o.addProperty("role", m.role.name());
			o.addProperty("nameHint", m.nameHint);
			members.add(o);
		}
		root.add("members", members);
		return GSON.toJson(root);
	}

	private static AccessRecord parseJson(String raw) {
		if (raw == null || raw.isBlank()) {
			throw new JsonParseException("empty file");
		}
		JsonObject root = JsonParser.parseString(raw).getAsJsonObject();
		AccessRecord record = new AccessRecord();
		if (!root.has("schemaVersion") || !root.has("revision") || !root.has("dimensionId") || !root.has("ownerUuid")) {
			throw new JsonParseException("missing required fields");
		}
		record.setSchemaVersion(root.get("schemaVersion").getAsInt());
		record.setRevision(root.get("revision").getAsLong());
		record.setDimensionId(root.get("dimensionId").getAsString());
		record.setOwnerUuid(UUID.fromString(root.get("ownerUuid").getAsString()));
		record.setOwnerNameHint(stringOrEmpty(root, "ownerNameHint"));
		record.setDisplayName(stringOrEmpty(root, "displayName"));
		record.setActive(!root.has("active") || root.get("active").getAsBoolean());
		record.setPassive(root.has("passive") && root.get("passive").getAsBoolean());
		record.setUpdatedAtEpochMs(root.has("updatedAtEpochMs") ? root.get("updatedAtEpochMs").getAsLong() : 0L);
		if (root.has("members")) {
			JsonArray arr = root.getAsJsonArray("members");
			for (JsonElement el : arr) {
				JsonObject o = el.getAsJsonObject();
				UUID uuid = UUID.fromString(o.get("uuid").getAsString());
				IslandRole role = AccessFileValidator.parsePersistedRole(o.get("role").getAsString());
				if (role == null) {
					throw new JsonParseException("invalid member role for " + uuid);
				}
				record.putMember(uuid, role, stringOrEmpty(o, "nameHint"));
			}
		}
		return record;
	}

	private static String stringOrEmpty(JsonObject o, String key) {
		return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsString() : "";
	}
}

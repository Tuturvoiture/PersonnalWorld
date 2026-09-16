package fr.galsaxx.invite;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ephemeral TEMP_VISITOR storage — never written to disk.
 */
public final class TempVisitorStore {
	private static final TempVisitorStore INSTANCE = new TempVisitorStore();

	private final Map<String, Map<UUID, Entry>> byDimension = new ConcurrentHashMap<>();

	private TempVisitorStore() {}

	public static TempVisitorStore get() {
		return INSTANCE;
	}

	public void put(String dimensionId, UUID uuid, String nameHint) {
		byDimension
				.computeIfAbsent(dimensionId, k -> new ConcurrentHashMap<>())
				.put(uuid, new Entry(uuid, nameHint == null ? "" : nameHint, System.currentTimeMillis()));
	}

	public boolean remove(String dimensionId, UUID uuid) {
		Map<UUID, Entry> map = byDimension.get(dimensionId);
		if (map == null) {
			return false;
		}
		return map.remove(uuid) != null;
	}

	public void clearDimension(String dimensionId) {
		byDimension.remove(dimensionId);
	}

	public void clearAll() {
		byDimension.clear();
	}

	public boolean isTemp(String dimensionId, UUID uuid) {
		Map<UUID, Entry> map = byDimension.get(dimensionId);
		return map != null && map.containsKey(uuid);
	}

	public Optional<Entry> get(String dimensionId, UUID uuid) {
		Map<UUID, Entry> map = byDimension.get(dimensionId);
		if (map == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(map.get(uuid));
	}

	public Map<UUID, Entry> view(String dimensionId) {
		Map<UUID, Entry> map = byDimension.get(dimensionId);
		return map == null ? Collections.emptyMap() : Collections.unmodifiableMap(map);
	}

	/** Remove player from every island temp list (disconnect). */
	public void removePlayerEverywhere(UUID uuid) {
		for (Map<UUID, Entry> map : byDimension.values()) {
			map.remove(uuid);
		}
	}

	public record Entry(UUID uuid, String nameHint, long grantedAtEpochMs) {}
}

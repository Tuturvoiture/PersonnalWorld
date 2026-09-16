package fr.galsaxx.invite;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lightweight multi-island directory (MVP: one active island per owner path).
 */
public final class IslandDirectory {
	private static final IslandDirectory INSTANCE = new IslandDirectory();

	private final Map<String, IslandMeta> byDimensionId = new ConcurrentHashMap<>();
	/** Owner UUID → currently active dimension id. */
	private final Map<UUID, String> activeByOwner = new ConcurrentHashMap<>();

	private IslandDirectory() {}

	public static IslandDirectory get() {
		return INSTANCE;
	}

	public void registerOrUpdate(AccessRecord record) {
		if (record == null || record.dimensionId() == null || record.ownerUuid() == null) {
			return;
		}
		IslandMeta meta = new IslandMeta(
				record.dimensionId(),
				record.ownerUuid(),
				record.displayName(),
				record.active(),
				record.passive());
		byDimensionId.put(record.dimensionId(), meta);
		if (record.active()) {
			activeByOwner.put(record.ownerUuid(), record.dimensionId());
		} else {
			activeByOwner.putIfAbsent(record.ownerUuid(), record.dimensionId());
		}
	}

	public Optional<IslandMeta> get(String dimensionId) {
		return Optional.ofNullable(byDimensionId.get(dimensionId));
	}

	public Collection<IslandMeta> all() {
		return byDimensionId.values();
	}

	public Optional<String> activeDimensionForOwner(UUID ownerUuid) {
		String dim = activeByOwner.get(ownerUuid);
		if (dim != null) {
			return Optional.of(dim);
		}
		return Optional.of(IslandIds.dimensionIdForPlayer(ownerUuid));
	}

	/**
	 * Resolve visit target: no name → active island; with name → match displayName (case-insensitive).
	 */
	public Optional<ResolveResult> resolveVisitTarget(UUID hostOwnerUuid, String optionalIslandName) {
		String active = activeDimensionForOwner(hostOwnerUuid).orElse(IslandIds.dimensionIdForPlayer(hostOwnerUuid));
		if (optionalIslandName == null || optionalIslandName.isBlank()) {
			IslandMeta meta = byDimensionId.getOrDefault(active,
					new IslandMeta(active, hostOwnerUuid, "", true, false));
			return Optional.of(new ResolveResult(meta.dimensionId(), meta.passive(), true));
		}
		String needle = optionalIslandName.trim().toLowerCase(Locale.ROOT);
		for (IslandMeta meta : byDimensionId.values()) {
			if (!hostOwnerUuid.equals(meta.ownerUuid())) {
				continue;
			}
			if (meta.displayName() != null && meta.displayName().toLowerCase(Locale.ROOT).equals(needle)) {
				boolean isActive = meta.dimensionId().equals(active);
				return Optional.of(new ResolveResult(meta.dimensionId(), meta.passive(), isActive));
			}
		}
		return Optional.empty();
	}

	public record IslandMeta(String dimensionId, UUID ownerUuid, String displayName, boolean active, boolean passive) {}

	public record ResolveResult(String dimensionId, boolean passive, boolean activeTarget) {}
}

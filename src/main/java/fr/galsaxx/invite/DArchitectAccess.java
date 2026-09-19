package fr.galsaxx.invite;

import fr.galsaxx.PersonnalWorld;
import net.darchitect.access.DimensionAccessManager;
import net.darchitect.access.DimensionPermission;
import net.darchitect.access.DimensionRole;
import net.darchitect.api.AccessMode;
import net.darchitect.api.DimensionArchitectRuntime;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Wrapper autour de l’API d’accès publique DimensionArchitect ({@code access()} depuis 0.1.2).
 * <p>
 * PersonnalWorld reste source de vérité (JSON) ; DA est l’enforcer runtime.
 * {@link #applyRecord} remplace <strong>toute</strong> la carte de rôles (owner + members persistants),
 * sans TEMP — les visiteurs temporaires utilisent {@link #grantTempGuest} / {@link #clearRole}.
 */
public final class DArchitectAccess {
	private DArchitectAccess() {}

	public static DimensionAccessManager manager() {
		return DimensionArchitectRuntime.get().access();
	}

	public static void setRole(String dimensionId, UUID player, DimensionRole role) {
		manager().setRole(dimensionId, player, role);
	}

	public static void clearRole(String dimensionId, UUID player) {
		manager().clearRole(dimensionId, player);
	}

	public static Optional<DimensionRole> getRole(String dimensionId, UUID player) {
		return manager().getRole(dimensionId, player);
	}

	public static boolean hasPermission(String dimensionId, UUID player, DimensionPermission permission) {
		return manager().hasPermission(dimensionId, player, permission);
	}

	public static Map<UUID, DimensionRole> getRolesForDimension(String dimensionId) {
		try {
			Map<UUID, DimensionRole> map = manager().getRolesForDimension(dimensionId);
			return map == null ? Collections.emptyMap() : map;
		} catch (RuntimeException e) {
			PersonnalWorld.LOGGER.warn("Failed to read DA roles for {}: {}", dimensionId, e.toString());
			return Collections.emptyMap();
		}
	}

	/**
	 * Remplacement atomique : OWNER + membres whitelist uniquement (pas de TEMP).
	 * Les UUID absents de la map perdent leur rôle DA (plus de build fantôme après kick).
	 */
	public static void applyRecord(AccessRecord record) {
		if (record == null || record.dimensionId() == null || record.ownerUuid() == null) {
			return;
		}
		String dim = record.dimensionId();
		Map<UUID, DimensionRole> roles = new LinkedHashMap<>();
		roles.put(record.ownerUuid(), DimensionRole.OWNER);
		for (AccessRecord.Member m : record.members().values()) {
			if (m.uuid.equals(record.ownerUuid())) {
				continue;
			}
			roles.put(m.uuid, m.role.toDArchitect());
		}
		manager().setRolesForDimension(dim, roles);
	}

	/** GUEST temporaire pour une visite — à retirer via {@link #clearRole} au leave. */
	public static void grantTempGuest(String dimensionId, UUID player) {
		setRole(dimensionId, player, DimensionRole.GUEST);
	}

	/** Migration soft des dims legacy OPEN → MANAGED + owner logique. */
	public static void migrateManaged(String dimensionId, UUID ownerUuid) {
		if (dimensionId == null || ownerUuid == null) {
			return;
		}
		try {
			manager().setAccessMode(dimensionId, AccessMode.MANAGED);
			manager().setOwner(dimensionId, ownerUuid);
		} catch (RuntimeException e) {
			PersonnalWorld.LOGGER.warn("Failed to migrate access mode for {}: {}", dimensionId, e.toString());
		}
	}
}

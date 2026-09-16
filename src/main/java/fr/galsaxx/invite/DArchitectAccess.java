package fr.galsaxx.invite;

import fr.galsaxx.PersonnalWorld;
import net.darchitect.access.DimensionAccessManager;
import net.darchitect.access.DimensionPermission;
import net.darchitect.access.DimensionRole;
import net.darchitect.impl.AccessManagerImpl;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Thin wrapper around DArchitect access runtime.
 * <p>
 * Uses {@link AccessManagerImpl#INSTANCE} until DimensionArchitect exposes a public {@code access()} API.
 */
public final class DArchitectAccess {
	private DArchitectAccess() {}

	public static DimensionAccessManager manager() {
		return AccessManagerImpl.INSTANCE;
	}

	public static void setRole(String dimensionId, UUID player, DimensionRole role) {
		manager().setRole(dimensionId, player, role);
	}

	public static Optional<DimensionRole> getRole(String dimensionId, UUID player) {
		return manager().getRole(dimensionId, player);
	}

	public static boolean hasPermission(String dimensionId, UUID player, DimensionPermission permission) {
		return manager().hasPermission(dimensionId, player, permission);
	}

	public static Map<UUID, DimensionRole> getRolesForDimension(String dimensionId) {
		try {
			Map<UUID, DimensionRole> map = AccessManagerImpl.INSTANCE.getRolesForDimension(dimensionId);
			return map == null ? Collections.emptyMap() : map;
		} catch (RuntimeException e) {
			PersonnalWorld.LOGGER.warn("Failed to read DA roles for {}: {}", dimensionId, e.toString());
			return Collections.emptyMap();
		}
	}

	public static void applyRecord(AccessRecord record) {
		if (record == null || record.dimensionId() == null || record.ownerUuid() == null) {
			return;
		}
		String dim = record.dimensionId();
		setRole(dim, record.ownerUuid(), DimensionRole.OWNER);
		for (AccessRecord.Member m : record.members().values()) {
			setRole(dim, m.uuid, m.role.toDArchitect());
		}
	}
}

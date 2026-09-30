package fr.galsaxx.invite;

import net.darchitect.access.DimensionPermission;
import net.darchitect.api.DimensionArchitectRuntime;
import net.darchitect.api.DimensionHandle;
import net.darchitect.api.ext.AccessPolicyProvider;

import java.util.UUID;

/**
 * DimensionArchitect donne au rôle GUEST une matrice vide : pas de JOIN.
 * Un visiteur PersonnalWorld doit pouvoir entrer, sans BUILD ni INTERACT.
 * Les îles déjà créées gardent cette matrice ; ce fournisseur ouvre seulement JOIN.
 */
public final class IslandVisitorAccess implements AccessPolicyProvider {
	@Override
	public boolean check(UUID player, DimensionHandle dimension, DimensionPermission permission) {
		if (dimension == null || player == null || permission == null) {
			return false;
		}
		String dimensionId = asPersonalId(dimension.getId());
		if (dimensionId != null
				&& permission == DimensionPermission.JOIN
				&& isVisitor(dimensionId, player)) {
			return true;
		}
		if (DimensionArchitectRuntime.getOrNull() == null) {
			return true;
		}
		return DimensionArchitectRuntime.get().access().hasPermission(dimension.getId(), player, permission);
	}

	private static boolean isVisitor(String dimensionId, UUID player) {
		AccessRecord record = AccessFileStore.get().getCached(dimensionId).orElse(null);
		if (record == null) {
			return false;
		}
		AccessRecord.Member member = record.members().get(player);
		return member != null && member.role == IslandRole.VISITOR;
	}

	/** {@code personnalworld:perso_…} ou l’id de stockage {@code personnalworld_perso_…}. */
	private static String asPersonalId(String raw) {
		if (raw == null) {
			return null;
		}
		if (IslandIds.isPersonalIsland(raw)) {
			return raw;
		}
		String prefix = "personnalworld_perso_";
		if (raw.startsWith(prefix)) {
			return "personnalworld:" + raw.substring("personnalworld_".length());
		}
		return null;
	}
}

package fr.galsaxx.invite;

import fr.galsaxx.PersonnalWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

/**
 * Debug/ops ownership transfer — logical owner only (dimension path unchanged).
 */
public final class TransferOwnershipService {
	private TransferOwnershipService() {}

	public static IslandAccessService.Result transfer(
			MinecraftServer server,
			String dimensionId,
			UUID newOwnerUuid,
			String newOwnerNameHint) {
		dimensionId = IslandIds.normalizeDimensionId(dimensionId);
		if (dimensionId == null || !IslandIds.isPersonalIsland(dimensionId)) {
			return IslandAccessService.Result.fail("message.personnalworld.pw.debug_bad_target");
		}
		UUID fallback = IslandIds.creatorUuidFromDimensionId(dimensionId).orElse(newOwnerUuid);
		AccessRecord record = AccessFileStore.get().loadOrRecover(dimensionId, fallback, "");
		UUID previous = record.ownerUuid();
		if (previous.equals(newOwnerUuid)) {
			return IslandAccessService.Result.fail("message.personnalworld.pw.debug_same_owner");
		}

		String previousName = record.ownerNameHint();
		record.members().remove(newOwnerUuid);
		record.setOwnerUuid(newOwnerUuid);
		record.setOwnerNameHint(newOwnerNameHint == null ? "" : newOwnerNameHint);
		record.putMember(previous, IslandRole.CO_CREATOR, previousName);
		TempVisitorStore.get().remove(dimensionId, newOwnerUuid);
		TempVisitorStore.get().remove(dimensionId, previous);
		AccessFileStore.get().saveMutation(record);

		PersonnalWorld.LOGGER.info(
				"[PersonnalWorld] ownership of {} transferred {} → {}",
				dimensionId, previous, newOwnerUuid);
		return IslandAccessService.Result.ok(
				"message.personnalworld.pw.debug_setowner_ok",
				dimensionId,
				newOwnerNameHint == null ? newOwnerUuid.toString() : newOwnerNameHint);
	}

	public static IslandAccessService.Result transferToPlayer(
			MinecraftServer server,
			String targetDimensionOrOwner,
			ServerPlayerEntity newOwner) {
		String dim = resolveTargetDimension(server, targetDimensionOrOwner);
		if (dim == null) {
			return IslandAccessService.Result.fail("message.personnalworld.pw.debug_bad_target");
		}
		return transfer(server, dim, newOwner.getUuid(), newOwner.getGameProfile().getName());
	}

	public static String resolveTargetDimension(MinecraftServer server, String raw) {
		if (raw == null || raw.isBlank()) {
			return null;
		}
		String normalized = IslandIds.normalizeDimensionId(raw);
		if (normalized != null && IslandIds.isPersonalIsland(normalized)) {
			return normalized;
		}
		ServerPlayerEntity online = server.getPlayerManager().getPlayer(raw);
		if (online != null) {
			return IslandDirectory.get().activeDimensionForOwner(online.getUuid())
					.orElse(IslandIds.dimensionIdForPlayer(online.getUuid()));
		}
		// Treat as player name of offline — cannot resolve UUID easily without user cache;
		// try path perso_<raw> only if raw is UUID.
		try {
			UUID uuid = UUID.fromString(raw);
			return IslandIds.dimensionIdForPlayer(uuid);
		} catch (IllegalArgumentException ignored) {
			return normalized != null && IslandIds.isPersonalIsland(normalized) ? normalized : null;
		}
	}
}

package fr.galsaxx.invite;

import fr.galsaxx.PersonnalWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Ops reload helpers for access files and islands.
 */
public final class IslandReloadService {
	private IslandReloadService() {}

	public static IslandAccessService.Result reloadAccess(MinecraftServer server, String rawTarget) {
		AccessFileStore.get().bindServer(server);
		if (rawTarget == null || rawTarget.isBlank()) {
			int count = 0;
			for (IslandDirectory.IslandMeta meta : IslandDirectory.get().all()) {
				reloadOne(server, meta.dimensionId());
				count++;
			}
			if (count == 0) {
				return IslandAccessService.Result.ok("message.personnalworld.pw.debug_reload_access_none");
			}
			return IslandAccessService.Result.ok("message.personnalworld.pw.debug_reload_access_all", count);
		}
		String dim = TransferOwnershipService.resolveTargetDimension(server, rawTarget);
		if (dim == null) {
			return IslandAccessService.Result.fail("message.personnalworld.pw.debug_bad_target");
		}
		reloadOne(server, dim);
		return IslandAccessService.Result.ok("message.personnalworld.pw.debug_reload_access_ok", dim);
	}

	private static void reloadOne(MinecraftServer server, String dimensionId) {
		var owner = IslandIds.creatorUuidFromDimensionId(dimensionId).orElse(null);
		AccessRecord record = AccessFileStore.get().reloadFromDisk(dimensionId, owner, "");
		DArchitectAccess.applyRecord(record);
		IslandAccessService.get().evictAllWithoutJoin(server, dimensionId);
	}

	/**
	 * Evict everyone, purge temps, resync access. Full dim unload via DA is best-effort
	 * (public API limited) — falls back to kick + access reload + admin advice.
	 */
	public static IslandAccessService.Result reloadIsland(MinecraftServer server, ServerCommandSource source, String rawTarget) {
		String dim = TransferOwnershipService.resolveTargetDimension(server, rawTarget);
		if (dim == null) {
			return IslandAccessService.Result.fail("message.personnalworld.pw.debug_bad_target");
		}
		IslandAccessService.get().evictEveryone(server, dim);
		IslandAccessService.get().purgeTempsForDimension(dim);
		reloadOne(server, dim);

		boolean unloaded = tryUnloadViaCommand(server, source, dim);
		if (unloaded) {
			PersonnalWorld.LOGGER.info("[PersonnalWorld] reload-island {} — unload command dispatched", dim);
			return IslandAccessService.Result.ok("message.personnalworld.pw.debug_reload_island_ok", dim);
		}
		PersonnalWorld.LOGGER.warn(
				"[PersonnalWorld] reload-island {}: players evicted and access resynced; dimension unload API not available — restart recommended if chunks stay loaded",
				dim);
		return IslandAccessService.Result.ok("message.personnalworld.pw.debug_reload_island_partial", dim);
	}

	private static boolean tryUnloadViaCommand(MinecraftServer server, ServerCommandSource source, String dim) {
		String[] candidates = {
				"darchitect unload " + dim,
				"da unload " + dim,
				"dimensionarchitect unload " + dim
		};
		for (String cmd : candidates) {
			try {
				int result = server.getCommandManager().getDispatcher().execute(cmd, source);
				if (result >= 0) {
					return true;
				}
			} catch (Exception ignored) {
				// try next
			}
		}
		return false;
	}
}

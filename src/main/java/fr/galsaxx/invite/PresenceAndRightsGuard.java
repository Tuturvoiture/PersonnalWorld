package fr.galsaxx.invite;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import fr.galsaxx.PersonnalWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ensures players without JOIN leave personal islands (incl. after restart / lost TEMP).
 */
public final class PresenceAndRightsGuard {
	private static final int CHECK_INTERVAL_TICKS = 40;
	private static final Map<UUID, String> LAST_DIM = new ConcurrentHashMap<>();
	private static int tickCounter;

	private PresenceAndRightsGuard() {}

	public static void register() {
		PlayerEvent.PLAYER_QUIT.register(PresenceAndRightsGuard::onQuit);
		TickEvent.SERVER_POST.register(PresenceAndRightsGuard::onServerTick);
		PersonnalWorld.LOGGER.info("PresenceAndRightsGuard registered");
	}

	public static void onServerStarting(MinecraftServer server) {
		AccessFileStore.get().bindServer(server);
		TempVisitorStore.get().clearAll();
		// TEMP never persists; re-apply whitelist-only roles to drop orphan DA GUESTs.
		for (IslandDirectory.IslandMeta meta : IslandDirectory.get().all()) {
			AccessFileStore.get().getCached(meta.dimensionId()).ifPresent(DArchitectAccess::applyRecord);
		}
	}

	private static void onQuit(ServerPlayerEntity player) {
		IslandAccessService.get().onPlayerDisconnect(player);
		LAST_DIM.remove(player.getUuid());
	}

	private static void onServerTick(MinecraftServer server) {
		tickCounter++;
		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			String current = player.getServerWorld().getRegistryKey().getValue().toString();
			String previous = LAST_DIM.put(player.getUuid(), current);
			if (previous != null && !previous.equals(current)) {
				IslandAccessService.get().onPlayerLeaveDimension(player, previous);
				if (IslandIds.isPersonalIsland(previous)) {
					// Leaving an island drops TEMP for that island.
				}
			}
		}
		if (tickCounter % CHECK_INTERVAL_TICKS != 0) {
			return;
		}
		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			String dim = player.getServerWorld().getRegistryKey().getValue().toString();
			if (!IslandIds.isPersonalIsland(dim)) {
				continue;
			}
			UUID ownerFallback = IslandIds.creatorUuidFromDimensionId(dim).orElse(null);
			AccessRecord record = AccessFileStore.get().ensureFresh(dim, ownerFallback, "");
			if (!IslandAccessService.get().hasJoin(record, player.getUuid())) {
				player.sendMessage(Text.translatable("message.personnalworld.pw.evicted"), false);
				fr.galsaxx.util.ReturnTeleport.teleportHome(player);
			}
		}
	}
}

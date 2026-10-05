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
	private static boolean rolesApplied;

	private PresenceAndRightsGuard() {}

	public static void register() {
		PlayerEvent.PLAYER_QUIT.register(PresenceAndRightsGuard::onQuit);
		PlayerEvent.PLAYER_JOIN.register(PresenceAndRightsGuard::onJoin);
		TickEvent.SERVER_POST.register(PresenceAndRightsGuard::onServerTick);
		PersonnalWorld.LOGGER.info("PresenceAndRightsGuard registered");
	}

	public static void onServerStarting(MinecraftServer server) {
		rolesApplied = false;
		AccessFileStore.get().bindServer(server);
		AccessFileStore.get().warmDirectory();
		TempVisitorStore.get().clearAll();
	}

	private static void onJoin(ServerPlayerEntity player) {
		MinecraftServer server = player.getServer();
		if (server == null) {
			return;
		}
		// After vanilla places the player (dim may have been auto-loaded from last logout).
		server.execute(() -> redirectHomeIfCannotStay(player));
	}

	private static void onQuit(ServerPlayerEntity player) {
		IslandAccessService.get().onPlayerDisconnect(player);
		LAST_DIM.remove(player.getUuid());
	}

	/**
	 * Île perso inactive ou sans JOIN : renvoi à la position sauvegardée (login / dim déchargée puis rechargée).
	 */
	static boolean redirectHomeIfCannotStay(ServerPlayerEntity player) {
		if (player == null || player.isRemoved() || player.getServer() == null) {
			return false;
		}
		String dim = player.getServerWorld().getRegistryKey().getValue().toString();
		if (!IslandIds.isPersonalIsland(dim)) {
			return false;
		}
		AccessFileStore.get().bindServer(player.getServer());
		UUID ownerFallback = IslandIds.creatorUuidFromDimensionId(dim).orElse(null);
		AccessRecord record = AccessFileStore.get().ensureFresh(dim, ownerFallback, "");
		if (!record.active()) {
			player.sendMessage(Text.translatable("message.personnalworld.pw.island_not_activated"), false);
			fr.galsaxx.util.ReturnTeleport.forgetIfDimension(player, dim);
			fr.galsaxx.util.ReturnTeleport.teleportHome(player);
			LAST_DIM.put(player.getUuid(), player.getServerWorld().getRegistryKey().getValue().toString());
			return true;
		}
		if (!IslandAccessService.get().hasJoin(record, player.getUuid())) {
			player.sendMessage(Text.translatable("message.personnalworld.pw.evicted"), false);
			fr.galsaxx.util.ReturnTeleport.forgetIfDimension(player, dim);
			fr.galsaxx.util.ReturnTeleport.teleportHome(player);
			LAST_DIM.put(player.getUuid(), player.getServerWorld().getRegistryKey().getValue().toString());
			return true;
		}
		return false;
	}

	private static void onServerTick(MinecraftServer server) {
		if (!rolesApplied && DArchitectAccess.isReady()) {
			rolesApplied = true;
			for (IslandDirectory.IslandMeta meta : IslandDirectory.get().all()) {
				AccessFileStore.get().getCached(meta.dimensionId()).ifPresent(DArchitectAccess::applyRecord);
			}
		}
		tickCounter++;
		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			String current = player.getServerWorld().getRegistryKey().getValue().toString();
			String previous = LAST_DIM.put(player.getUuid(), current);
			if (previous != null && !previous.equals(current)) {
				IslandAccessService.get().onPlayerLeaveDimension(player, previous);
			}
		}
		if (tickCounter % CHECK_INTERVAL_TICKS != 0) {
			return;
		}
		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			redirectHomeIfCannotStay(player);
		}
	}
}

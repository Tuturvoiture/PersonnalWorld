package fr.galsaxx.invite;

import fr.galsaxx.network.SyncIslandMembersPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.UUID;

/**
 * Façade stable serveur pour commandes <strong>et</strong> future UI livre.
 * <p>
 * Toute mutation d’accès doit passer ici (ou via les mêmes méthodes) afin que
 * le sync S2C {@link SyncIslandMembersPayload} reste cohérent.
 * Les handlers C2S du livre appelleront ces méthodes ; pas besoin de dupliquer
 * la logique dans {@link IslandAccessService}.
 */
public final class IslandMembersApi {
	private IslandMembersApi() {}

	// —— Lecture (UI) ——

	public static List<IslandMemberEntry> listMembers(MinecraftServer server, UUID ownerUuid) {
		AccessFileStore.get().bindServer(server);
		String dim = IslandDirectory.get().activeDimensionForOwner(ownerUuid)
				.orElse(IslandIds.dimensionIdForPlayer(ownerUuid));
		AccessRecord record = AccessFileStore.get().ensureFresh(dim, ownerUuid, "");
		return record.toMemberEntries(true, true, dim, TempVisitorStore.get().view(dim));
	}

	public static List<IslandMemberEntry> listMembersByDimension(MinecraftServer server, String dimensionId) {
		AccessFileStore.get().bindServer(server);
		UUID owner = IslandIds.creatorUuidFromDimensionId(dimensionId).orElse(null);
		AccessRecord record = AccessFileStore.get().ensureFresh(dimensionId, owner, "");
		return record.toMemberEntries(true, true, dimensionId, TempVisitorStore.get().view(dimensionId));
	}

	public static List<IslandMemberEntry> listMembersForActor(MinecraftServer server, ServerPlayerEntity actor) {
		return IslandAccessService.get().listMembers(server, actor);
	}

	public static boolean canManageMembers(MinecraftServer server, ServerPlayerEntity actor) {
		String dim = resolveActorManageDim(server, actor);
		AccessRecord record = IslandAccessService.get().ensureIslandAccess(server, dim, actor);
		return IslandAccessService.get().canManageMembers(record, actor.getUuid());
	}

	public static IslandRole effectiveRole(MinecraftServer server, String dimensionId, UUID playerUuid) {
		UUID owner = IslandIds.creatorUuidFromDimensionId(dimensionId).orElse(null);
		AccessRecord record = AccessFileStore.get().ensureFresh(dimensionId, owner, "");
		return IslandAccessService.get().effectiveRole(record, playerUuid);
	}

	// —— Mutations (commandes + UI) ——

	public static IslandAccessService.Result invite(
			MinecraftServer server, ServerPlayerEntity actor, PlayerRef target, IslandRole role) {
		IslandAccessService.Result r = IslandAccessService.get().invite(server, actor, target, role);
		if (r.ok()) {
			notifyWatchers(server, managedDimOf(server, actor));
		}
		return r;
	}

	public static IslandAccessService.Result kick(
			MinecraftServer server, ServerPlayerEntity actor, PlayerRef target) {
		String dim = managedDimOf(server, actor);
		IslandAccessService.Result r = IslandAccessService.get().kick(server, actor, target);
		if (r.ok()) {
			notifyWatchers(server, dim);
		}
		return r;
	}

	public static IslandAccessService.Result setRole(
			MinecraftServer server, ServerPlayerEntity actor, PlayerRef target, IslandRole role) {
		IslandAccessService.Result r = IslandAccessService.get().setRole(server, actor, target, role);
		if (r.ok()) {
			notifyWatchers(server, managedDimOf(server, actor));
		}
		return r;
	}

	public static IslandAccessService.Result visit(
			MinecraftServer server, ServerPlayerEntity visitor, PlayerRef host, String optionalIslandName) {
		IslandAccessService.Result r = IslandAccessService.get().visit(server, visitor, host, optionalIslandName);
		if (r.ok()) {
			String dim = IslandDirectory.get().resolveVisitTarget(host.uuid(), optionalIslandName)
					.map(IslandDirectory.ResolveResult::dimensionId)
					.orElse(IslandIds.dimensionIdForPlayer(host.uuid()));
			notifyWatchers(server, dim);
			syncToClient(visitor, dim);
		}
		return r;
	}

	public static IslandAccessService.Result leave(ServerPlayerEntity player) {
		String dim = player.getServerWorld().getRegistryKey().getValue().toString();
		IslandAccessService.Result r = IslandAccessService.get().leave(player);
		if (r.ok() && IslandIds.isPersonalIsland(dim) && player.getServer() != null) {
			notifyWatchers(player.getServer(), dim);
		}
		return r;
	}

	// —— Sync S2C ——

	/** Push member list to one client (livre ouvert / écran permissions). */
	public static void syncToClient(ServerPlayerEntity player, String dimensionId) {
		MinecraftServer server = player.getServer();
		if (server == null || dimensionId == null) {
			return;
		}
		List<IslandMemberEntry> members = listMembersByDimension(server, dimensionId);
		SyncIslandMembersPayload.send(player, dimensionId, members);
	}

	/**
	 * Notifie owner, co-créateurs online et joueurs présents dans la dim.
	 * Appelé après chaque mutation réussie.
	 */
	public static void notifyWatchers(MinecraftServer server, String dimensionId) {
		if (server == null || dimensionId == null || !IslandIds.isPersonalIsland(dimensionId)) {
			return;
		}
		UUID ownerFallback = IslandIds.creatorUuidFromDimensionId(dimensionId).orElse(null);
		AccessRecord record = AccessFileStore.get().ensureFresh(dimensionId, ownerFallback, "");
		List<IslandMemberEntry> members = record.toMemberEntries(
				true, true, dimensionId, TempVisitorStore.get().view(dimensionId));

		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			UUID id = player.getUuid();
			boolean interested = id.equals(record.ownerUuid())
					|| isCoCreator(record, id)
					|| dimensionId.equals(player.getServerWorld().getRegistryKey().getValue().toString());
			if (interested) {
				SyncIslandMembersPayload.send(player, dimensionId, members);
			}
		}
	}

	private static boolean isCoCreator(AccessRecord record, UUID uuid) {
		AccessRecord.Member m = record.members().get(uuid);
		return m != null && m.role == IslandRole.CO_CREATOR;
	}

	private static String managedDimOf(MinecraftServer server, ServerPlayerEntity actor) {
		return resolveActorManageDim(server, actor);
	}

	/** Même règle que {@link IslandAccessService} (île gérée si co-créateur dessus, sinon île perso). */
	private static String resolveActorManageDim(MinecraftServer server, ServerPlayerEntity actor) {
		String current = actor.getServerWorld().getRegistryKey().getValue().toString();
		if (IslandIds.isPersonalIsland(current)) {
			AccessRecord record = IslandAccessService.get().ensureIslandAccess(server, current, actor);
			if (IslandAccessService.get().canManageMembers(record, actor.getUuid())) {
				return current;
			}
		}
		return IslandIds.dimensionIdForPlayer(actor.getUuid());
	}
}

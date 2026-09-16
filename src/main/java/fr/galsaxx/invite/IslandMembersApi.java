package fr.galsaxx.invite;

import fr.galsaxx.network.SyncIslandMembersPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.UUID;

/**
 * Stable read API for the future adventure-book UI.
 */
public final class IslandMembersApi {
	private IslandMembersApi() {}

	public static List<IslandMemberEntry> listMembers(MinecraftServer server, UUID ownerUuid) {
		String dim = IslandDirectory.get().activeDimensionForOwner(ownerUuid)
				.orElse(IslandIds.dimensionIdForPlayer(ownerUuid));
		AccessRecord record = AccessFileStore.get().ensureFresh(dim, ownerUuid, "");
		return record.toMemberEntries(true, true, dim, TempVisitorStore.get().view(dim));
	}

	public static List<IslandMemberEntry> listMembersByDimension(MinecraftServer server, String dimensionId) {
		UUID owner = IslandIds.creatorUuidFromDimensionId(dimensionId).orElse(null);
		AccessRecord record = AccessFileStore.get().ensureFresh(dimensionId, owner, "");
		return record.toMemberEntries(true, true, dimensionId, TempVisitorStore.get().view(dimensionId));
	}

	/** Push member list to a client (stub payload for future book UI). */
	public static void syncToClient(ServerPlayerEntity player, String dimensionId) {
		MinecraftServer server = player.getServer();
		if (server == null) {
			return;
		}
		List<IslandMemberEntry> members = listMembersByDimension(server, dimensionId);
		SyncIslandMembersPayload.send(player, dimensionId, members);
	}
}

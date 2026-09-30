package fr.galsaxx.island;

import fr.galsaxx.invite.AccessFileStore;
import fr.galsaxx.invite.AccessRecord;
import net.minecraft.server.network.ServerPlayerEntity;

public final class IslandNameRefresh {
	private IslandNameRefresh() {}

	public static void onJoin(ServerPlayerEntity player) {
		if (player.getServer() == null) {
			return;
		}
		AccessFileStore store = AccessFileStore.get();
		store.bindServer(player.getServer());
		String name = player.getGameProfile().getName();
		for (AccessRecord record : store.listByOwner(player.getUuid())) {
			if (!name.equals(record.ownerNameHint())) {
				record.setOwnerNameHint(name);
				store.saveMutation(record);
			}
		}
		for (AccessRecord record : store.listInvited(player.getUuid())) {
			if (record.updateMemberName(player.getUuid(), name)) {
				store.saveMutation(record);
			}
		}
	}
}

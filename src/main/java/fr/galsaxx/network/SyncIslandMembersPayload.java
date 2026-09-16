package fr.galsaxx.network;

import fr.galsaxx.PersonnalWorld;
import fr.galsaxx.invite.IslandMemberEntry;
import fr.galsaxx.invite.IslandRole;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * S2C member-list sync for the future adventure-book UI.
 * Registered as a stub: encode/decode ready; client handler is a no-op until the book lands.
 */
public record SyncIslandMembersPayload(String dimensionId, List<IslandMemberEntry> members) implements CustomPayload {
	public static final CustomPayload.Id<SyncIslandMembersPayload> ID =
			new CustomPayload.Id<>(Identifier.of(PersonnalWorld.MOD_ID, "sync_island_members"));

	public static final PacketCodec<PacketByteBuf, SyncIslandMembersPayload> CODEC = PacketCodec.of(
			(payload, buf) -> write(buf, payload),
			SyncIslandMembersPayload::read
	);

	private static void write(PacketByteBuf buf, SyncIslandMembersPayload payload) {
		buf.writeString(payload.dimensionId());
		buf.writeVarInt(payload.members().size());
		for (IslandMemberEntry e : payload.members()) {
			buf.writeUuid(e.uuid());
			buf.writeString(e.nameHint());
			buf.writeString(e.role().name());
			buf.writeBoolean(e.temporary());
			buf.writeString(e.islandId());
		}
	}

	private static SyncIslandMembersPayload read(PacketByteBuf buf) {
		String dim = buf.readString();
		int n = buf.readVarInt();
		List<IslandMemberEntry> members = new ArrayList<>(n);
		for (int i = 0; i < n; i++) {
			UUID uuid = buf.readUuid();
			String name = buf.readString();
			IslandRole role = IslandRole.valueOf(buf.readString());
			boolean temp = buf.readBoolean();
			String islandId = buf.readString();
			members.add(new IslandMemberEntry(uuid, name, role, temp, islandId));
		}
		return new SyncIslandMembersPayload(dim, members);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	public static void register() {
		PersonnalWorld.LOGGER.info("SyncIslandMembersPayload codec ready (client handler deferred to book UI)");
	}

	/**
	 * Best-effort prepare: logs size for now; full S2C wiring lands with the adventure book.
	 */
	public static void send(ServerPlayerEntity player, String dimensionId, List<IslandMemberEntry> members) {
		PersonnalWorld.LOGGER.debug(
				"SyncIslandMembersPayload prepared for {} dim={} members={}",
				player.getGameProfile().getName(), dimensionId, members.size());
	}
}

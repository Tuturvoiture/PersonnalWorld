package fr.galsaxx.network;

import dev.architectury.networking.NetworkManager;
import fr.galsaxx.PersonnalWorld;
import fr.galsaxx.invite.AccessFileStore;
import fr.galsaxx.invite.AccessRecord;
import fr.galsaxx.invite.IslandIds;
import fr.galsaxx.invite.IslandMembersApi;
import fr.galsaxx.invite.IslandRole;
import fr.galsaxx.invite.PlayerRef;
import fr.galsaxx.island.IslandGameruleSync;
import fr.galsaxx.island.IslandLifecycle;
import fr.galsaxx.island.IslandPresetRegistry;
import fr.galsaxx.util.PersonalWorldSpawnReference;
import fr.galsaxx.util.PersonnalWorldUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class IslandBookNetworking {
	private IslandBookNetworking() {}

	public record Card(String dimensionId, String displayName, boolean active, String presetId, int slotIndex, String ownerName, String role) {}

	public record PresetInfo(String id, String name, String icon, boolean unlocked) {}

	public record SyncPayload(List<Card> owned, List<Card> invited, int maxIslands, List<PresetInfo> presets) implements CustomPayload {
		public static final Id<SyncPayload> ID = new Id<>(Identifier.of(PersonnalWorld.MOD_ID, "sync_island_book"));
		public static final PacketCodec<RegistryByteBuf, SyncPayload> CODEC = PacketCodec.of(SyncPayload::write, SyncPayload::read);

		private static void write(SyncPayload payload, RegistryByteBuf buf) {
			writeCards(buf, payload.owned);
			writeCards(buf, payload.invited);
			buf.writeVarInt(payload.maxIslands);
			buf.writeVarInt(payload.presets.size());
			for (PresetInfo preset : payload.presets) {
				buf.writeString(preset.id);
				buf.writeString(preset.name);
				buf.writeString(preset.icon);
				buf.writeBoolean(preset.unlocked);
			}
		}

		private static SyncPayload read(RegistryByteBuf buf) {
			List<Card> owned = readCards(buf);
			List<Card> invited = readCards(buf);
			int max = buf.readVarInt();
			int n = buf.readVarInt();
			List<PresetInfo> presets = new ArrayList<>(n);
			for (int i = 0; i < n; i++) {
				presets.add(new PresetInfo(buf.readString(), buf.readString(), buf.readString(), buf.readBoolean()));
			}
			return new SyncPayload(owned, invited, max, presets);
		}

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	public record DetailPayload(String dimensionId, boolean hasSpawn, int x, int y, int z, String displayName, boolean mobGriefing, boolean fire, boolean pvp) implements CustomPayload {
		public static final Id<DetailPayload> ID = new Id<>(Identifier.of(PersonnalWorld.MOD_ID, "island_book_detail"));
		public static final PacketCodec<RegistryByteBuf, DetailPayload> CODEC = PacketCodec.of(
				(payload, buf) -> {
					buf.writeString(payload.dimensionId);
					buf.writeBoolean(payload.hasSpawn);
					buf.writeVarInt(payload.x);
					buf.writeVarInt(payload.y);
					buf.writeVarInt(payload.z);
					buf.writeString(payload.displayName);
					buf.writeBoolean(payload.mobGriefing);
					buf.writeBoolean(payload.fire);
					buf.writeBoolean(payload.pvp);
				},
				buf -> new DetailPayload(buf.readString(), buf.readBoolean(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readString(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean()));

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	public record ActionPayload(String action, String dimensionId, String presetId, String name, String rule, String value) implements CustomPayload {
		public static final Id<ActionPayload> ID = new Id<>(Identifier.of(PersonnalWorld.MOD_ID, "island_book_action"));
		public static final PacketCodec<RegistryByteBuf, ActionPayload> CODEC = PacketCodec.of(
				(payload, buf) -> {
					buf.writeString(payload.action);
					buf.writeString(payload.dimensionId);
					buf.writeString(payload.presetId);
					buf.writeString(payload.name);
					buf.writeString(payload.rule);
					buf.writeString(payload.value);
				},
				buf -> new ActionPayload(buf.readString(), buf.readString(), buf.readString(), buf.readString(), buf.readString(), buf.readString()));

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	public static void registerServer() {
		dev.architectury.utils.EnvExecutor.runInEnv(dev.architectury.utils.Env.SERVER, () -> () -> {
			NetworkManager.registerS2CPayloadType(SyncPayload.ID, SyncPayload.CODEC);
			NetworkManager.registerS2CPayloadType(DetailPayload.ID, DetailPayload.CODEC);
		});
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, ActionPayload.ID, ActionPayload.CODEC, (payload, context) ->
				context.queue(() -> {
					if (context.getPlayer() instanceof ServerPlayerEntity player) {
						handle(player, payload);
					}
				}));
	}

	public static void registerClient(java.util.function.Consumer<SyncPayload> onSync, java.util.function.Consumer<DetailPayload> onDetail) {
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, SyncPayload.ID, SyncPayload.CODEC, (payload, context) ->
				context.queue(() -> onSync.accept(payload)));
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, DetailPayload.ID, DetailPayload.CODEC, (payload, context) ->
				context.queue(() -> onDetail.accept(payload)));
	}

	public static void request(String action, String dimensionId, String presetId, String name, String rule, String value) {
		NetworkManager.sendToServer(new ActionPayload(
				action == null ? "" : action,
				dimensionId == null ? "" : dimensionId,
				presetId == null ? "" : presetId,
				name == null ? "" : name,
				rule == null ? "" : rule,
				value == null ? "" : value));
	}

	private static void handle(ServerPlayerEntity player, ActionPayload payload) {
		MinecraftServer server = player.getServer();
		if (server == null) {
			return;
		}
		String action = payload.action();
		if ("request".equals(action)) {
			sendSync(player);
			return;
		}
		if ("visit".equals(action)) {
			fr.galsaxx.invite.IslandAccessService.Result joined =
					fr.galsaxx.invite.IslandAccessService.get().joinInvited(server, player, payload.dimensionId());
			joined.send(player);
			return;
		}
		if ("detail".equals(action)) {
			if (!IslandLifecycle.isOwner(server, player.getUuid(), payload.dimensionId())) {
				return;
			}
			sendDetail(player, payload.dimensionId());
			return;
		}
		if ("create".equals(action)) {
			IslandLifecycle.Outcome outcome = IslandLifecycle.create(
					server, player.getUuid(), player.getGameProfile().getName(), payload.presetId(), payload.name(), player);
			outcome.send(player);
			sendSync(player);
			return;
		}
		if (isMemberAction(action)) {
			if (!canManageDisplayed(server, player, payload.dimensionId())) {
				player.sendMessage(net.minecraft.text.Text.translatable("message.personnalworld.pw.no_permission"), false);
				return;
			}
			if ("members".equals(action)) {
				IslandMembersApi.syncToClient(player, payload.dimensionId());
				return;
			}
			if (!mutateMember(server, player, payload)) {
				return;
			}
			sendSync(player);
			return;
		}
		if (!IslandLifecycle.isOwner(server, player.getUuid(), payload.dimensionId())) {
			return;
		}
		AccessFileStore store = AccessFileStore.get();
		store.bindServer(server);
		AccessRecord record = store.loadOrRecover(payload.dimensionId(), player.getUuid(), player.getGameProfile().getName());
		switch (action) {
			case "rename" -> {
				if (payload.name().isBlank()) {
					player.sendMessage(net.minecraft.text.Text.translatable("message.personnalworld.pw.island_name_empty"), false);
					return;
				}
				record.setDisplayName(payload.name().trim());
				store.saveMutation(record);
			}
			case "setActive" -> setActive(store, player.getUuid(), payload.dimensionId());
			case "refreshSpawn" -> {
				ServerWorld world = world(server, payload.dimensionId());
				String key = PersonalWorldSpawnReference.relocate(world, player);
				player.sendMessage(net.minecraft.text.Text.translatable(key), false);
			}
			case "setOverlay" -> setOverlay(server, payload.dimensionId(), payload.rule(), payload.value());
			default -> {
				return;
			}
		}
		sendSync(player);
		sendDetail(player, payload.dimensionId());
	}

	private static void setActive(AccessFileStore store, java.util.UUID owner, String dimensionId) {
		for (AccessRecord record : store.listByOwner(owner)) {
			boolean active = record.dimensionId().equals(dimensionId);
			if (record.active() != active) {
				record.setActive(active);
				store.saveMutation(record);
			}
		}
	}

	private static boolean isMemberAction(String action) {
		return "members".equals(action) || "invite".equals(action) || "kick".equals(action) || "setMemberRole".equals(action);
	}

	/** Owner ou co-créateur de l’île affichée. Ne reconstruit pas le fichier avec l’acteur comme owner. */
	private static boolean canManageDisplayed(MinecraftServer server, ServerPlayerEntity player, String dimensionId) {
		if (!fr.galsaxx.invite.IslandIds.isPersonalIsland(dimensionId)) {
			return false;
		}
		AccessFileStore store = AccessFileStore.get();
		store.bindServer(server);
		java.util.UUID owner = fr.galsaxx.invite.IslandIds.creatorUuidFromDimensionId(dimensionId).orElse(null);
		AccessRecord record = store.loadOrRecover(dimensionId, owner, "");
		return fr.galsaxx.invite.IslandAccessService.get().canManageMembers(record, player.getUuid());
	}

	private static boolean mutateMember(MinecraftServer server, ServerPlayerEntity player, ActionPayload payload) {
		java.util.Optional<PlayerRef> target = PlayerRef.resolve(server, payload.name());
		if (target.isEmpty()) {
			player.sendMessage(net.minecraft.text.Text.translatable("message.personnalworld.pw.player_not_found"), false);
			return false;
		}
		fr.galsaxx.invite.IslandAccessService access = fr.galsaxx.invite.IslandAccessService.get();
		fr.galsaxx.invite.IslandAccessService.Result result = switch (payload.action()) {
			case "invite" -> access.inviteOn(server, player, payload.dimensionId(), target.get(), IslandRole.parseInviteRole(payload.rule()));
			case "kick" -> access.kickOn(server, player, payload.dimensionId(), target.get());
			case "setMemberRole" -> access.setRoleOn(server, player, payload.dimensionId(), target.get(), IslandRole.parseInviteRole(payload.rule()));
			default -> fr.galsaxx.invite.IslandAccessService.Result.fail("message.personnalworld.pw.invalid_role");
		};
		boolean quietRole = "quiet".equals(payload.value()) && "setMemberRole".equals(payload.action());
		if (!quietRole) {
			result.send(player);
		}
		if (result.ok()) {
			IslandMembersApi.notifyWatchers(server, payload.dimensionId());
			target.get().online().ifPresent(IslandBookNetworking::sendSync);
		}
		return result.ok();
	}

	private static void setOverlay(MinecraftServer server, String dimensionId, String rule, String value) {
		if (!rule.equals("mobGriefing") && !rule.equals("doFireTick") && !rule.equals("pvp")) {
			return;
		}
		ServerWorld world = world(server, dimensionId);
		if (world == null) {
			return;
		}
		PersonnalWorldUtil.putGameruleOverlay(world, rule, Boolean.toString(Boolean.parseBoolean(value)));
		IslandGameruleSync.apply(world);
	}

	public static void sendSync(ServerPlayerEntity player) {
		MinecraftServer server = player.getServer();
		if (server == null) {
			return;
		}
		AccessFileStore store = AccessFileStore.get();
		store.bindServer(server);
		List<Card> owned = new ArrayList<>();
		for (AccessRecord record : store.listByOwner(player.getUuid())) {
			if (record.displayName() == null || record.displayName().isBlank()) {
				int slot = IslandIds.slotIndex(record.dimensionId());
				record.setDisplayName(fr.galsaxx.island.IslandDefaultName.of(player.getClientOptions().language(), slot < 0 ? 0 : slot));
				store.saveMutation(record);
			}
			owned.add(card(server, record, ""));
		}
		List<Card> invited = new ArrayList<>();
		for (AccessRecord record : store.listInvited(player.getUuid())) {
			IslandRole role = store.invitedRole(record, player.getUuid());
			invited.add(card(server, record, role == null ? "" : role.name()));
		}
		List<PresetInfo> presets = new ArrayList<>();
		for (IslandPresetRegistry.IslandPreset preset : IslandLifecycle.currentPresets(server)) {
			presets.add(new PresetInfo(preset.id(), preset.name(), preset.icon(), preset.unlocked()));
		}
		NetworkManager.sendToPlayer(player, new SyncPayload(owned, invited, fr.galsaxx.config.PersonnalWorldConfig.get().maxIslandsPerPlayer(), presets));
	}

	private static void sendDetail(ServerPlayerEntity player, String dimensionId) {
		MinecraftServer server = player.getServer();
		ServerWorld world = world(server, dimensionId);
		AccessRecord record = AccessFileStore.get().loadOrRecover(dimensionId, player.getUuid(), "");
		boolean hasSpawn = false;
		int x = 0;
		int y = 0;
		int z = 0;
		boolean grief = true;
		boolean fire = true;
		boolean pvp = true;
		if (world != null) {
			var pos = PersonalWorldSpawnReference.getMarkerPos(world);
			if (pos.isPresent()) {
				BlockPos block = pos.get();
				hasSpawn = true;
				x = block.getX();
				y = block.getY();
				z = block.getZ();
			}
			Map<String, String> overlay = PersonnalWorldUtil.getGameruleOverlay(world);
			grief = bool(overlay, "mobGriefing", world.getGameRules().getBoolean(net.minecraft.world.GameRules.DO_MOB_GRIEFING));
			fire = bool(overlay, "doFireTick", world.getGameRules().getBoolean(net.minecraft.world.GameRules.DO_FIRE_TICK));
			pvp = bool(overlay, "pvp", true);
		}
		NetworkManager.sendToPlayer(player, new DetailPayload(dimensionId, hasSpawn, x, y, z, record.displayName(), grief, fire, pvp));
	}

	private static boolean bool(Map<String, String> overlay, String key, boolean fallback) {
		String value = overlay.get(key);
		return value == null ? fallback : Boolean.parseBoolean(value);
	}

	private static Card card(MinecraftServer server, AccessRecord record, String role) {
		String preset = "";
		Identifier id = Identifier.tryParse(record.dimensionId());
		if (id != null) {
			ServerWorld world = server.getWorld(RegistryKey.of(RegistryKeys.WORLD, id));
			if (world != null) {
				preset = PersonnalWorldUtil.getIslandProfile(world);
			}
		}
		return new Card(
				record.dimensionId(),
				record.displayName(),
				record.active(),
				preset,
				Math.max(0, IslandIds.slotIndex(record.dimensionId())),
				record.ownerNameHint(),
				role);
	}

	private static ServerWorld world(MinecraftServer server, String dimensionId) {
		Identifier id = Identifier.tryParse(dimensionId);
		if (id == null || server == null) {
			return null;
		}
		return server.getWorld(RegistryKey.of(RegistryKeys.WORLD, id));
	}

	private static void writeCards(RegistryByteBuf buf, List<Card> cards) {
		buf.writeVarInt(cards.size());
		for (Card card : cards) {
			buf.writeString(card.dimensionId);
			buf.writeString(card.displayName);
			buf.writeBoolean(card.active);
			buf.writeString(card.presetId);
			buf.writeVarInt(card.slotIndex);
			buf.writeString(card.ownerName);
			buf.writeString(card.role);
		}
	}

	private static List<Card> readCards(RegistryByteBuf buf) {
		int n = buf.readVarInt();
		List<Card> cards = new ArrayList<>(n);
		for (int i = 0; i < n; i++) {
			cards.add(new Card(buf.readString(), buf.readString(), buf.readBoolean(), buf.readString(), buf.readVarInt(), buf.readString(), buf.readString()));
		}
		return cards;
	}
}

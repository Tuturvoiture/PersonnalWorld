package fr.galsaxx.island;

import fr.galsaxx.PersonnalWorld;
import fr.galsaxx.config.PersonnalWorldConfig;
import fr.galsaxx.invite.AccessFileStore;
import fr.galsaxx.invite.AccessRecord;
import fr.galsaxx.invite.IslandDirectory;
import fr.galsaxx.invite.IslandIds;
import fr.galsaxx.util.PersonnalWorldUtil;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public final class IslandLifecycle {
	public record Outcome(boolean ok, String messageKey) {
		public void send(ServerPlayerEntity player) {
			if (player == null || messageKey == null) {
				return;
			}
			if (ok) {
				player.sendMessage(Text.translatable(messageKey), false);
			} else {
				player.sendMessage(Text.translatable(messageKey), false);
			}
		}
	}

	private IslandLifecycle() {}

	public static Outcome create(MinecraftServer server, UUID ownerUuid, String ownerName, String presetId, String displayName, ServerPlayerEntity notify) {
		if (ownerUuid == null) {
			return new Outcome(false, "message.personnalworld.pw.player_not_found");
		}
		if (displayName == null || displayName.isBlank()) {
			return new Outcome(false, "message.personnalworld.pw.island_name_empty");
		}
		AccessFileStore.get().bindServer(server);
		List<IslandPresetRegistry.IslandPreset> presets = currentPresets(server);
		if (IslandPresetRegistry.find(presets, presetId).isEmpty()) {
			return new Outcome(false, "message.personnalworld.pw.preset_unknown");
		}
		List<AccessRecord> owned = AccessFileStore.get().listByOwner(ownerUuid);
		IslandCreationPolicy.Decision decision = IslandCreationPolicy.evaluate(
				owned.size(),
				PersonnalWorldConfig.get().maxIslandsPerPlayer());
		if (!decision.allowed()) {
			return new Outcome(false, "message.personnalworld.pw.max_islands");
		}
		int slot = nextSlot(owned);
		String dimensionId = IslandIds.dimensionIdForSlot(ownerUuid, slot);
		Identifier identifier = Identifier.tryParse(dimensionId);
		if (identifier == null) {
			return new Outcome(false, "message.personnalworld.dimension_create_error");
		}
		RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, identifier);
		ServerPlayerEntity onlineOwner = null;
		if (notify != null && ownerUuid.equals(notify.getUuid())) {
			onlineOwner = notify;
		} else if (server.getPlayerManager().getPlayer(ownerUuid) != null) {
			onlineOwner = server.getPlayerManager().getPlayer(ownerUuid);
		}
		ServerWorld world = PersonnalWorldUtil.ensurePersonalWorld(server, worldKey, identifier, onlineOwner);
		if (world == null) {
			return new Outcome(false, "message.personnalworld.dimension_unavailable_after_create");
		}
		AccessRecord record = AccessFileStore.get().loadOrRecover(dimensionId, ownerUuid, ownerName == null ? "" : ownerName);
		record.setOwnerUuid(ownerUuid);
		record.setOwnerNameHint(ownerName == null ? "" : ownerName);
		record.setDisplayName(displayName.trim());
		boolean first = owned.isEmpty();
		record.setActive(first);
		AccessFileStore.get().saveMutation(record);
		IslandDirectory.get().registerOrUpdate(record);
		PersonnalWorldUtil.setIslandProfile(world, presetId);
		IslandGameruleSync.apply(world);
		return new Outcome(true, "message.personnalworld.pw.island_created");
	}

	public static boolean isOwner(MinecraftServer server, UUID actor, String dimensionId) {
		if (actor == null || dimensionId == null || dimensionId.isBlank()) {
			return false;
		}
		AccessFileStore store = AccessFileStore.get();
		store.bindServer(server);
		store.listByOwner(actor);
		return store.getCached(dimensionId).map(record -> actor.equals(record.ownerUuid())).orElse(false);
	}

	public static List<IslandPresetRegistry.IslandPreset> currentPresets(MinecraftServer server) {
		java.nio.file.Path dir = server.getRunDirectory().resolve("config").resolve("personnalworld").resolve("island_presets");
		return IslandPresetRegistry.merge(IslandPresetRegistry.defaults(), IslandPresetFiles.load(dir));
	}

	private static int nextSlot(List<AccessRecord> owned) {
		int max = -1;
		for (AccessRecord record : owned) {
			max = Math.max(max, IslandIds.slotIndex(record.dimensionId()));
		}
		return max + 1;
	}
}

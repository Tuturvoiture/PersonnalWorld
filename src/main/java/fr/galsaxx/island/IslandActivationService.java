package fr.galsaxx.island;

import fr.galsaxx.config.PersonnalWorldConfig;
import fr.galsaxx.invite.AccessFileStore;
import fr.galsaxx.invite.AccessRecord;
import fr.galsaxx.invite.IslandAccessService;
import fr.galsaxx.invite.IslandDirectory;
import fr.galsaxx.invite.IslandIds;
import fr.galsaxx.util.PersonnalWorldUtil;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bascule l’île active d’un owner : cooldown, load de la nouvelle, unload des autres.
 */
public final class IslandActivationService {
	private static final Map<UUID, Long> LAST_SWITCH_EPOCH_MS = new ConcurrentHashMap<>();

	private IslandActivationService() {}

	public static IslandAccessService.Result activate(MinecraftServer server, ServerPlayerEntity owner, String dimensionId) {
		if (server == null || owner == null || dimensionId == null || !IslandIds.isPersonalIsland(dimensionId)) {
			return IslandAccessService.Result.fail("message.personnalworld.pw.debug_bad_target");
		}
		AccessFileStore store = AccessFileStore.get();
		store.bindServer(server);
		List<AccessRecord> owned = store.listByOwner(owner.getUuid());
		AccessRecord target = null;
		for (AccessRecord record : owned) {
			if (dimensionId.equals(record.dimensionId())) {
				target = record;
				break;
			}
		}
		if (target == null) {
			return IslandAccessService.Result.fail("message.personnalworld.pw.no_permission");
		}

		if (target.active()) {
			if (!ensureLoaded(server, owner, dimensionId)) {
				return IslandAccessService.Result.fail("message.personnalworld.personal_world_unavailable");
			}
			return IslandAccessService.Result.ok("message.personnalworld.pw.active_already");
		}

		int cooldownSec = PersonnalWorldConfig.get().activeIslandSwitchCooldownSeconds();
		Long last = LAST_SWITCH_EPOCH_MS.get(owner.getUuid());
		if (cooldownSec > 0 && last != null) {
			long elapsedMs = System.currentTimeMillis() - last;
			long remainMs = cooldownSec * 1000L - elapsedMs;
			if (remainMs > 0) {
				int remainSec = (int) Math.ceil(remainMs / 1000.0);
				return IslandAccessService.Result.fail("message.personnalworld.pw.active_switch_cooldown", remainSec);
			}
		}

		List<String> toUnload = new ArrayList<>();
		for (AccessRecord record : owned) {
			boolean makeActive = dimensionId.equals(record.dimensionId());
			if (record.active() != makeActive) {
				record.setActive(makeActive);
				store.saveMutation(record);
			}
			if (!makeActive) {
				toUnload.add(record.dimensionId());
			}
		}
		IslandDirectory.get().registerOrUpdate(store.loadOrRecover(dimensionId, owner.getUuid(), owner.getGameProfile().getName()));

		for (String other : toUnload) {
			IslandAccessService.get().evictEveryone(server, other, "message.personnalworld.pw.island_deactivated");
			IslandAccessService.get().purgeTempsForDimension(other);
			PersonnalWorldUtil.tryUnloadPersonalWorld(server, other);
		}

		if (!ensureLoaded(server, owner, dimensionId)) {
			return IslandAccessService.Result.fail("message.personnalworld.personal_world_unavailable");
		}

		LAST_SWITCH_EPOCH_MS.put(owner.getUuid(), System.currentTimeMillis());
		return IslandAccessService.Result.ok("message.personnalworld.pw.active_switch_ok");
	}

	private static boolean ensureLoaded(MinecraftServer server, ServerPlayerEntity owner, String dimensionId) {
		Identifier id = Identifier.tryParse(dimensionId);
		if (id == null) {
			return false;
		}
		RegistryKey<World> key = RegistryKey.of(RegistryKeys.WORLD, id);
		ServerWorld world = PersonnalWorldUtil.openExistingPersonalWorld(server, key, id, owner);
		if (world == null) {
			world = PersonnalWorldUtil.ensurePersonalWorld(server, key, id, owner);
		}
		return world != null;
	}
}

package fr.galsaxx.invite;

import fr.galsaxx.config.PersonnalWorldConfig;
import fr.galsaxx.util.PersonalWorldSpawnSafety;
import fr.galsaxx.util.PersonnalWorldUtil;
import fr.galsaxx.util.ReturnTeleport;
import net.darchitect.access.DimensionPermission;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Business access API for personal islands (PW whitelist = source of truth).
 */
public final class IslandAccessService {
	private static final IslandAccessService INSTANCE = new IslandAccessService();

	private IslandAccessService() {}

	public static IslandAccessService get() {
		return INSTANCE;
	}

	public AccessRecord ensureIslandAccess(MinecraftServer server, String dimensionId, ServerPlayerEntity ownerHint) {
		AccessFileStore.get().bindServer(server);
		UUID owner = ownerHint != null ? ownerHint.getUuid() : IslandIds.creatorUuidFromDimensionId(dimensionId).orElse(null);
		String name = ownerHint != null ? ownerHint.getGameProfile().getName() : "";
		AccessRecord record = AccessFileStore.get().ensureFresh(dimensionId, owner, name);
		DArchitectAccess.applyRecord(record);
		return record;
	}

	public AccessRecord ensureIslandAccess(MinecraftServer server, String dimensionId, UUID ownerFallback, String nameHint) {
		AccessFileStore.get().bindServer(server);
		AccessRecord record = AccessFileStore.get().ensureFresh(dimensionId, ownerFallback, nameHint);
		DArchitectAccess.applyRecord(record);
		return record;
	}

	public boolean canInvite(AccessRecord record, UUID actor) {
		if (actor.equals(record.ownerUuid())) {
			return true;
		}
		AccessRecord.Member m = record.members().get(actor);
		return m != null && m.role == IslandRole.CO_CREATOR;
	}

	public boolean canManageMembers(AccessRecord record, UUID actor) {
		return canInvite(record, actor);
	}

	public IslandRole effectiveRole(AccessRecord record, UUID player) {
		if (player.equals(record.ownerUuid())) {
			return IslandRole.OWNER;
		}
		if (TempVisitorStore.get().isTemp(record.dimensionId(), player)) {
			return IslandRole.TEMP_VISITOR;
		}
		AccessRecord.Member m = record.members().get(player);
		return m == null ? null : m.role;
	}

	public boolean hasJoin(AccessRecord record, UUID player) {
		IslandRole role = effectiveRole(record, player);
		if (role == null || role == IslandRole.BANNED) {
			return false;
		}
		return role == IslandRole.OWNER
				|| role == IslandRole.CO_CREATOR
				|| role == IslandRole.BUILDER
				|| role == IslandRole.VISITOR
				|| role == IslandRole.TEMP_VISITOR
				|| DArchitectAccess.hasPermission(record.dimensionId(), player, DimensionPermission.JOIN);
	}

	public Result invite(MinecraftServer server, ServerPlayerEntity actor, PlayerRef target, IslandRole role) {
		if (role == null || !role.isAssignableByInviteCommand()) {
			return Result.fail("message.personnalworld.pw.invalid_role");
		}
		if (actor.getUuid().equals(target.uuid())) {
			return Result.fail("message.personnalworld.pw.cannot_target_self");
		}
		String dimId = resolveManagedDimension(server, actor);
		AccessRecord record = ensureIslandAccess(server, dimId, actor);
		if (!canInvite(record, actor.getUuid())) {
			return Result.fail("message.personnalworld.pw.no_permission");
		}
		if (target.uuid().equals(record.ownerUuid())) {
			return Result.fail("message.personnalworld.pw.cannot_change_owner");
		}
		record.putMember(target.uuid(), role, target.name());
		revokeTemp(dimId, target.uuid());
		AccessFileStore.get().saveMutation(record);
		return Result.ok("message.personnalworld.pw.invite_ok", target.name(), role.name());
	}

	public Result kick(MinecraftServer server, ServerPlayerEntity actor, PlayerRef target) {
		String dimId = resolveManagedDimension(server, actor);
		AccessRecord record = ensureIslandAccess(server, dimId, actor);
		if (!canManageMembers(record, actor.getUuid())) {
			return Result.fail("message.personnalworld.pw.no_permission");
		}
		if (target.uuid().equals(record.ownerUuid())) {
			return Result.fail("message.personnalworld.pw.cannot_change_owner");
		}
		boolean removed = record.members().remove(target.uuid()) != null;
		boolean tempRemoved = TempVisitorStore.get().remove(dimId, target.uuid());
		if (!removed && !tempRemoved) {
			return Result.fail("message.personnalworld.pw.not_on_list");
		}
		if (removed) {
			AccessFileStore.get().saveMutation(record);
		} else {
			// TEMP only: clear DA guest without rewriting whitelist file
			DArchitectAccess.clearRole(dimId, target.uuid());
			DArchitectAccess.applyRecord(record);
		}
		evictIfPresent(server, dimId, target.uuid());
		return Result.ok("message.personnalworld.pw.kick_ok", target.name());
	}

	public Result setRole(MinecraftServer server, ServerPlayerEntity actor, PlayerRef target, IslandRole role) {
		if (role == null || !role.isAssignableByInviteCommand()) {
			return Result.fail("message.personnalworld.pw.invalid_role");
		}
		String dimId = resolveManagedDimension(server, actor);
		AccessRecord record = ensureIslandAccess(server, dimId, actor);
		if (!canManageMembers(record, actor.getUuid())) {
			return Result.fail("message.personnalworld.pw.no_permission");
		}
		if (target.uuid().equals(record.ownerUuid())) {
			return Result.fail("message.personnalworld.pw.cannot_change_owner");
		}
		record.putMember(target.uuid(), role, target.name());
		revokeTemp(dimId, target.uuid());
		AccessFileStore.get().saveMutation(record);
		return Result.ok("message.personnalworld.pw.role_ok", target.name(), role.name());
	}

	public List<IslandMemberEntry> listMembers(MinecraftServer server, ServerPlayerEntity actor) {
		String dimId = resolveManagedDimension(server, actor);
		AccessRecord record = ensureIslandAccess(server, dimId, actor);
		return record.toMemberEntries(true, true, dimId, TempVisitorStore.get().view(dimId));
	}

	/**
	 * Visit host island (host online or offline via UUID / user cache).
	 */
	public Result visit(MinecraftServer server, ServerPlayerEntity visitor, PlayerRef host, String optionalIslandName) {
		Optional<IslandDirectory.ResolveResult> resolved =
				IslandDirectory.get().resolveVisitTarget(host.uuid(), optionalIslandName);
		String dimId;
		boolean passive;
		if (resolved.isPresent()) {
			dimId = resolved.get().dimensionId();
			passive = resolved.get().passive();
		} else if (optionalIslandName != null && !optionalIslandName.isBlank()) {
			return Result.fail("message.personnalworld.pw.island_not_found");
		} else {
			dimId = IslandIds.dimensionIdForPlayer(host.uuid());
			passive = false;
		}
		if (passive && !PersonnalWorldConfig.get().allowPassiveIslandVisit()) {
			return Result.fail("message.personnalworld.pw.passive_visit_denied");
		}

		AccessRecord record = ensureIslandAccess(server, dimId, host.uuid(), host.name());
		if (visitor.getUuid().equals(record.ownerUuid())) {
			return Result.fail("message.personnalworld.pw.visit_own");
		}
		IslandRole existing = effectiveRole(record, visitor.getUuid());
		if (existing == IslandRole.BANNED) {
			return Result.fail("message.personnalworld.pw.visit_denied");
		}
		boolean permanent = existing == IslandRole.CO_CREATOR || existing == IslandRole.BUILDER || existing == IslandRole.VISITOR;
		if (!permanent) {
			TempVisitorStore.get().put(dimId, visitor.getUuid(), visitor.getGameProfile().getName());
			DArchitectAccess.grantTempGuest(dimId, visitor.getUuid());
		}

		Identifier id = Identifier.tryParse(dimId);
		if (id == null) {
			return Result.fail("message.personnalworld.personal_world_unavailable");
		}
		RegistryKey<World> key = RegistryKey.of(RegistryKeys.WORLD, id);
		ServerPlayerEntity hostOnline = host.online().orElse(null);
		ServerWorld world = PersonnalWorldUtil.ensurePersonalWorld(server, key, id, hostOnline);
		if (world == null) {
			return Result.fail("message.personnalworld.personal_world_unavailable");
		}
		// Access again after ensure (migration / file create)
		ensureIslandAccess(server, dimId, host.uuid(), host.name());
		Vec3d spawn = PersonalWorldSpawnSafety.resolveTeleportPosition(world);
		visitor.teleport(world, spawn.x, spawn.y, spawn.z, Set.of(), 0.0F, 0.0F);
		return Result.ok("message.personnalworld.pw.visit_ok", host.name());
	}

	public Result leave(ServerPlayerEntity player) {
		String current = player.getServerWorld().getRegistryKey().getValue().toString();
		if (!IslandIds.isPersonalIsland(current)) {
			return Result.fail("message.personnalworld.command_only_in_personal_world");
		}
		UUID uuid = player.getUuid();
		AccessRecord record = AccessFileStore.get().getCached(current).orElse(null);
		boolean isOwner = record != null && uuid.equals(record.ownerUuid());
		if (isOwner) {
			return Result.fail("message.personnalworld.pw.leave_owner");
		}
		revokeTemp(current, uuid);
		ReturnTeleport.teleportHome(player);
		return Result.ok("message.personnalworld.pw.leave_ok");
	}

	public void onPlayerLeaveDimension(ServerPlayerEntity player, String fromDimensionId) {
		if (IslandIds.isPersonalIsland(fromDimensionId)) {
			revokeTemp(fromDimensionId, player.getUuid());
		}
	}

	public void onPlayerDisconnect(ServerPlayerEntity player) {
		UUID uuid = player.getUuid();
		String current = player.getServerWorld().getRegistryKey().getValue().toString();
		if (IslandIds.isPersonalIsland(current)) {
			revokeTemp(current, uuid);
		}
		for (IslandDirectory.IslandMeta meta : IslandDirectory.get().all()) {
			if (TempVisitorStore.get().isTemp(meta.dimensionId(), uuid)) {
				revokeTemp(meta.dimensionId(), uuid);
			}
		}
		TempVisitorStore.get().removePlayerEverywhere(uuid);
	}

	public void purgeTempsForDimension(String dimensionId) {
		for (UUID uuid : List.copyOf(TempVisitorStore.get().view(dimensionId).keySet())) {
			DArchitectAccess.clearRole(dimensionId, uuid);
		}
		TempVisitorStore.get().clearDimension(dimensionId);
		AccessFileStore.get().getCached(dimensionId).ifPresent(DArchitectAccess::applyRecord);
	}

	private void revokeTemp(String dimensionId, UUID uuid) {
		boolean wasTemp = TempVisitorStore.get().remove(dimensionId, uuid);
		if (wasTemp) {
			DArchitectAccess.clearRole(dimensionId, uuid);
			AccessFileStore.get().getCached(dimensionId).ifPresent(DArchitectAccess::applyRecord);
		}
	}

	public void evictIfPresent(MinecraftServer server, String dimensionId, UUID playerUuid) {
		ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUuid);
		if (player == null) {
			return;
		}
		String current = player.getServerWorld().getRegistryKey().getValue().toString();
		if (dimensionId.equals(current)) {
			player.sendMessage(Text.translatable("message.personnalworld.pw.evicted"), false);
			ReturnTeleport.teleportHome(player);
		}
	}

	public void evictAllWithoutJoin(MinecraftServer server, String dimensionId) {
		AccessRecord record = AccessFileStore.get().getCached(dimensionId).orElse(null);
		if (record == null) {
			UUID owner = IslandIds.creatorUuidFromDimensionId(dimensionId).orElse(null);
			record = AccessFileStore.get().loadOrRecover(dimensionId, owner, "");
		}
		Identifier id = Identifier.tryParse(dimensionId);
		if (id == null) {
			return;
		}
		ServerWorld world = server.getWorld(RegistryKey.of(RegistryKeys.WORLD, id));
		if (world == null) {
			return;
		}
		for (ServerPlayerEntity player : List.copyOf(world.getPlayers())) {
			if (!hasJoin(record, player.getUuid())) {
				player.sendMessage(Text.translatable("message.personnalworld.pw.evicted"), false);
				ReturnTeleport.teleportHome(player);
			}
		}
	}

	public void evictEveryone(MinecraftServer server, String dimensionId) {
		Identifier id = Identifier.tryParse(dimensionId);
		if (id == null) {
			return;
		}
		ServerWorld world = server.getWorld(RegistryKey.of(RegistryKeys.WORLD, id));
		if (world == null) {
			return;
		}
		for (ServerPlayerEntity player : List.copyOf(world.getPlayers())) {
			player.sendMessage(Text.translatable("message.personnalworld.pw.island_reloading"), false);
			ReturnTeleport.teleportHome(player);
		}
	}

	private String resolveManagedDimension(MinecraftServer server, ServerPlayerEntity actor) {
		String current = actor.getServerWorld().getRegistryKey().getValue().toString();
		if (IslandIds.isPersonalIsland(current)) {
			AccessRecord record = ensureIslandAccess(server, current, actor);
			if (canManageMembers(record, actor.getUuid())) {
				return current;
			}
		}
		return IslandIds.dimensionIdForPlayer(actor.getUuid());
	}

	public record Result(boolean ok, String messageKey, Object[] args) {
		public static Result ok(String key, Object... args) {
			return new Result(true, key, args);
		}

		public static Result fail(String key, Object... args) {
			return new Result(false, key, args);
		}

		public void send(ServerPlayerEntity player) {
			player.sendMessage(Text.translatable(messageKey, args), false);
		}

		public void send(net.minecraft.server.command.ServerCommandSource source) {
			source.sendFeedback(() -> Text.translatable(messageKey, args), false);
		}
	}
}

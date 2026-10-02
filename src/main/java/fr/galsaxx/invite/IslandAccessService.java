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
		AccessRecord.Member member = record.members().get(player);
		if (member != null && member.role != null && member.role.isAssignableByInviteCommand()) {
			return member.role;
		}
		if (member != null && member.role == IslandRole.BANNED) {
			return IslandRole.BANNED;
		}
		if (TempVisitorStore.get().isTemp(record.dimensionId(), player)) {
			return IslandRole.TEMP_VISITOR;
		}
		return member == null ? null : member.role;
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
		return inviteOn(server, actor, resolveManagedDimension(server, actor), target, role);
	}

	public Result inviteOn(MinecraftServer server, ServerPlayerEntity actor, String dimensionId, PlayerRef target, IslandRole role) {
		if (role == null || !role.isAssignableByInviteCommand()) {
			return Result.fail("message.personnalworld.pw.invalid_role");
		}
		if (actor.getUuid().equals(target.uuid())) {
			return Result.fail("message.personnalworld.pw.cannot_target_self");
		}
		UUID hintedOwner = IslandIds.creatorUuidFromDimensionId(dimensionId).orElse(actor.getUuid());
		String hintedName = hintedOwner.equals(actor.getUuid()) ? actor.getGameProfile().getName() : "";
		AccessRecord record = ensureIslandAccess(server, dimensionId, hintedOwner, hintedName);
		if (!canInvite(record, actor.getUuid())) {
			return Result.fail("message.personnalworld.pw.no_permission");
		}
		if (target.uuid().equals(record.ownerUuid())) {
			return Result.fail("message.personnalworld.pw.cannot_change_owner");
		}
		record.putMember(target.uuid(), role, target.name());
		revokeTemp(dimensionId, target.uuid());
		AccessFileStore.get().saveMutation(record);
		String ownerName = record.ownerNameHint().isBlank() ? actor.getGameProfile().getName() : record.ownerNameHint();
		target.online().ifPresent(invited ->
				invited.sendMessage(Text.translatable("message.personnalworld.pw.invited_you", ownerName), false));
		return Result.ok("message.personnalworld.pw.invite_ok", target.name(), roleLabel(role));
	}

	public Result kick(MinecraftServer server, ServerPlayerEntity actor, PlayerRef target) {
		return kickOn(server, actor, resolveManagedDimension(server, actor), target);
	}

	public Result kickOn(MinecraftServer server, ServerPlayerEntity actor, String dimensionId, PlayerRef target) {
		UUID hintedOwner = IslandIds.creatorUuidFromDimensionId(dimensionId).orElse(actor.getUuid());
		String hintedName = hintedOwner.equals(actor.getUuid()) ? actor.getGameProfile().getName() : "";
		AccessRecord record = ensureIslandAccess(server, dimensionId, hintedOwner, hintedName);
		if (!canManageMembers(record, actor.getUuid())) {
			return Result.fail("message.personnalworld.pw.no_permission");
		}
		if (target.uuid().equals(record.ownerUuid())) {
			return Result.fail("message.personnalworld.pw.cannot_change_owner");
		}
		boolean removed = record.members().remove(target.uuid()) != null;
		boolean tempRemoved = TempVisitorStore.get().remove(dimensionId, target.uuid());
		if (!removed && !tempRemoved) {
			return Result.fail("message.personnalworld.pw.not_on_list");
		}
		if (removed) {
			AccessFileStore.get().saveMutation(record);
		} else {
			DArchitectAccess.clearRole(dimensionId, target.uuid());
			DArchitectAccess.applyRecord(record);
		}
		evictIfPresent(server, dimensionId, target.uuid());
		return Result.ok("message.personnalworld.pw.kick_ok", target.name());
	}

	public Result setRole(MinecraftServer server, ServerPlayerEntity actor, PlayerRef target, IslandRole role) {
		return setRoleOn(server, actor, resolveManagedDimension(server, actor), target, role);
	}

	public Result setRoleOn(MinecraftServer server, ServerPlayerEntity actor, String dimensionId, PlayerRef target, IslandRole role) {
		if (role == null || !role.isAssignableByInviteCommand()) {
			return Result.fail("message.personnalworld.pw.invalid_role");
		}
		UUID hintedOwner = IslandIds.creatorUuidFromDimensionId(dimensionId).orElse(actor.getUuid());
		String hintedName = hintedOwner.equals(actor.getUuid()) ? actor.getGameProfile().getName() : "";
		AccessRecord record = ensureIslandAccess(server, dimensionId, hintedOwner, hintedName);
		if (!canManageMembers(record, actor.getUuid())) {
			return Result.fail("message.personnalworld.pw.no_permission");
		}
		if (target.uuid().equals(record.ownerUuid())) {
			return Result.fail("message.personnalworld.pw.cannot_change_owner");
		}
		record.putMember(target.uuid(), role, target.name());
		revokeTemp(dimensionId, target.uuid());
		AccessFileStore.get().saveMutation(record);
		return Result.ok("message.personnalworld.pw.role_ok", target.name(), roleLabel(role));
	}

	/** Rejoint une île où le joueur est déjà invité. Ne crée pas la dimension. */
	public Result joinInvited(MinecraftServer server, ServerPlayerEntity visitor, String dimensionId) {
		if (!IslandIds.isPersonalIsland(dimensionId)) {
			return Result.fail("message.personnalworld.pw.visit_denied");
		}
		AccessFileStore.get().bindServer(server);
		if (!PersonnalWorldUtil.personalDimensionExists(server, dimensionId)) {
			return Result.fail("message.personnalworld.pw.island_not_found");
		}
		return teleportWhitelisted(server, visitor, dimensionId, null);
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
		if (resolved.isEmpty()) {
			return Result.fail("message.personnalworld.pw.island_not_found");
		}
		String dimId = resolved.get().dimensionId();
		if (resolved.get().passive() && !PersonnalWorldConfig.get().allowPassiveIslandVisit()) {
			return Result.fail("message.personnalworld.pw.passive_visit_denied");
		}
		AccessFileStore.get().bindServer(server);
		if (!PersonnalWorldUtil.personalDimensionExists(server, dimId)) {
			return Result.fail("message.personnalworld.pw.island_not_found");
		}
		return teleportWhitelisted(server, visitor, dimId, host.name());
	}

	/** Téléporte seulement un membre whitelist vers une dimension qui existe déjà. */
	private Result teleportWhitelisted(MinecraftServer server, ServerPlayerEntity visitor, String dimId, String hostLabel) {
		Identifier id = Identifier.tryParse(dimId);
		if (id == null) {
			return Result.fail("message.personnalworld.personal_world_unavailable");
		}
		UUID owner = IslandIds.creatorUuidFromDimensionId(dimId).orElse(null);
		AccessRecord record = AccessFileStore.get().loadOrRecover(dimId, owner, "");
		if (visitor.getUuid().equals(record.ownerUuid())) {
			return Result.fail("message.personnalworld.pw.visit_own");
		}
		if (!isWhitelisted(record, visitor.getUuid())) {
			return Result.fail("message.personnalworld.pw.visit_denied");
		}
		RegistryKey<World> key = RegistryKey.of(RegistryKeys.WORLD, id);
		ServerPlayerEntity hostOnline = server.getPlayerManager().getPlayer(record.ownerUuid());
		// Never create on visit — reload persisted dim if unloaded (host may be offline).
		ServerWorld world = PersonnalWorldUtil.openExistingPersonalWorld(server, key, id, hostOnline);
		if (world == null) {
			return Result.fail("message.personnalworld.personal_world_unavailable");
		}
		DArchitectAccess.applyRecord(record);
		ReturnTeleport.rememberIfAllowed(visitor);
		Vec3d spawn = PersonalWorldSpawnSafety.resolveTeleportPosition(world);
		visitor.teleport(world, spawn.x, spawn.y, spawn.z, Set.of(), 0.0F, 0.0F);
		String ownerName = !record.ownerNameHint().isBlank()
				? record.ownerNameHint()
				: (hostLabel != null && !hostLabel.isBlank() ? hostLabel : record.ownerUuid().toString());
		return Result.ok("message.personnalworld.pw.visit_ok", ownerName);
	}

	private static boolean isWhitelisted(AccessRecord record, UUID player) {
		AccessRecord.Member member = record.members().get(player);
		return member != null && member.role != null && member.role.isAssignableByInviteCommand();
	}

	public Result leave(ServerPlayerEntity player) {
		String current = player.getServerWorld().getRegistryKey().getValue().toString();
		if (!IslandIds.isPersonalIsland(current)) {
			return Result.fail("message.personnalworld.command_only_in_personal_world");
		}
		UUID uuid = player.getUuid();
		MinecraftServer server = player.getServer();
		if (server != null) {
			AccessFileStore.get().bindServer(server);
		}
		UUID owner = IslandIds.creatorUuidFromDimensionId(current).orElse(null);
		AccessRecord record = AccessFileStore.get().loadOrRecover(current, owner, "");
		if (uuid.equals(record.ownerUuid())) {
			return Result.fail("message.personnalworld.pw.leave_owner");
		}
		revokeTemp(current, uuid);
		ReturnTeleport.forgetIfDimension(player, current);
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
			ReturnTeleport.forgetIfDimension(player, dimensionId);
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
				ReturnTeleport.forgetIfDimension(player, dimensionId);
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
			ReturnTeleport.forgetIfDimension(player, dimensionId);
			ReturnTeleport.teleportHome(player);
		}
	}

	private String resolveManagedDimension(MinecraftServer server, ServerPlayerEntity actor) {
		AccessFileStore.get().bindServer(server);
		String current = actor.getServerWorld().getRegistryKey().getValue().toString();
		if (IslandIds.isPersonalIsland(current)) {
			AccessRecord record = ensureIslandAccess(server, current, actor);
			if (canManageMembers(record, actor.getUuid())) {
				return current;
			}
		}
		return AccessFileStore.get().activeOrFirstOwned(actor.getUuid())
				.orElse(IslandIds.dimensionIdForPlayer(actor.getUuid()));
	}

	private static Text roleLabel(IslandRole role) {
		return Text.translatable("screen.personnalworld.adventure_book.role." + role.name().toLowerCase(java.util.Locale.ROOT));
	}

	public record Result(boolean ok, String messageKey, Object[] args) {
		public static Result ok(String key, Object... args) {
			return new Result(true, key, args);
		}

		public static Result fail(String key, Object... args) {
			return new Result(false, key, args);
		}

		public void send(ServerPlayerEntity player) {
			Text text = Text.translatable(messageKey, args);
			if (travelFeedback()) {
				ReturnTeleport.actionBar(player, text);
				return;
			}
			player.sendMessage(text, false);
		}

		public void send(net.minecraft.server.command.ServerCommandSource source) {
			if (travelFeedback() && source.getEntity() instanceof ServerPlayerEntity player) {
				ReturnTeleport.actionBar(player, Text.translatable(messageKey, args));
				return;
			}
			source.sendFeedback(() -> Text.translatable(messageKey, args), false);
		}

		private boolean travelFeedback() {
			return "message.personnalworld.pw.visit_ok".equals(messageKey);
		}
	}
}

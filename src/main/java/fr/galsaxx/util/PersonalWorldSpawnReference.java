package fr.galsaxx.util;

import fr.galsaxx.PersonnalWorld;
import fr.galsaxx.PersonnalWorldContent;
import fr.galsaxx.island.SpawnMove;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

/**
 * Référence de spawn par dimension ({@code spawn_marker}).
 * MVP : île perso unique ; futur : profils d’île / positions déplaçables.
 */
public final class PersonalWorldSpawnReference {
	private PersonalWorldSpawnReference() {}

	public record SpawnMarkerDefaults(int x, int y, int z) {
		public BlockPos toBlockPos() {
			return new BlockPos(x, y, z);
		}
	}

	public static SpawnMarkerDefaults defaultsFor(ServerWorld world) {
		String dimId = world.getRegistryKey().getValue().toString();
		if (dimId.startsWith(PersonnalWorld.MOD_ID + ":perso_")) {
			return new SpawnMarkerDefaults(
					PersonnalWorld.ISLAND_SPAWN_X,
					PersonnalWorld.SPAWN_MARKER_Y,
					PersonnalWorld.ISLAND_SPAWN_Z);
		}
		// Futur : lire islandProfile depuis PWWorldState pour d’autres types de dims.
		return new SpawnMarkerDefaults(
				PersonnalWorld.ISLAND_SPAWN_X,
				PersonnalWorld.SPAWN_MARKER_Y,
				PersonnalWorld.ISLAND_SPAWN_Z);
	}

	public static void ensureMarker(ServerWorld world) {
		PersonnalWorldUtil.PWWorldState state = PersonnalWorldUtil.getWorldState(world);
		BlockPos target = state.hasSpawnMarker()
				? state.getSpawnMarkerPos()
				: defaultsFor(world).toBlockPos();

		world.getChunk(target.getX() >> 4, target.getZ() >> 4);
		if (!isMarkerBlock(world, target)) {
			rememberReplaced(state, world.getBlockState(target));
			world.setBlockState(target, PersonnalWorldContent.SPAWN_MARKER.get().getDefaultState(), 3);
		}
		state.setSpawnMarker(target);
		state.markDirty();
	}

	/**
	 * Pose le cube sous les pieds du joueur, dans la dimension affichée.
	 * Enregistre aussi l’orientation (N/E/S/O à 90°).
	 */
	public static RelocateOutcome relocate(ServerWorld world, ServerPlayerEntity player) {
		if (world == null || player == null) {
			return RelocateOutcome.fail("message.personnalworld.spawn.wrong_dim");
		}
		String islandId = world.getRegistryKey().getValue().toString();
		String standing = player.getServerWorld().getRegistryKey().getValue().toString();
		BlockPos floorPos = player.getBlockPos().down();
		world.getChunk(floorPos.getX() >> 4, floorPos.getZ() >> 4);
		BlockState floor = world.getBlockState(floorPos);
		PersonnalWorldUtil.PWWorldState state = PersonnalWorldUtil.getWorldState(world);
		boolean samePos = state.hasSpawnMarker() && state.getSpawnMarkerPos().equals(floorPos);
		String markerKey = Registries.BLOCK.getId(PersonnalWorldContent.SPAWN_MARKER.get()).toString();
		float yaw = fr.galsaxx.island.SpawnFacing.snapYaw(player.getYaw());
		if (samePos && islandId.equals(standing) && player.isOnGround()) {
			state.setSpawnMarkerYaw(yaw);
			state.markDirty();
			world.setSpawnPos(floorPos.up(), yaw);
			return RelocateOutcome.ok("message.personnalworld.spawn.moved");
		}
		SpawnMove.Result plan = SpawnMove.plan(
				islandId.equals(standing),
				player.isOnGround(),
				classify(world, floorPos, floor),
				samePos,
				state.replacedBlockId(),
				Registries.BLOCK.getId(floor.getBlock()).toString(),
				markerKey);
		if (!plan.ok()) {
			return RelocateOutcome.fail(plan.messageKey());
		}
		if (state.hasSpawnMarker()) {
			BlockPos old = state.getSpawnMarkerPos();
			world.setBlockState(old, blockFrom(plan.restoreKey(), state.replacedBlockProps(), markerKey), 3);
		}
		rememberReplaced(state, floor);
		world.setBlockState(floorPos, PersonnalWorldContent.SPAWN_MARKER.get().getDefaultState(), 3);
		state.setSpawnMarker(floorPos);
		state.setSpawnMarkerYaw(yaw);
		state.markDirty();
		world.setSpawnPos(floorPos.up(), yaw);
		return RelocateOutcome.ok("message.personnalworld.spawn.moved");
	}

	public static float getSpawnYaw(ServerWorld world) {
		PersonnalWorldUtil.PWWorldState state = PersonnalWorldUtil.getWorldState(world);
		if (state == null || !state.hasSpawnMarker()) {
			return 0.0F;
		}
		return state.getSpawnMarkerYaw();
	}

	public record RelocateOutcome(boolean ok, String messageKey, Object[] args) {
		public static RelocateOutcome ok(String key, Object... args) {
			return new RelocateOutcome(true, key, args);
		}

		public static RelocateOutcome fail(String key) {
			return new RelocateOutcome(false, key, new Object[0]);
		}
	}

	public static Optional<BlockPos> getMarkerPos(ServerWorld world) {
		PersonnalWorldUtil.PWWorldState state = PersonnalWorldUtil.getWorldState(world);
		if (!state.hasSpawnMarker()) {
			return Optional.empty();
		}
		BlockPos pos = state.getSpawnMarkerPos();
		return isMarkerBlock(world, pos) ? Optional.of(pos) : Optional.empty();
	}

	public static Optional<Vec3d> getTeleportPosition(ServerWorld world) {
		return getMarkerPos(world).map(pos -> new Vec3d(
				pos.getX() + 0.5,
				pos.getY() + 1,
				pos.getZ() + 0.5));
	}

	private static boolean isMarkerBlock(ServerWorld world, BlockPos pos) {
		return world.getBlockState(pos).isOf(PersonnalWorldContent.SPAWN_MARKER.get());
	}

	private static SpawnMove.FloorKind classify(ServerWorld world, BlockPos pos, BlockState state) {
		if (state.isAir()) {
			return SpawnMove.FloorKind.AIR;
		}
		if (state.isOf(Blocks.BARRIER)) {
			return SpawnMove.FloorKind.BARRIER;
		}
		if (state.isOf(PersonnalWorldContent.SPAWN_MARKER.get())) {
			return SpawnMove.FloorKind.MARKER;
		}
		if (state.hasBlockEntity()) {
			return SpawnMove.FloorKind.CONTAINER;
		}
		if (!state.getFluidState().isEmpty()) {
			return SpawnMove.FloorKind.FLUID;
		}
		if (!state.isSolidBlock(world, pos) || !Block.isShapeFullCube(state.getOutlineShape(world, pos))) {
			return SpawnMove.FloorKind.NOT_FULL;
		}
		return SpawnMove.FloorKind.SOLID;
	}

	private static void rememberReplaced(PersonnalWorldUtil.PWWorldState state, BlockState previous) {
		if (previous.isOf(PersonnalWorldContent.SPAWN_MARKER.get())) {
			state.setReplacedBlock("minecraft:air", "");
			return;
		}
		state.setReplacedBlock(Registries.BLOCK.getId(previous.getBlock()).toString(), propsOf(previous));
	}

	private static String propsOf(BlockState state) {
		StringBuilder out = new StringBuilder();
		for (Property<?> property : state.getProperties()) {
			if (!out.isEmpty()) {
				out.append(',');
			}
			out.append(property.getName()).append('=').append(valueName(state, property));
		}
		return out.toString();
	}

	private static <T extends Comparable<T>> String valueName(BlockState state, Property<T> property) {
		return property.name(state.get(property));
	}

	private static BlockState blockFrom(String blockId, String props, String markerKey) {
		if (blockId == null || blockId.isBlank() || blockId.equals(markerKey)) {
			return Blocks.AIR.getDefaultState();
		}
		Identifier id = Identifier.tryParse(blockId);
		Block block = id == null ? Blocks.AIR : Registries.BLOCK.get(id);
		BlockState state = block.getDefaultState();
		if (props == null || props.isBlank()) {
			return state;
		}
		for (String pair : props.split(",")) {
			int eq = pair.indexOf('=');
			if (eq <= 0) {
				continue;
			}
			String name = pair.substring(0, eq);
			String value = pair.substring(eq + 1);
			for (Property<?> property : state.getProperties()) {
				if (property.getName().equals(name)) {
					state = applyProp(state, property, value);
				}
			}
		}
		return state;
	}

	private static <T extends Comparable<T>> BlockState applyProp(BlockState state, Property<T> property, String value) {
		return property.parse(value).map(parsed -> state.with(property, parsed)).orElse(state);
	}

	// TODO darchitect/futur : setDefaultsForProfile(String profile, SpawnMarkerDefaults defaults)
}

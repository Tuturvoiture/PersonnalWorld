package fr.galsaxx.util;

import fr.galsaxx.PersonnalWorld;
import fr.galsaxx.PersonnalWorldContent;
import net.minecraft.server.world.ServerWorld;
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
			world.setBlockState(target, PersonnalWorldContent.SPAWN_MARKER.get().getDefaultState(), 3);
		}
		state.setSpawnMarker(target);
		state.markDirty();
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

	// TODO darchitect/futur : setDefaultsForProfile(String profile, SpawnMarkerDefaults defaults)
	// TODO futur : relocateMarker(ServerWorld world, BlockPos newPos)
}

package fr.galsaxx.util;

import fr.galsaxx.PersonnalWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Sécurité au TP vers un monde perso : marqueur spawn_marker, scan colonne, bedrock de secours.
 */
public final class PersonalWorldSpawnSafety {
    private PersonalWorldSpawnSafety() {}

    /**
     * Position de TP sûre : marqueur spawn_marker si valide, sinon scan + bedrock.
     */
    public static Vec3d resolveTeleportPosition(ServerWorld world) {
        PersonalWorldSpawnReference.ensureMarker(world);
        return PersonalWorldSpawnReference.getTeleportPosition(world)
                .orElseGet(() -> resolveFallback(
                        world,
                        PersonnalWorld.ISLAND_SPAWN_X,
                        PersonnalWorld.ISLAND_SPAWN_Y,
                        PersonnalWorld.ISLAND_SPAWN_Z));
    }

    /** Scan 50 blocs + bedrock Y=87 si aucun sol (inchangé). */
    static Vec3d resolveFallback(ServerWorld world, int spawnX, int spawnY, int spawnZ) {
        world.getChunk(spawnX >> 4, spawnZ >> 4);

        int minY = spawnY - PersonnalWorld.SPAWN_FALLBACK_SCAN_DEPTH;
        for (int groundY = spawnY; groundY >= minY; groundY--) {
            BlockPos ground = new BlockPos(spawnX, groundY, spawnZ);
            if (isSolidGround(world, ground) && canStandAt(world, spawnX, groundY + 1, spawnZ)) {
                return teleportAboveGround(spawnX, groundY, spawnZ);
            }
        }

        int bedrockY = spawnY - PersonnalWorld.SPAWN_BEDROCK_OFFSET_Y;
        BlockPos bedrockPos = new BlockPos(spawnX, bedrockY, spawnZ);
        world.setBlockState(bedrockPos, Blocks.BEDROCK.getDefaultState(), 3);
        return teleportAboveGround(spawnX, bedrockY, spawnZ);
    }

    private static Vec3d teleportAboveGround(int x, int groundY, int z) {
        return new Vec3d(x + 0.5, groundY + 1, z + 0.5);
    }

    private static boolean isSolidGround(ServerWorld world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return !state.getCollisionShape(world, pos).isEmpty();
    }

	private static boolean canStandAt(ServerWorld world, int x, int feetY, int z) {
		BlockPos feet = new BlockPos(x, feetY, z);
		BlockPos head = new BlockPos(x, feetY + 1, z);
		return world.getBlockState(feet).getCollisionShape(world, feet).isEmpty()
				&& world.getBlockState(head).getCollisionShape(world, head).isEmpty();
	}
}

package fr.galsaxx.util;

import fr.galsaxx.config.PersonnalWorldConfig;
import fr.galsaxx.invite.IslandIds;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.Set;

/**
 * Retour depuis le monde perso : NBT sauvegardé, sinon spawn joueur (lit / respawn vanilla / Overworld).
 */
public final class ReturnTeleport {
	private ReturnTeleport() {}

	/**
	 * @return {@code true} si un téléport a été effectué
	 */
	/**
	 * Same rules as the staff: skip dimensions listed in {@code noDimensionSavePosition},
	 * and never store a personal island (own or someone else's).
	 */
	public static void rememberIfAllowed(ServerPlayerEntity player) {
		String current = player.getServerWorld().getRegistryKey().getValue().toString();
		if (PersonnalWorldConfig.get().isNoSavePosition(current) || IslandIds.isPersonalIsland(current)) {
			return;
		}
		NbtCompound posNbt = new NbtCompound();
		posNbt.putDouble("x", player.getX());
		posNbt.putDouble("y", player.getY());
		posNbt.putDouble("z", player.getZ());
		posNbt.putFloat("yaw", player.getYaw());
		posNbt.putFloat("pitch", player.getPitch());
		posNbt.putString("dim", current);
		((ReturnPositionSaver) player).setReturnPosition(posNbt);
	}

	public static void actionBar(ServerPlayerEntity player, Text text) {
		if (!PersonnalWorldConfig.get().actionBarMessages()) {
			return;
		}
		player.sendMessage(text, true);
	}

	/** Drops a saved return that points at {@code dimensionId}, so a kick cannot walk back onto that island. */
	public static void forgetIfDimension(ServerPlayerEntity player, String dimensionId) {
		if (player == null || dimensionId == null) {
			return;
		}
		NbtCompound posNbt = ((ReturnPositionSaver) player).getReturnPosition();
		if (posNbt != null && posNbt.contains("dim") && dimensionId.equals(posNbt.getString("dim"))) {
			((ReturnPositionSaver) player).setReturnPosition(new NbtCompound());
		}
	}

	public static boolean teleportHome(ServerPlayerEntity player) {
		MinecraftServer server = player.getServer();
		if (server == null) {
			return false;
		}

		NbtCompound posNbt = ((ReturnPositionSaver) player).getReturnPosition();
		if (posNbt != null && posNbt.contains("x") && posNbt.contains("dim")) {
			Identifier dimId = Identifier.tryParse(posNbt.getString("dim"));
			if (dimId != null) {
				RegistryKey<World> dimKey = RegistryKey.of(RegistryKeys.WORLD, dimId);
				ServerWorld destination = server.getWorld(dimKey);
				if (destination != null) {
					player.teleport(
							destination,
							posNbt.getDouble("x"),
							posNbt.getDouble("y"),
							posNbt.getDouble("z"),
							Set.of(),
							posNbt.getFloat("yaw"),
							posNbt.getFloat("pitch")
					);
					actionBar(player, Text.translatable("message.personnalworld.return_to_origin"));
					return true;
				}
			}
		}

		TeleportTarget target = player.getRespawnTarget(true, TeleportTarget.NO_OP);
		player.teleportTo(target);
		actionBar(player, Text.translatable("message.personnalworld.returned_to_player_spawn"));
		return true;
	}
}

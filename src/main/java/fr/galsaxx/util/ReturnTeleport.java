package fr.galsaxx.util;

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
					player.sendMessage(Text.translatable("message.personnalworld.return_to_origin"), false);
					return true;
				}
			}
		}

		TeleportTarget target = player.getRespawnTarget(true, TeleportTarget.NO_OP);
		player.teleportTo(target);
		player.sendMessage(Text.translatable("message.personnalworld.returned_to_player_spawn"), false);
		return true;
	}
}

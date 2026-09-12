package fr.galsaxx;

import fr.galsaxx.config.PersonnalWorldConfig;
import fr.galsaxx.util.PersonalWorldSpawnSafety;
import fr.galsaxx.util.PersonnalWorldUtil;
import fr.galsaxx.util.ReturnPositionSaver;
import fr.galsaxx.util.ReturnTeleport;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Set;

public class PersonnalWorldItem extends Item {
	public PersonnalWorldItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (!world.isClient() && user instanceof ServerPlayerEntity serverPlayer) {
			MinecraftServer server = serverPlayer.getServer();
			if (server == null) {
				return new TypedActionResult<>(ActionResult.FAIL, user.getStackInHand(hand));
			}

			PersonnalWorldConfig config = PersonnalWorldConfig.get();
			int cooldown = config.staffCooldownTicks();

			String dimPath = "perso_" + user.getUuidAsString();
			Identifier dimId = Identifier.of(PersonnalWorld.MOD_ID, dimPath);
			RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, dimId);

			ServerWorld currentWorld = serverPlayer.getServerWorld();
			String currentWorldId = currentWorld.getRegistryKey().getValue().toString();

			if (currentWorldId.equals(worldKey.getValue().toString())) {
				ReturnTeleport.teleportHome(serverPlayer);
				user.getItemCooldownManager().set(this, cooldown);
				return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
			}

			if (config.isNoTeleport(currentWorldId)) {
				serverPlayer.sendMessage(Text.translatable("message.personnalworld.teleport_blocked_in_dimension"), false);
				user.getItemCooldownManager().set(this, cooldown);
				return new TypedActionResult<>(ActionResult.FAIL, user.getStackInHand(hand));
			}

			if (!config.isNoSavePosition(currentWorldId)) {
				NbtCompound posNbt = new NbtCompound();
				posNbt.putDouble("x", serverPlayer.getX());
				posNbt.putDouble("y", serverPlayer.getY());
				posNbt.putDouble("z", serverPlayer.getZ());
				posNbt.putFloat("yaw", serverPlayer.getYaw());
				posNbt.putFloat("pitch", serverPlayer.getPitch());
				posNbt.putString("dim", currentWorldId);
				((ReturnPositionSaver) serverPlayer).setReturnPosition(posNbt);
			}

			ServerWorld persoWorld = PersonnalWorldUtil.ensurePersonalWorld(
					server,
					worldKey,
					dimId,
					serverPlayer
			);

			if (persoWorld != null) {
				Vec3d safeSpawn = PersonalWorldSpawnSafety.resolveTeleportPosition(persoWorld);
				serverPlayer.teleport(
						persoWorld,
						safeSpawn.x,
						safeSpawn.y,
						safeSpawn.z,
						Set.of(),
						0.0F,
						0.0F
				);
				serverPlayer.sendMessage(Text.translatable("message.personnalworld.welcome_island"), false);
				user.getItemCooldownManager().set(this, cooldown);
				return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
			}
			serverPlayer.sendMessage(Text.translatable("message.personnalworld.personal_world_unavailable"), false);
			return new TypedActionResult<>(ActionResult.FAIL, user.getStackInHand(hand));
		}
		return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
	}
}

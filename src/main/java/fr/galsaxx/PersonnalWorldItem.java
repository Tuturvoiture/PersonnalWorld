package fr.galsaxx;

import fr.galsaxx.config.PersonnalWorldConfig;
import fr.galsaxx.util.PersonalWorldSpawnReference;
import fr.galsaxx.util.PersonalWorldSpawnSafety;
import fr.galsaxx.util.PersonnalWorldUtil;
import fr.galsaxx.util.ReturnTeleport;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

			fr.galsaxx.invite.AccessFileStore.get().bindServer(server);
			java.util.List<fr.galsaxx.invite.AccessRecord> owned = fr.galsaxx.invite.AccessFileStore.get().listByOwner(user.getUuid());
			String dimIdString;
			if (owned.isEmpty()) {
				dimIdString = fr.galsaxx.invite.IslandIds.dimensionIdForPlayer(user.getUuid());
			} else {
				fr.galsaxx.invite.AccessRecord chosen = owned.get(0);
				for (fr.galsaxx.invite.AccessRecord record : owned) {
					if (record.active()) {
						chosen = record;
						break;
					}
				}
				dimIdString = chosen.dimensionId();
			}
			Identifier dimId = Identifier.tryParse(dimIdString);
			if (dimId == null) {
				return new TypedActionResult<>(ActionResult.FAIL, user.getStackInHand(hand));
			}
			RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, dimId);

			ServerWorld currentWorld = serverPlayer.getServerWorld();
			String currentWorldId = currentWorld.getRegistryKey().getValue().toString();

			if (fr.galsaxx.invite.IslandIds.isPersonalIsland(currentWorldId)) {
				if (!ownsCurrentIsland(server, serverPlayer, currentWorldId)) {
					ReturnTeleport.forgetIfDimension(serverPlayer, currentWorldId);
				}
				ReturnTeleport.teleportHome(serverPlayer);
				user.getItemCooldownManager().set(this, cooldown);
				return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
			}

			if (config.isNoTeleport(currentWorldId)) {
				serverPlayer.sendMessage(Text.translatable("message.personnalworld.teleport_blocked_in_dimension"), false);
				user.getItemCooldownManager().set(this, cooldown);
				return new TypedActionResult<>(ActionResult.FAIL, user.getStackInHand(hand));
			}

			ReturnTeleport.rememberIfAllowed(serverPlayer);

			ServerWorld persoWorld = PersonnalWorldUtil.ensurePersonalWorld(
					server,
					worldKey,
					dimId,
					serverPlayer
			);

			if (persoWorld != null) {
				Vec3d safeSpawn = PersonalWorldSpawnSafety.resolveTeleportPosition(persoWorld);
				float yaw = PersonalWorldSpawnReference.getSpawnYaw(persoWorld);
				serverPlayer.teleport(
						persoWorld,
						safeSpawn.x,
						safeSpawn.y,
						safeSpawn.z,
						Set.of(),
						yaw,
						0.0F
				);
				ReturnTeleport.actionBar(serverPlayer, Text.translatable("message.personnalworld.welcome_island"));
				user.getItemCooldownManager().set(this, cooldown);
				return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
			}
			serverPlayer.sendMessage(Text.translatable("message.personnalworld.personal_world_unavailable"), false);
			return new TypedActionResult<>(ActionResult.FAIL, user.getStackInHand(hand));
		}
		return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
	}

	/** Owner logique, ou créateur du chemin tant que le fichier ne dit pas le contraire. */
	private static boolean ownsCurrentIsland(MinecraftServer server, ServerPlayerEntity player, String dimensionId) {
		fr.galsaxx.invite.AccessFileStore.get().bindServer(server);
		java.util.Optional<fr.galsaxx.invite.AccessRecord> record =
				fr.galsaxx.invite.AccessFileStore.get().getCached(dimensionId);
		if (record.isPresent()) {
			return player.getUuid().equals(record.get().ownerUuid());
		}
		return player.getUuid().equals(fr.galsaxx.invite.IslandIds.creatorUuidFromDimensionId(dimensionId).orElse(null));
	}
}

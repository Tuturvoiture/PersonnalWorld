package fr.galsaxx;

import fr.galsaxx.util.PersonnalWorldUtil;
import fr.galsaxx.util.ReturnPositionSaver;
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
import net.minecraft.util.math.BlockPos;
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

            String dimPath = "perso_" + user.getUuidAsString();
            Identifier dimId = Identifier.of(PersonnalWorld.MOD_ID, dimPath);
            RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, dimId);

            ServerWorld currentWorld = serverPlayer.getServerWorld();
            String currentWorldId = currentWorld.getRegistryKey().getValue().toString();
            String overworld = World.OVERWORLD.getValue().toString();
            String nether = World.NETHER.getValue().toString();
            String end = World.END.getValue().toString();

            if (currentWorldId.equals(worldKey.getValue().toString())) {
                NbtCompound posNbt = ((ReturnPositionSaver) serverPlayer).getReturnPosition();
                if (posNbt != null && posNbt.contains("x")) {
                    Identifier dimIdReturn = Identifier.tryParse(posNbt.getString("dim"));
                    RegistryKey<World> dimKey = RegistryKey.of(RegistryKeys.WORLD, dimIdReturn);
                    ServerWorld destination = server.getWorld(dimKey);
                    if (destination != null) {
                        serverPlayer.teleport(
                                destination,
                                posNbt.getDouble("x"),
                                posNbt.getDouble("y"),
                                posNbt.getDouble("z"),
                                Set.of(),
                                posNbt.getFloat("yaw"),
                                posNbt.getFloat("pitch")
                        );
                        serverPlayer.sendMessage(Text.translatable("message.personnalworld.return_to_origin"), false);
                    } else {
                        serverPlayer.sendMessage(Text.translatable("message.personnalworld.origin_dimension_not_found"), false);
                    }
                } else {
                    serverPlayer.sendMessage(Text.translatable("message.personnalworld.no_saved_position"), false);
                }
                user.getItemCooldownManager().set(this, 40);
                return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
            }

            if (currentWorldId.equals(overworld) || currentWorldId.equals(nether) || currentWorldId.equals(end)) {
                NbtCompound posNbt = new NbtCompound();
                posNbt.putDouble("x", serverPlayer.getX());
                posNbt.putDouble("y", serverPlayer.getY());
                posNbt.putDouble("z", serverPlayer.getZ());
                posNbt.putFloat("yaw", serverPlayer.getYaw());
                posNbt.putFloat("pitch", serverPlayer.getPitch());
                posNbt.putString("dim", serverPlayer.getWorld().getRegistryKey().getValue().toString());
                ((ReturnPositionSaver) serverPlayer).setReturnPosition(posNbt);
            }

            ServerWorld persoWorld = PersonnalWorldUtil.ensurePersonalWorld(
                    server,
                    worldKey,
                    dimId,
                    serverPlayer
            );

            if (persoWorld != null) {
                BlockPos spawnPos = new BlockPos(24, 68, 17);
                serverPlayer.teleport(
                        persoWorld,
                        spawnPos.getX() + 0.5,
                        spawnPos.getY(),
                        spawnPos.getZ() + 0.5,
                        Set.of(),
                        0.0F,
                        0.0F
                );
                serverPlayer.sendMessage(Text.translatable("message.personnalworld.welcome_island"), false);
                user.getItemCooldownManager().set(this, 40);
                return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
            }
            serverPlayer.sendMessage(Text.translatable("message.personnalworld.personal_world_unavailable"), false);
            return new TypedActionResult<>(ActionResult.FAIL, user.getStackInHand(hand));
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
    }
}

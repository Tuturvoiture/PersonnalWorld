package fr.galsaxx;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.text.Text;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.util.Identifier;


import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.minecraft.nbt.NbtCompound;
import fr.galsaxx.util.ReturnPositionSaver;
import fr.galsaxx.util.IslandGenerator;

import qouteall.dimlib.api.DimensionAPI;

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
            Identifier dimId = Identifier.of("personnalworld", dimPath);
            RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, dimId);
            RegistryKey<DimensionType> dimTypeKey = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, Identifier.of("minecraft", "overworld"));
            RegistryEntry<DimensionType> dimTypeEntry = server.getRegistryManager().get(RegistryKeys.DIMENSION_TYPE).getEntry(dimTypeKey).orElse(null);

            if (dimTypeEntry == null) {
                serverPlayer.sendMessage(Text.translatable("message.personnalworld.cannot_get_dimension_type"), false);
                return new TypedActionResult<>(ActionResult.FAIL, user.getStackInHand(hand));
            }

            ServerWorld currentWorld = serverPlayer.getServerWorld();
            String currentWorldId = currentWorld.getRegistryKey().getValue().toString();
            String overworld = World.OVERWORLD.getValue().toString();
            String nether = World.NETHER.getValue().toString();
            String end = World.END.getValue().toString();

            // Retour au monde d'origine
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
            else {
                // Sauvegarde la position
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

                // Création du monde perso s'il n'existe pas
                /**if (server.getWorld(worldKey) == null) {
                    // ==== Générateur basé sur preset vanilla "the_void" ====
                    var registryManager = server.getRegistryManager();
                    var presetRegistry = registryManager.get(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET);

                    var voidPresetKey = RegistryKey.of(
                            RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET,
                            Identifier.of("minecraft", "the_void")
                    );

                    var voidPresetEntry = presetRegistry.getEntry(voidPresetKey).orElse(null);
                    if (voidPresetEntry == null) {
                        serverPlayer.sendMessage(Text.literal("[personnalworld] Erreur : preset 'minecraft:the_void' introuvable."), false);
                        return new TypedActionResult<>(ActionResult.FAIL, user.getStackInHand(hand));
                    }

                    FlatChunkGeneratorConfig flatConfig = voidPresetEntry.value().settings();
                    FlatChunkGenerator flatChunkGen = new FlatChunkGenerator(flatConfig);
                    DimensionOptions dimOptions = new DimensionOptions(dimTypeEntry, flatChunkGen);

                    DimensionAPI.addDimensionDynamically(server, dimId, dimOptions);

                    // Attend que la dimension soit prête
                    int retry = 0;
                    while (server.getWorld(worldKey) == null && retry++ < 50) {
                        try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                    }

                    // Génère l'île
                    ServerWorld persoWorld = server.getWorld(worldKey);
                    if (persoWorld != null) {
                        persoWorld.getServer().submit(() -> {
                            BlockPos center = new BlockPos(0, 50, 0);
                            persoWorld.getChunk(center.getX() >> 4, center.getZ() >> 4);
                            IslandGenerator.generateIsland(persoWorld, serverPlayer);
                        });
                    }
                }
                **/

                ServerWorld persoWorld = fr.galsaxx.util.PersonnalWorldUtil.ensurePersonalWorld(
                        server,
                        dimTypeEntry,
                        worldKey,
                        dimId,
                        serverPlayer
                );

                // Téléportation vers le monde perso
                ServerWorld persoWorld2 = server.getWorld(worldKey);
                if (persoWorld2 != null) {
                    BlockPos spawnPos = new BlockPos(24, 68, 17);
                    serverPlayer.teleport(
                            persoWorld2,
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
                } else {
                    serverPlayer.sendMessage(Text.translatable("message.personnalworld.personal_world_unavailable"), false);
                    return new TypedActionResult<>(ActionResult.FAIL, user.getStackInHand(hand));
                }
            }
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
    }
}

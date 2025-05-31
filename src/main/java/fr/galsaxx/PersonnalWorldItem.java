package fr.galsaxx;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.text.Text;

import qouteall.dimlib.api.DimensionAPI;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import java.util.Set;

import net.minecraft.nbt.NbtCompound;
import fr.galsaxx.util.ReturnPositionSaver; // Ton interface de mixin

public class PersonnalWorldItem extends Item {
    public PersonnalWorldItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient() && user instanceof ServerPlayerEntity serverPlayer) {
            MinecraftServer server = serverPlayer.getServer();
            if (server == null) return new TypedActionResult<>(ActionResult.FAIL, user.getStackInHand(hand));

            String dimPath = "perso_" + user.getUuidAsString();
            Identifier dimId = Identifier.of("personnalworld", dimPath);
            RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, dimId);
            RegistryKey<DimensionType> dimTypeKey = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, Identifier.of("minecraft", "overworld"));
            RegistryEntry<DimensionType> dimTypeEntry = server.getRegistryManager().get(RegistryKeys.DIMENSION_TYPE).getEntry(dimTypeKey).orElse(null);

            if (dimTypeEntry == null) {
                serverPlayer.sendMessage(Text.literal("Erreur : Impossible de récupérer le type de dimension !"), false);
                return new TypedActionResult<>(ActionResult.FAIL, user.getStackInHand(hand));
            }

            ServerWorld currentWorld = serverPlayer.getServerWorld();
            String currentWorldId = currentWorld.getRegistryKey().getValue().toString();
            String overworld = World.OVERWORLD.getValue().toString();
            String nether = World.NETHER.getValue().toString();
            String end = World.END.getValue().toString();

            // Lecture/sauvegarde de position via NBT joueur (ReturnPositionSaver)
            if (currentWorldId.equals(worldKey.getValue().toString())) {
                // Déjà dans le monde perso : ramener à la dernière position connue dans un monde classique
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
                        serverPlayer.sendMessage(Text.literal("Retour à votre position d'origine !"), false);
                    } else {
                        serverPlayer.sendMessage(Text.literal("Dimension d'origine introuvable !"), false);
                    }
                } else {
                    serverPlayer.sendMessage(Text.literal("Aucune position sauvegardée trouvée !"), false);
                }
            } else {
                // Si on est dans l'overworld, nether ou end : sauvegarde la position avant d'aller dans le monde perso
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

                // Création dynamique de la dimension si besoin
                if (server.getWorld(worldKey) == null) {
                    DimensionOptions dimOptions = new DimensionOptions(dimTypeEntry, server.getOverworld().getChunkManager().getChunkGenerator());
                    DimensionAPI.addDimensionDynamically(server, dimId, dimOptions);
                    serverPlayer.sendMessage(Text.literal("Création de votre monde personnel..."), false);
                }

                // Téléportation au spawn du monde perso
                ServerWorld persoWorld = server.getWorld(worldKey);
                if (persoWorld != null) {
                    BlockPos spawn = persoWorld.getSpawnPos();
                    float yaw = persoWorld.getSpawnAngle();
                    serverPlayer.teleport(
                            persoWorld,
                            spawn.getX() + 0.5,
                            spawn.getY(),
                            spawn.getZ() + 0.5,
                            Set.of(),
                            yaw,
                            0.0F
                    );
                    serverPlayer.sendMessage(Text.literal("Bienvenue dans votre monde perso !"), false);
                } else {
                    serverPlayer.sendMessage(Text.literal("Erreur : monde perso inaccessible."), false);
                }
            }
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
    }
}

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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class PersonnalWorldItem extends Item {
    // Simple sauvegarde en mémoire (non persistante, pour tester rapidement)
    private static final Map<String, double[]> lastPositions = new HashMap<>();

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

            if (currentWorldId.equals(worldKey.getValue().toString())) {
                // Déjà dans le monde perso : ramener à la dernière position connue dans un monde classique
                double[] pos = lastPositions.get(serverPlayer.getUuidAsString());
                if (pos != null) {
                    ServerWorld destination = server.getWorld(World.OVERWORLD); // tu peux améliorer pour gérer nether/end
                    serverPlayer.teleport(
                            destination,
                            pos[0], pos[1], pos[2],
                            Set.of(),
                            (float)pos[3], (float)pos[4]
                    );
                    serverPlayer.sendMessage(Text.literal("Retour à votre position d'origine !"), false);
                } else {
                    serverPlayer.sendMessage(Text.literal("Aucune position sauvegardée trouvée !"), false);
                }
            } else {
                // Si on est dans l'overworld, nether ou end : sauvegarde la position avant d'aller dans le monde perso
                if (currentWorldId.equals(overworld) || currentWorldId.equals(nether) || currentWorldId.equals(end)) {
                    lastPositions.put(serverPlayer.getUuidAsString(), new double[]{
                            serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                            serverPlayer.getYaw(), serverPlayer.getPitch()
                    });
                }

                // Création dynamique de la dimension si besoin
                if (server.getWorld(worldKey) == null) {
                    DimensionOptions dimOptions = new DimensionOptions(dimTypeEntry, server.getOverworld().getChunkManager().getChunkGenerator());
                    DimensionAPI.addDimensionDynamically(server, dimId, dimOptions);
                    serverPlayer.sendMessage(Text.literal("Création de votre monde personnel..."), false);
                }

                // Téléportation au spawn du monde perso (plus moveToWorld, signature Fabric 1.21.1)
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

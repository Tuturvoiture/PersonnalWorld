package fr.galsaxx.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;

import qouteall.dimlib.api.DimensionAPI;

/**
 * Déplacement pur de la fonctionnalité de création/initialisation du monde perso.
 * - Crée la dimension via le preset vanilla "minecraft:the_void"
 * - Attend que le monde soit prêt
 * - Nettoie la petite plateforme générée par le preset (une seule fois)
 * - Génère l'île NBT (une seule fois)
 * - Fixe un spawn (optionnel, même valeur que ton code)
 *
 * Version ciblée : Minecraft 1.21.1 (Java 21) – mappings Yarn récents.
 */
public final class PersonnalWorldUtil {
    private PersonnalWorldUtil() {}

    /**
     * Assure l’existence et l’initialisation du monde perso.
     * Si le monde existe déjà, ne refait PAS la génération/clean grâce au PersistentState.
     *
     * @param server       serveur
     * @param dimTypeEntry type de dimension (ex. overworld)
     * @param worldKey     clé du monde (personnalworld:perso_<uuid>)
     * @param dimId        identifiant dimension (même valeur que worldKey.value())
     * @param owner        joueur propriétaire (pour messages et IslandGenerator)
     * @return le ServerWorld prêt, ou null si échec
     */
    public static ServerWorld ensurePersonalWorld(MinecraftServer server,
                                                  RegistryEntry<DimensionType> dimTypeEntry,
                                                  RegistryKey<World> worldKey,
                                                  Identifier dimId,
                                                  ServerPlayerEntity owner) {
        // 1) Monde déjà présent ?
        ServerWorld existing = server.getWorld(worldKey);
        if (existing != null) {
            runOneTimeInitIfNeeded(existing, owner);
            return existing;
        }

        // 2) Création via preset "the_void" (pas de structures)
        var registryManager = server.getRegistryManager();
        var presetRegistry = registryManager.get(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET);

        RegistryKey<FlatLevelGeneratorPreset> voidPresetKey = RegistryKey.of(
                RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET,
                Identifier.of("minecraft", "the_void")
        );

        var voidPresetEntry = presetRegistry.getEntry(voidPresetKey).orElse(null);
        if (voidPresetEntry == null) {
            if (owner != null) {
                owner.sendMessage(Text.literal("[personnalworld] Erreur : preset 'minecraft:the_void' introuvable."), false);
            }
            return null;
        }

        FlatChunkGeneratorConfig flatConfig = voidPresetEntry.value().settings();
        FlatChunkGenerator flatChunkGen = new FlatChunkGenerator(flatConfig);
        DimensionOptions dimOptions = new DimensionOptions(dimTypeEntry, flatChunkGen);

        // Ajout dynamique
        DimensionAPI.addDimensionDynamically(server, dimId, dimOptions);

        // 3) Attente courte que le monde soit prêt
        int retry = 0;
        while (server.getWorld(worldKey) == null && retry++ < 50) {
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }

        ServerWorld persoWorld = server.getWorld(worldKey);
        if (persoWorld == null) {
            if (owner != null) {
                owner.sendMessage(Text.literal("[personnalworld] Erreur : monde perso indisponible après création."), false);
            }
            return null;
        }

        // 4) Initialisation "une seule fois"
        runOneTimeInitIfNeeded(persoWorld, owner);
        return persoWorld;
    }

    /** Exécute le nettoyage de la plateforme + génération de l’île une seule fois par monde. */
    private static void runOneTimeInitIfNeeded(ServerWorld world, ServerPlayerEntity owner) {
        PWWorldState state = PWWorldState.get(world);
        if (state.initialized) return;

        // On programme sur le thread serveur
        world.getServer().submit(() -> {
            // Charge au moins le chunk 0,0 avant modifications de blocs
            BlockPos center = new BlockPos(0, 50, 0);
            world.getChunk(center.getX() >> 4, center.getZ() >> 4);

            // centre médian ~ (8, -61, 8), largeur exacte 40 (half=20)
            // Y de -65 à -58 (8 couches)
            int half = 20;
            int centerX = 8;
            int centerZ = 8;

            clearPlatformBox(
                    world,
                    centerX - half,  -65,
                    centerZ - half,
                    centerX + half - 1, -58,
                    centerZ + half - 1
            );



            // (B) Génére l'île (ta méthode existante)
            try {
                IslandGenerator.generateIsland(world, owner);
            } catch (Throwable t) {
                if (owner != null) {
                    owner.sendMessage(Text.literal("[personnalworld] Avertissement : échec génération île (" + t.getClass().getSimpleName() + ")."), false);
                }
            }

            // (C) Optionnel : fixe le spawn (les mêmes coords que ton code)
            world.setSpawnPos(new BlockPos(24, 68, 17), 0.0F);

            // Flag persistant
            state.initialized = true;
            state.markDirty();
        });
    }

    /**
     * Supprime la plateforme posée par le preset autour de (0,0) sur une mince tranche verticale.
     * Ajuste yMin/yMax si tu connais précisément la hauteur de la plateforme (souvent 4..5).
     */
    /** Supprime une zone rectangulaire de largeur 2*halfWidth (≈40) et hauteur 3 autour d'un centre donné. */
    // (inchangé)
    private static void clearPlatformBox(ServerWorld world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        int minChunkX = Math.floorDiv(minX, 16);
        int maxChunkX = Math.floorDiv(maxX, 16);
        int minChunkZ = Math.floorDiv(minZ, 16);
        int maxChunkZ = Math.floorDiv(maxZ, 16);
        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                world.getChunk(cx, cz);
            }
        }
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos p = new BlockPos(x, y, z);
                    if (!world.isAir(p)) {
                        world.setBlockState(p, net.minecraft.block.Blocks.AIR.getDefaultState(), 3);
                    }
                }
            }
        }
    }



    /** PersistentState minimal pour ne faire l'init (clean + île) qu'une seule fois. */
    static final class PWWorldState extends PersistentState {
        boolean initialized = false;

        // Type moderne requis par getOrCreate(...) en 1.21.x
        static final Type<PWWorldState> TYPE = new Type<>(
                PWWorldState::new,           // constructeur par défaut
                PWWorldState::readNbt,       // lecteur depuis NBT
                null                         // pas de DataFixer
        );

        PWWorldState() {}

        static PWWorldState readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
            PWWorldState s = new PWWorldState();
            s.initialized = nbt.getBoolean("initialized");
            return s;
        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
            nbt.putBoolean("initialized", initialized);
            return nbt;
        }

        static PWWorldState get(ServerWorld world) {
            PersistentStateManager mgr = world.getPersistentStateManager();
            return mgr.getOrCreate(TYPE, "personnalworld_init");
        }
    }
}

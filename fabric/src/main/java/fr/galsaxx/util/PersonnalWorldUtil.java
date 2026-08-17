package fr.galsaxx.util;

import fr.galsaxx.PersonnalWorld;
import net.darchitect.api.DimensionArchitectRuntime;
import net.darchitect.api.ModCallContext;
import net.darchitect.api.WorldType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

/**
 * Demande la dimension perso à DimensionArchitect (VOID), puis pose l’île NBT une seule fois.
 */
public final class PersonnalWorldUtil {
    private PersonnalWorldUtil() {}

    /**
     * Assure l’existence et l’initialisation du monde perso.
     * Si le monde existe déjà, ne refait PAS la génération grâce au PersistentState.
     */
    public static ServerWorld ensurePersonalWorld(MinecraftServer server,
                                                  RegistryKey<World> worldKey,
                                                  Identifier dimId,
                                                  ServerPlayerEntity owner) {
        ServerWorld existing = server.getWorld(worldKey);
        if (existing != null) {
            runOneTimeInitIfNeeded(existing, owner);
            return existing;
        }

        try {
            ModCallContext.runAs(PersonnalWorld.MOD_ID, () -> {
                var api = DimensionArchitectRuntime.get();
                String id = dimId.toString();
                if (!api.hasDimension(id)) {
                    api.builder(id).type(WorldType.VOID).build();
                }
            });
        } catch (IllegalArgumentException already) {
            String msg = already.getMessage();
            if (msg == null || !msg.contains("already exists")) {
                if (owner != null) {
                    owner.sendMessage(Text.literal("[personnalworld] Erreur : " + already.getMessage()), false);
                }
                return null;
            }
        } catch (RuntimeException e) {
            if (owner != null) {
                owner.sendMessage(Text.literal("[personnalworld] Erreur création dimension : " + e.getMessage()), false);
            }
            return null;
        }

        ServerWorld persoWorld = server.getWorld(worldKey);
        if (persoWorld == null) {
            if (owner != null) {
                owner.sendMessage(Text.literal("[personnalworld] Erreur : monde perso indisponible après création."), false);
            }
            return null;
        }

        runOneTimeInitIfNeeded(persoWorld, owner);
        return persoWorld;
    }

    /** Génère l’île NBT une seule fois par monde. */
    private static void runOneTimeInitIfNeeded(ServerWorld world, ServerPlayerEntity owner) {
        PWWorldState state = PWWorldState.get(world);
        if (state.initialized) return;

        world.getServer().submit(() -> {
            BlockPos center = new BlockPos(0, 50, 0);
            world.getChunk(center.getX() >> 4, center.getZ() >> 4);

            try {
                IslandGenerator.generateIsland(world, owner);
            } catch (Throwable t) {
                if (owner != null) {
                    owner.sendMessage(Text.literal("[personnalworld] Avertissement : échec génération île (" + t.getClass().getSimpleName() + ")."), false);
                }
            }

            world.setSpawnPos(new BlockPos(24, 68, 17), 0.0F);

            state.initialized = true;
            state.markDirty();
        });
    }

    static final class PWWorldState extends PersistentState {
        boolean initialized = false;

        static final Type<PWWorldState> TYPE = new Type<>(
                PWWorldState::new,
                PWWorldState::readNbt,
                null
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

package fr.galsaxx.util;

import fr.galsaxx.PersonnalWorld;
import fr.galsaxx.config.PersonnalWorldConfig;
import fr.galsaxx.invite.AccessFileStore;
import fr.galsaxx.invite.AccessRecord;
import fr.galsaxx.invite.DArchitectAccess;
import fr.galsaxx.invite.IslandDirectory;
import net.darchitect.api.AccessMode;
import net.darchitect.api.DimensionAlreadyExistsException;
import net.darchitect.api.DimensionArchitectRuntime;
import net.darchitect.api.ModCallContext;
import net.darchitect.api.WorldType;
import net.darchitect.api.worldprofile.StructureGenerationRules;
import net.darchitect.api.worldprofile.WorldPreset;
import net.darchitect.api.worldprofile.WorldProfile;
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

import java.util.UUID;
import java.nio.file.Path;

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
		AccessFileStore.get().bindServer(server);
        ServerWorld existing = server.getWorld(worldKey);
        if (existing != null) {
            runOneTimeInitIfNeeded(existing, owner);
			ensureAccessForWorld(server, dimId.toString(), owner);
            return existing;
        }

        try {
            ModCallContext.runAs(PersonnalWorld.MOD_ID, () -> {
                var api = DimensionArchitectRuntime.get();
                String id = dimId.toString();
                if (!api.hasDimension(id)) {
					UUID ownerUuid = owner != null ? owner.getUuid() : null;
                    var builder = api.builder(id)
                            .type(WorldType.VOID)
                            // shareInventory=true → isolatePlayerData(false) (DArchitect ≥ 0.0.58).
                            .isolatePlayerData(!PersonnalWorldConfig.get().shareInventory())
							.accessMode(AccessMode.MANAGED)
                            // VOID API 0.0.44 : Optional.empty() = structures vanilla. SKYBLOCK les coupe.
                            .worldProfile(WorldProfile.builder()
                                    .preset(WorldPreset.SKYBLOCK)
                                    .structures(StructureGenerationRules.none())
                                    .build());
					if (ownerUuid != null) {
						builder.owner(ownerUuid);
					}
					builder.build();
                }
            });
        } catch (DimensionAlreadyExistsException ignored) {
            // Course : dim créée entre hasDimension et build — continuer vers getWorld.
        } catch (IllegalArgumentException already) {
            String msg = already.getMessage();
            if (msg == null || !msg.contains("already exists")) {
                if (owner != null) {
                    owner.sendMessage(Text.translatable("message.personnalworld.dimension_already_exists_error", already.getMessage()), false);
                }
                return null;
            }
        } catch (RuntimeException e) {
            if (owner != null) {
                owner.sendMessage(Text.translatable("message.personnalworld.dimension_create_error", e.getMessage()), false);
            }
            return null;
        }

        ServerWorld persoWorld = server.getWorld(worldKey);
        if (persoWorld == null) {
            if (owner != null) {
                owner.sendMessage(Text.translatable("message.personnalworld.dimension_unavailable_after_create"), false);
            }
            return null;
        }

        runOneTimeInitIfNeeded(persoWorld, owner);
		ensureAccessForWorld(server, dimId.toString(), owner);
        return persoWorld;
    }

	private static void ensureAccessForWorld(MinecraftServer server, String dimensionId, ServerPlayerEntity ownerHint) {
		UUID ownerUuid = ownerHint != null
				? ownerHint.getUuid()
				: fr.galsaxx.invite.IslandIds.creatorUuidFromDimensionId(dimensionId).orElse(null);
		String nameHint = ownerHint != null ? ownerHint.getGameProfile().getName() : "";
		Path accessFile = AccessFileStore.get().accessRoot()
				.resolve(fr.galsaxx.invite.IslandIds.fileKey(dimensionId) + ".json");
		AccessRecord record;
		if (AccessFileStore.get().getCached(dimensionId).isEmpty() && !java.nio.file.Files.isRegularFile(accessFile)) {
			record = AccessFileStore.createInitial(dimensionId, ownerUuid, nameHint);
			// First persist: keep revision at 1 (saveMutation bumps).
			record.setRevision(0L);
			AccessFileStore.get().saveMutation(record);
		} else {
			record = AccessFileStore.get().loadOrRecover(dimensionId, ownerUuid, nameHint);
			DArchitectAccess.applyRecord(record);
		}
		IslandDirectory.get().registerOrUpdate(record);

		Identifier id = Identifier.tryParse(dimensionId);
		if (id == null) {
			return;
		}
		ServerWorld world = server.getWorld(RegistryKey.of(net.minecraft.registry.RegistryKeys.WORLD, id));
		PWWorldState state = PWWorldState.get(world);
		if (state != null && !state.accessMigrated) {
			state.accessMigrated = true;
			if (record.ownerUuid() != null) {
				state.ownerUuid = record.ownerUuid().toString();
			}
			state.markDirty();
		}
	}

    static PWWorldState getWorldState(ServerWorld world) {
        return PWWorldState.get(world);
    }

	/** Génère l’île NBT une seule fois par monde (synchrone — avant le TP). */
	private static void runOneTimeInitIfNeeded(ServerWorld world, ServerPlayerEntity owner) {
		PWWorldState state = PWWorldState.get(world);
		if (state.initialized || state.initializing) {
			return;
		}
		state.initializing = true;

		BlockPos origin = new BlockPos(
				PersonnalWorld.ISLAND_NBT_ORIGIN_X,
				PersonnalWorld.ISLAND_NBT_ORIGIN_Y,
				PersonnalWorld.ISLAND_NBT_ORIGIN_Z);
		world.getChunk(origin.getX() >> 4, origin.getZ() >> 4);

		try {
			IslandGenerator.generateIsland(world, owner);
		} catch (Throwable t) {
			if (owner != null) {
				owner.sendMessage(Text.translatable("message.personnalworld.island_generate_failed", t.getClass().getSimpleName()), false);
			}
		}

		PersonalWorldSpawnReference.ensureMarker(world);

		world.setSpawnPos(new BlockPos(PersonnalWorld.ISLAND_SPAWN_X, PersonnalWorld.ISLAND_SPAWN_Y, PersonnalWorld.ISLAND_SPAWN_Z), 0.0F);

		state.initialized = true;
		state.initializing = false;
		state.markDirty();
	}

	static final class PWWorldState extends PersistentState {
		boolean initialized = false;
		/** Évite une double génération si ensurePersonalWorld est rappelé pendant l’init. */
		transient boolean initializing = false;
		private boolean hasSpawnMarker = false;
        private int spawnMarkerX;
        private int spawnMarkerY;
        private int spawnMarkerZ;
        /** Réservé : profil d’île pour futures dims (non utilisé en MVP). */
        private String islandProfile = "";
		/** Soft migration MANAGED / access file done. */
		boolean accessMigrated = false;
		/** Logical owner UUID string (may differ from path uuid after debug setowner). */
		String ownerUuid = "";

        static final Type<PWWorldState> TYPE = new Type<>(
                PWWorldState::new,
                PWWorldState::readNbt,
                null
        );

        PWWorldState() {}

        boolean hasSpawnMarker() {
            return hasSpawnMarker;
        }

        BlockPos getSpawnMarkerPos() {
            return new BlockPos(spawnMarkerX, spawnMarkerY, spawnMarkerZ);
        }

        void setSpawnMarker(BlockPos pos) {
            hasSpawnMarker = true;
            spawnMarkerX = pos.getX();
            spawnMarkerY = pos.getY();
            spawnMarkerZ = pos.getZ();
        }

        String getIslandProfile() {
            return islandProfile;
        }

        static PWWorldState readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
            PWWorldState s = new PWWorldState();
            s.initialized = nbt.getBoolean("initialized");
            s.hasSpawnMarker = nbt.getBoolean("hasSpawnMarker");
            if (s.hasSpawnMarker) {
                s.spawnMarkerX = nbt.getInt("spawnMarkerX");
                s.spawnMarkerY = nbt.getInt("spawnMarkerY");
                s.spawnMarkerZ = nbt.getInt("spawnMarkerZ");
            }
            if (nbt.contains("islandProfile")) {
                s.islandProfile = nbt.getString("islandProfile");
            }
			s.accessMigrated = nbt.contains("accessMigrated") && nbt.getBoolean("accessMigrated");
			if (nbt.contains("ownerUuid")) {
				s.ownerUuid = nbt.getString("ownerUuid");
			}
            return s;
        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
            nbt.putBoolean("initialized", initialized);
            nbt.putBoolean("hasSpawnMarker", hasSpawnMarker);
            if (hasSpawnMarker) {
                nbt.putInt("spawnMarkerX", spawnMarkerX);
                nbt.putInt("spawnMarkerY", spawnMarkerY);
                nbt.putInt("spawnMarkerZ", spawnMarkerZ);
            }
            if (!islandProfile.isEmpty()) {
                nbt.putString("islandProfile", islandProfile);
            }
			nbt.putBoolean("accessMigrated", accessMigrated);
			if (ownerUuid != null && !ownerUuid.isEmpty()) {
				nbt.putString("ownerUuid", ownerUuid);
			}
            return nbt;
        }

        static PWWorldState get(ServerWorld world) {
			if (world == null) {
				return null;
			}
            PersistentStateManager mgr = world.getPersistentStateManager();
            return mgr.getOrCreate(TYPE, "personnalworld_init");
        }
    }
}

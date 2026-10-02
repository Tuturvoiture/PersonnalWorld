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
    /**
     * True when the dimension is loaded, registered in DA, or already known via access JSON
     * (persisted island that may be unloaded after idle / restart).
     */
    public static boolean personalDimensionExists(MinecraftServer server, String dimensionId) {
        Identifier id = Identifier.tryParse(dimensionId);
        if (id == null) {
            return false;
        }
        if (server.getWorld(RegistryKey.of(net.minecraft.registry.RegistryKeys.WORLD, id)) != null) {
            return true;
        }
        try {
            boolean[] present = {false};
            ModCallContext.runAs(PersonnalWorld.MOD_ID, () ->
                    present[0] = DimensionArchitectRuntime.get().hasDimension(dimensionId));
            if (present[0]) {
                return true;
            }
        } catch (RuntimeException ignored) {
            // fall through to access-file check
        }
        AccessFileStore.get().bindServer(server);
        if (AccessFileStore.get().getCached(dimensionId).isPresent()) {
            return true;
        }
        Path accessRoot = AccessFileStore.get().accessRoot();
        if (accessRoot == null) {
            return false;
        }
        return java.nio.file.Files.isRegularFile(
                accessRoot.resolve(fr.galsaxx.invite.IslandIds.fileKey(dimensionId) + ".json"));
    }

    /**
     * Opens an island that already exists (access JSON / DA persistence). Never creates a new dim.
     * Safe for {@code /pw visit} with host offline when the world was unloaded.
     */
    public static ServerWorld openExistingPersonalWorld(MinecraftServer server,
                                                        RegistryKey<World> worldKey,
                                                        Identifier dimId,
                                                        ServerPlayerEntity ownerHint) {
        AccessFileStore.get().bindServer(server);
        ServerWorld existing = server.getWorld(worldKey);
        if (existing != null) {
            runOneTimeInitIfNeeded(existing, ownerHint);
            ensureAccessForWorld(server, dimId.toString(), ownerHint);
            return existing;
        }

        String id = dimId.toString();
        try {
            ModCallContext.runAs(PersonnalWorld.MOD_ID, () -> {
                var api = DimensionArchitectRuntime.get();
                if (api.hasDimension(id)) {
                    return;
                }
                tryLoadPersistedDimension(api, id);
            });
        } catch (RuntimeException e) {
            PersonnalWorld.LOGGER.warn("Failed to open existing personal world {}: {}", id, e.toString());
            return null;
        }

        ServerWorld world = server.getWorld(worldKey);
        if (world == null) {
            return null;
        }
        runOneTimeInitIfNeeded(world, ownerHint);
        ensureAccessForWorld(server, id, ownerHint);
        return world;
    }

    /**
     * Best-effort reload from DA disk snapshot when the dim was unregistered (idle unload).
     * Uses {@code DimensionManagerImpl.loadDimension} until a public load API exists.
     */
    private static boolean tryLoadPersistedDimension(net.darchitect.api.DimensionArchitect api, String id) {
        if (!(api instanceof net.darchitect.impl.DimensionManagerImpl manager)) {
            return false;
        }
        try {
            manager.loadDimension(id);
            return manager.hasDimension(id);
        } catch (DimensionAlreadyExistsException already) {
            return true;
        } catch (IllegalArgumentException | IllegalStateException missing) {
            PersonnalWorld.LOGGER.debug("No persisted DA snapshot for {}: {}", id, missing.toString());
            return false;
        } catch (RuntimeException e) {
            PersonnalWorld.LOGGER.warn("DA loadDimension failed for {}: {}", id, e.toString());
            return false;
        }
    }

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
					// Prefer reloading a persisted island over creating a blank VOID world.
					if (tryLoadPersistedDimension(api, id)) {
						return;
					}
					UUID ownerUuid = owner != null
							? owner.getUuid()
							: fr.galsaxx.invite.IslandIds.creatorUuidFromDimensionId(id).orElse(null);
                    var builder = api.builder(id)
                            .type(WorldType.VOID)
                            // shareInventory=true → isolatePlayerData(false) (DArchitect ≥ 0.0.58).
                            .isolatePlayerData(!PersonnalWorldConfig.get().shareInventory())
							.accessMode(AccessMode.MANAGED)
							.permissions(net.darchitect.access.RolePermissionMatrix.builder()
									.grant(net.darchitect.access.DimensionRole.GUEST, net.darchitect.access.DimensionPermission.JOIN)
									.build())
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
			applyDefaultDisplayName(record, ownerHint);
			AccessFileStore.get().saveMutation(record);
		} else {
			record = AccessFileStore.get().loadOrRecover(dimensionId, ownerUuid, nameHint);
			if (applyDefaultDisplayName(record, ownerHint)) {
				AccessFileStore.get().saveMutation(record);
			}
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
			if (record.ownerUuid() != null) {
				DArchitectAccess.migrateManaged(dimensionId, record.ownerUuid());
				state.ownerUuid = record.ownerUuid().toString();
			}
			DArchitectAccess.applyRecord(record);
			state.accessMigrated = true;
			state.markDirty();
		}
	}

	private static boolean applyDefaultDisplayName(AccessRecord record, ServerPlayerEntity ownerHint) {
		if (record == null || ownerHint == null || (record.displayName() != null && !record.displayName().isBlank())) {
			return false;
		}
		int slot = fr.galsaxx.invite.IslandIds.slotIndex(record.dimensionId());
		String language = ownerHint.getClientOptions().language();
		record.setDisplayName(fr.galsaxx.island.IslandDefaultName.of(language, slot < 0 ? 0 : slot));
		return true;
	}

	static PWWorldState getWorldState(ServerWorld world) {
        return PWWorldState.get(world);
    }

	public static void setIslandProfile(ServerWorld world, String profile) {
		PWWorldState state = PWWorldState.get(world);
		if (state == null) {
			return;
		}
		state.islandProfile = profile == null ? "" : profile;
		state.markDirty();
	}

	public static String getIslandProfile(ServerWorld world) {
		PWWorldState state = PWWorldState.get(world);
		return state == null ? "" : state.islandProfile;
	}

	public static java.util.Map<String, String> getGameruleOverlay(ServerWorld world) {
		PWWorldState state = PWWorldState.get(world);
		if (state == null) {
			return java.util.Map.of();
		}
		return java.util.Map.copyOf(state.gameruleOverlay);
	}

	public static void putGameruleOverlay(ServerWorld world, String rule, String value) {
		PWWorldState state = PWWorldState.get(world);
		if (state == null || rule == null) {
			return;
		}
		state.gameruleOverlay.put(rule, value);
		state.markDirty();
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
		/** Bloc recouvert par le cube, remis en place au prochain déplacement. */
		private String replacedBlockId = "";
		private String replacedBlockProps = "";
        /** Réservé : profil d’île pour futures dims (non utilisé en MVP). */
        private String islandProfile = "";
		private final java.util.Map<String, String> gameruleOverlay = new java.util.LinkedHashMap<>();
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

		String replacedBlockId() {
			return replacedBlockId == null ? "" : replacedBlockId;
		}

		String replacedBlockProps() {
			return replacedBlockProps == null ? "" : replacedBlockProps;
		}

		void setReplacedBlock(String blockId, String props) {
			replacedBlockId = blockId == null ? "" : blockId;
			replacedBlockProps = props == null ? "" : props;
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
			if (nbt.contains("replacedBlockId")) {
				s.replacedBlockId = nbt.getString("replacedBlockId");
				s.replacedBlockProps = nbt.contains("replacedBlockProps") ? nbt.getString("replacedBlockProps") : "";
			}
            if (nbt.contains("islandProfile")) {
                s.islandProfile = nbt.getString("islandProfile");
            }
			if (nbt.contains("gameruleOverlay")) {
				NbtCompound overlay = nbt.getCompound("gameruleOverlay");
				for (String key : overlay.getKeys()) {
					s.gameruleOverlay.put(key, overlay.getString(key));
				}
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
			if (replacedBlockId != null && !replacedBlockId.isEmpty()) {
				nbt.putString("replacedBlockId", replacedBlockId);
				nbt.putString("replacedBlockProps", replacedBlockProps == null ? "" : replacedBlockProps);
			}
            if (!islandProfile.isEmpty()) {
                nbt.putString("islandProfile", islandProfile);
            }
			if (!gameruleOverlay.isEmpty()) {
				NbtCompound overlay = new NbtCompound();
				gameruleOverlay.forEach(overlay::putString);
				nbt.put("gameruleOverlay", overlay);
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

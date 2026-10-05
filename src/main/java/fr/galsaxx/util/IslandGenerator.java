package fr.galsaxx.util;

import fr.galsaxx.island.IslandPresetRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class IslandGenerator {
	private static final String[] CLASSPATH_SUFFIXES = {
			"/data/personnalworld/structures/%s.nbt",
			"/data/personnalworld/structure/%s.nbt"
	};

	public static void generateIsland(ServerWorld world, ServerPlayerEntity player) {
		BlockPos origin = new BlockPos(
				fr.galsaxx.PersonnalWorld.ISLAND_NBT_ORIGIN_X,
				fr.galsaxx.PersonnalWorld.ISLAND_NBT_ORIGIN_Y,
				fr.galsaxx.PersonnalWorld.ISLAND_NBT_ORIGIN_Z);
		if (!world.getBlockState(origin).isAir()) {
			return;
		}

		String profile = PersonnalWorldUtil.getIslandProfile(world);
		String preferred = IslandPresetRegistry.preferredStructure(profile);
		Optional<StructureTemplate> optionalTemplate = loadStructure(world, preferred);
		if (optionalTemplate.isEmpty() && !IslandPresetRegistry.DEFAULT_STRUCTURE.equals(preferred)) {
			optionalTemplate = loadStructure(world, IslandPresetRegistry.DEFAULT_STRUCTURE);
		}

		if (optionalTemplate.isPresent()) {
			StructureTemplate template = optionalTemplate.get();
			template.place(
					world,
					origin,
					origin,
					new StructurePlacementData(),
					world.getRandom(),
					2
			);
			return;
		}

		BlockPos fallback = new BlockPos(
				fr.galsaxx.PersonnalWorld.ISLAND_SPAWN_X,
				fr.galsaxx.PersonnalWorld.ISLAND_SPAWN_Y - 2,
				fr.galsaxx.PersonnalWorld.ISLAND_SPAWN_Z);
		if (player != null) {
			player.sendMessage(Text.translatable("message.personnalworld.island_load_error"), false);
		}
		BlockState block = Blocks.STONE.getDefaultState();
		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				world.setBlockState(fallback.add(dx, 0, dz), block, 3);
			}
		}
	}

	private static Optional<StructureTemplate> loadStructure(ServerWorld world, String structureId) {
		if (structureId == null || structureId.isBlank()) {
			return Optional.empty();
		}
		StructureTemplateManager mgr = world.getServer().getStructureTemplateManager();
		Optional<StructureTemplate> fromManager = mgr.getTemplate(Identifier.of("personnalworld", structureId));
		if (fromManager.isPresent()) {
			return fromManager;
		}
		return loadFromClasspath(world, structureId);
	}

	private static Optional<StructureTemplate> loadFromClasspath(ServerWorld world, String structureId) {
		Set<String> paths = new LinkedHashSet<>();
		for (String pattern : CLASSPATH_SUFFIXES) {
			paths.add(String.format(pattern, structureId));
		}
		for (String res : paths) {
			try (InputStream in = IslandGenerator.class.getResourceAsStream(res)) {
				if (in == null) {
					continue;
				}
				NbtCompound nbt = NbtIo.readCompressed(in, NbtSizeTracker.ofUnlimitedBytes());
				StructureTemplate template = new StructureTemplate();
				template.readNbt(world.getRegistryManager().getWrapperOrThrow(RegistryKeys.BLOCK), nbt);
				return Optional.of(template);
			} catch (Exception ignored) {
			}
		}
		return Optional.empty();
	}
}

package fr.galsaxx.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class IslandGenerator {
    public static void generateIsland(ServerWorld world, ServerPlayerEntity player) {
        BlockPos center = new BlockPos(0, 50, 0); // Hauteur 150 = sûr dans le vide
        // Place l’île uniquement si ce n’est pas déjà fait
        if (world.getBlockState(center).isAir()) {
            StructureTemplateManager mgr = world.getServer().getStructureTemplateManager();
            Optional<StructureTemplate> optionalTemplate = mgr.getTemplate(Identifier.of("minecraft", "ile_1"));
            if (optionalTemplate.isPresent()) {
                StructureTemplate template = optionalTemplate.get();
                template.place(
                        world,
                        center,
                        center,
                        new StructurePlacementData(),
                        world.getRandom(),
                        2
                );
                System.out.println("[personnalworld] Île NBT générée avec succès !");
            } else {
                System.out.println("[personnalworld] Erreur : le fichier NBT n’a pas été trouvé !");
                // Exemple pour placer une plate-forme 3x3 de blocs sous les pieds du joueur
                BlockPos center2 = new BlockPos(24, 66, 17);; // position centrale où le joueur va spawn

                player.sendMessage(Text.literal("Attention, erreur dans le chargement de l'ile"), false);
                BlockState block = Blocks.STONE.getDefaultState(); // Ou un autre bloc de ton choix

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos pos = center2.add(dx, -1, dz); // Sous les pieds du joueur
                        world.setBlockState(pos, block, 3);
                    }
                }

            }
        } else {
            System.out.println("[personnalworld] L’île existe déjà à cette position.");
        }
    }
}

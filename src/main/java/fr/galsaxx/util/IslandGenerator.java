package fr.galsaxx.util;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class IslandGenerator {
    public static void generateIsland(ServerWorld world) {
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
            }
        } else {
            System.out.println("[personnalworld] L’île existe déjà à cette position.");
        }
    }
}

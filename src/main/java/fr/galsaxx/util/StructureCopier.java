package fr.galsaxx.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class StructureCopier {
    // Appel à chaque chargement du serveur (SERVER_STARTED)
    public static void ensureIslandsCopied(MinecraftServer server) {
        // Chemin du dossier où copier les NBT
        Path structuresFolder = server.getSavePath(WorldSavePath.ROOT)
                .resolve("generated")
                .resolve("minecraft")
                .resolve("structures");
        try {
            Files.createDirectories(structuresFolder);
        } catch (IOException e) {
            System.out.println("[personnalworld] Erreur création dossier structures : " + e.getMessage());
            return;
        }

        // Pattern pour trouver les ile_***.nbt dans le JAR
        Pattern pattern = Pattern.compile("data/personnalworld/structures/ile_.*\\.nbt");
        try {
            // Récupère le chemin du JAR courant
            URL url = StructureCopier.class.getProtectionDomain().getCodeSource().getLocation();
            if (url.getPath().endsWith(".jar")) {
                try (JarFile jar = new JarFile(url.toURI().getPath())) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (pattern.matcher(name).matches()) {
                            String fileName = name.substring(name.lastIndexOf('/') + 1);
                            Path target = structuresFolder.resolve(fileName);
                            if (!Files.exists(target)) {
                                try (InputStream in = jar.getInputStream(entry);
                                     OutputStream out = Files.newOutputStream(target)) {
                                    in.transferTo(out);
                                    System.out.println("[personnalworld] Structure copiée : " + fileName);
                                }
                            }
                        }
                    }
                }
            } else {
                // En dev, on peut lire depuis le filesystem
                Path devStructs = Path.of("src/main/resources/data/personnalworld/structures");
                if (Files.exists(devStructs)) {
                    Files.list(devStructs)
                            .filter(p -> p.getFileName().toString().matches("ile_.*\\.nbt"))
                            .forEach(p -> {
                                Path target = structuresFolder.resolve(p.getFileName().toString());
                                if (!Files.exists(target)) {
                                    try (InputStream in = Files.newInputStream(p);
                                         OutputStream out = Files.newOutputStream(target)) {
                                        in.transferTo(out);
                                        System.out.println("[personnalworld] (Dev) Structure copiée : " + p.getFileName());
                                    } catch (IOException e) {
                                        System.out.println("[personnalworld] Erreur copie (Dev) : " + e.getMessage());
                                    }
                                }
                            });
                }
            }
        } catch (IOException | URISyntaxException e) {
            System.out.println("[personnalworld] Erreur lors de la copie des îles : " + e.getMessage());
        }
    }
}

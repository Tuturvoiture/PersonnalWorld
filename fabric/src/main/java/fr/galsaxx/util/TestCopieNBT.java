package fr.galsaxx.util;

import net.minecraft.server.MinecraftServer;
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

public class TestCopieNBT {
    public static void run(MinecraftServer server) {
        System.out.println("[TEST_NBT] Début du script de test.");

        Path structuresFolder = server.getSavePath(WorldSavePath.ROOT)
                .resolve("generated")
                .resolve("minecraft")
                .resolve("structures");
        try {
            Files.createDirectories(structuresFolder);
            System.out.println("[TEST_NBT] Dossier de destination : " + structuresFolder);
        } catch (IOException e) {
            System.out.println("[TEST_NBT] Erreur création dossier structures : " + e.getMessage());
            return;
        }

        Pattern pattern = Pattern.compile("data/personnalworld/structures/.*\\.nbt");
        try {
            URL url = TestCopieNBT.class.getProtectionDomain().getCodeSource().getLocation();
            System.out.println("[TEST_NBT] URL du code source : " + url);
            if (url.getPath().endsWith(".jar")) {
                System.out.println("[TEST_NBT] Exécution dans un JAR !");
                try (JarFile jar = new JarFile(url.toURI().getPath())) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (pattern.matcher(name).matches()) {
                            System.out.println("[TEST_NBT] Fichier trouvé dans le JAR : " + name);
                            String fileName = name.substring(name.lastIndexOf('/') + 1);
                            Path target = structuresFolder.resolve(fileName);
                            if (!Files.exists(target)) {
                                try (InputStream in = jar.getInputStream(entry);
                                     OutputStream out = Files.newOutputStream(target)) {
                                    in.transferTo(out);
                                    System.out.println("[TEST_NBT] Structure copiée : " + fileName);
                                }
                            } else {
                                System.out.println("[TEST_NBT] Structure déjà présente : " + fileName);
                            }
                        }
                    }
                }
            } else {
                System.out.println("[TEST_NBT] Mode DEV détecté (pas dans un JAR) !");
                Path devStructs = Path.of("src/main/resources/data/personnalworld/structures");
                System.out.println("[TEST_NBT] Cherche dans : " + devStructs);
                System.out.println("[TEST_NBT] Liste fichiers dans src/main/resources/data/personnalworld/structures :");
                try {
                    Files.list(devStructs).forEach(p -> System.out.println("  " + p));
                } catch (Exception e) {
                    System.out.println("  (erreur de lecture : " + e.getMessage() + ")");
                }
                if (Files.exists(devStructs)) {
                    Files.list(devStructs)
                            .filter(p -> p.getFileName().toString().endsWith(".nbt"))
                            .forEach(p -> {
                                System.out.println("[TEST_NBT] Fichier trouvé (dev) : " + p.getFileName());
                                Path target = structuresFolder.resolve(p.getFileName().toString());
                                if (!Files.exists(target)) {
                                    try (InputStream in = Files.newInputStream(p);
                                         OutputStream out = Files.newOutputStream(target)) {
                                        in.transferTo(out);
                                        System.out.println("[TEST_NBT] (Dev) Structure copiée : " + p.getFileName());
                                    } catch (IOException e) {
                                        System.out.println("[TEST_NBT] Erreur copie (Dev) : " + e.getMessage());
                                    }
                                } else {
                                    System.out.println("[TEST_NBT] Déjà présent (dev) : " + p.getFileName());
                                }
                            });
                } else {
                    System.out.println("[TEST_NBT] Dossier de structures non trouvé (dev) : " + devStructs);
                }
            }
        } catch (IOException | URISyntaxException e) {
            System.out.println("[TEST_NBT] Erreur lors de la copie des NBT : " + e.getMessage());
        }
        System.out.println("[TEST_NBT] Fin du script de test.");
    }
}

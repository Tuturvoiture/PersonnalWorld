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

public class StructureCopier {
    public static void run(MinecraftServer server) {
        Path structuresFolder = server.getSavePath(WorldSavePath.ROOT)
                .resolve("generated")
                .resolve("minecraft")
                .resolve("structures");
        try {
            Files.createDirectories(structuresFolder);
        } catch (IOException e) {
            return;
        }

        Pattern pattern = Pattern.compile("data/personnalworld/structures/.*\\.nbt");
        try {
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
                                }
                            }
                        }
                    }
                }
            } else {
                Path devStructs = Path.of("src/main/resources/data/personnalworld/structures");
                try {
                    Files.list(devStructs).forEach(p -> System.out.println("  " + p));
                } catch (Exception e) {
                    System.out.println("  (error : " + e.getMessage() + ")");
                }
                if (Files.exists(devStructs)) {
                    Files.list(devStructs)
                            .filter(p -> p.getFileName().toString().endsWith(".nbt"))
                            .forEach(p -> {
                                Path target = structuresFolder.resolve(p.getFileName().toString());
                                if (!Files.exists(target)) {
                                    try (InputStream in = Files.newInputStream(p);
                                         OutputStream out = Files.newOutputStream(target)) {
                                        in.transferTo(out);
                                    } catch (IOException e) {
                                    }
                                } else {
                                }
                            });
                } else {
                }
            }
        } catch (IOException | URISyntaxException e) {
        }
    }
}

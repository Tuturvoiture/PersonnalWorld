package fr.galsaxx.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class StructureCopier {
	private static final String[] NBT_NAMES = {"ile_1.nbt", "test.nbt"};
	private static final String[] CLASSPATH_DIRS = {
			"data/personnalworld/structures/",
			"data/personnalworld/structure/"
	};

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

		ClassLoader cl = StructureCopier.class.getClassLoader();
		for (String name : NBT_NAMES) {
			Path target = structuresFolder.resolve(name);
			if (Files.exists(target)) {
				continue;
			}
			for (String dir : CLASSPATH_DIRS) {
				try (InputStream stream = cl.getResourceAsStream(dir + name)) {
					if (stream == null) {
						continue;
					}
					try (OutputStream out = Files.newOutputStream(target)) {
						stream.transferTo(out);
					}
					break;
				} catch (IOException ignored) {
				}
			}
		}
	}
}

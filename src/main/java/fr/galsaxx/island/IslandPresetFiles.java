package fr.galsaxx.island;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.galsaxx.PersonnalWorld;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class IslandPresetFiles {
	private IslandPresetFiles() {}

	public static List<IslandPresetRegistry.IslandPreset> load(Path directory) {
		List<IslandPresetRegistry.IslandPreset> extra = new ArrayList<>();
		if (directory == null || !Files.isDirectory(directory)) {
			return extra;
		}
		try (var stream = Files.list(directory)) {
			stream.filter(path -> path.getFileName().toString().endsWith(".json")).forEach(path -> {
				try {
					JsonObject root = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
					String id = root.has("id") ? root.get("id").getAsString() : "";
					String name = root.has("name") ? root.get("name").getAsString() : id;
					String icon = root.has("icon") ? root.get("icon").getAsString() : "";
					String structure = root.has("structure")
							? root.get("structure").getAsString()
							: IslandPresetRegistry.DEFAULT_STRUCTURE;
					boolean unlocked = !root.has("unlocked") || root.get("unlocked").getAsBoolean();
					if (!id.isBlank()) {
						extra.add(new IslandPresetRegistry.IslandPreset(id, name, icon, structure, unlocked));
					}
				} catch (Exception e) {
					PersonnalWorld.LOGGER.warn("Island preset ignored {}: {}", path.getFileName(), e.toString());
				}
			});
		} catch (IOException e) {
			PersonnalWorld.LOGGER.warn("Cannot list island presets in {}", directory, e);
		}
		return extra;
	}
}

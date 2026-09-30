package fr.galsaxx.island;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public final class IslandPresetRegistry {
	public static final String FALLBACK_ICON = "textures/gui/island_button.png";

	public record IslandPreset(String id, String name, String icon) {}

	private IslandPresetRegistry() {}

	public static List<IslandPreset> defaults() {
		return List.of(
				new IslandPreset("classic", "Classique", FALLBACK_ICON),
				new IslandPreset("forest", "Forêt", FALLBACK_ICON),
				new IslandPreset("rock", "Roche", FALLBACK_ICON));
	}

	public static String iconOrFallback(String icon) {
		if (icon == null || icon.isBlank()) {
			return FALLBACK_ICON;
		}
		return icon;
	}

	/** Les défauts restent. Un id déjà présent est remplacé (nom / icône). */
	public static List<IslandPreset> merge(List<IslandPreset> base, List<IslandPreset> extra) {
		LinkedHashMap<String, IslandPreset> map = new LinkedHashMap<>();
		if (base != null) {
			for (IslandPreset preset : base) {
				map.put(preset.id(), normalize(preset));
			}
		}
		if (extra != null) {
			for (IslandPreset preset : extra) {
				if (preset == null || preset.id() == null || preset.id().isBlank()) {
					continue;
				}
				map.put(preset.id(), normalize(preset));
			}
		}
		return List.copyOf(map.values());
	}

	public static Optional<IslandPreset> find(List<IslandPreset> presets, String id) {
		if (presets == null || id == null) {
			return Optional.empty();
		}
		for (IslandPreset preset : presets) {
			if (preset.id().equals(id)) {
				return Optional.of(preset);
			}
		}
		return Optional.empty();
	}

	public static List<IslandPreset> copy(List<IslandPreset> presets) {
		return new ArrayList<>(presets);
	}

	private static IslandPreset normalize(IslandPreset preset) {
		String name = preset.name() == null || preset.name().isBlank() ? preset.id() : preset.name();
		return new IslandPreset(preset.id(), name, iconOrFallback(preset.icon()));
	}
}

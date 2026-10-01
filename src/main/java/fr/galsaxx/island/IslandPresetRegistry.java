package fr.galsaxx.island;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public final class IslandPresetRegistry {
	public static final String FALLBACK_ICON = "textures/gui/island_button.png";
	/** Structure NBT par défaut si le fichier du type d’île est absent. */
	public static final String DEFAULT_STRUCTURE = "ile_1";

	/**
	 * @param structure id de structure Minecraft ({@code personnalworld:<structure>}), sans namespace
	 * @param unlocked  false = affiché mais non sélectionnable / non créable
	 */
	public record IslandPreset(String id, String name, String icon, String structure, boolean unlocked) {}

	private IslandPresetRegistry() {}

	public static List<IslandPreset> defaults() {
		return List.of(
				new IslandPreset("classic", "Classique", FALLBACK_ICON, DEFAULT_STRUCTURE, true),
				new IslandPreset("forest", "Forêt", FALLBACK_ICON, "ile_forest", false),
				new IslandPreset("rock", "Roche", FALLBACK_ICON, "ile_rock", false),
				new IslandPreset("desert", "Désert", FALLBACK_ICON, "ile_desert", false));
	}

	public static String iconOrFallback(String icon) {
		if (icon == null || icon.isBlank()) {
			return FALLBACK_ICON;
		}
		return icon;
	}

	public static String structureOrDefault(String structure) {
		if (structure == null || structure.isBlank()) {
			return DEFAULT_STRUCTURE;
		}
		return structure;
	}

	/** Les défauts restent. Un id déjà présent est remplacé (nom / icône / structure / unlocked). */
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

	public static Optional<IslandPreset> firstUnlocked(List<IslandPreset> presets) {
		if (presets == null) {
			return Optional.empty();
		}
		for (IslandPreset preset : presets) {
			if (preset.unlocked()) {
				return Optional.of(preset);
			}
		}
		return Optional.empty();
	}

	/**
	 * Structure à tenter pour un profil d’île stocké ; le chargeur retombe sur {@link #DEFAULT_STRUCTURE}
	 * si le fichier NBT manque.
	 */
	public static String preferredStructure(String presetId) {
		if (presetId == null || presetId.isBlank() || "classic".equals(presetId)) {
			return DEFAULT_STRUCTURE;
		}
		Optional<IslandPreset> known = find(defaults(), presetId);
		if (known.isPresent()) {
			return structureOrDefault(known.get().structure());
		}
		return "ile_" + presetId;
	}

	public static List<IslandPreset> copy(List<IslandPreset> presets) {
		return new ArrayList<>(presets);
	}

	private static IslandPreset normalize(IslandPreset preset) {
		String name = preset.name() == null || preset.name().isBlank() ? preset.id() : preset.name();
		return new IslandPreset(
				preset.id(),
				name,
				iconOrFallback(preset.icon()),
				structureOrDefault(preset.structure()),
				preset.unlocked());
	}
}

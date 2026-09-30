package fr.galsaxx.island;

import java.util.Locale;

/** Nom par défaut du monde créé sans saisie : « Monde 1 » ou « World 1 ». */
public final class IslandDefaultName {
	private IslandDefaultName() {}

	public static String of(String language, int slotIndex) {
		int number = Math.max(0, slotIndex) + 1;
		String lang = language == null ? "" : language.toLowerCase(Locale.ROOT);
		if (lang.startsWith("fr")) {
			return "Monde " + number;
		}
		return "World " + number;
	}
}

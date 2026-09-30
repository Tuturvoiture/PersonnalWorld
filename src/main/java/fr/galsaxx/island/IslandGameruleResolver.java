package fr.galsaxx.island;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * (1) Overworld, (2) dimension hors surcouche, (3) surcouche.
 * Si sync et (2) ≠ (1) sans clé dans (3) : copier (1). La surcouche gagne toujours.
 */
public final class IslandGameruleResolver {
	private IslandGameruleResolver() {}

	public static Map<String, String> resolve(
			boolean sync,
			Map<String, String> overworld,
			Map<String, String> dimension,
			Map<String, String> overlay) {
		Map<String, String> normal = overworld == null ? Map.of() : overworld;
		Map<String, String> dim = dimension == null ? Map.of() : dimension;
		Map<String, String> custom = overlay == null ? Map.of() : overlay;
		Map<String, String> result = new LinkedHashMap<>();
		if (sync) {
			for (Map.Entry<String, String> entry : normal.entrySet()) {
				String key = entry.getKey();
				if (custom.containsKey(key)) {
					result.put(key, custom.get(key));
					continue;
				}
				String fromDim = dim.get(key);
				String fromWorld = entry.getValue();
				if (fromDim == null || !fromDim.equals(fromWorld)) {
					result.put(key, fromWorld);
				} else {
					result.put(key, fromDim);
				}
			}
		} else {
			result.putAll(dim);
		}
		for (Map.Entry<String, String> entry : custom.entrySet()) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}

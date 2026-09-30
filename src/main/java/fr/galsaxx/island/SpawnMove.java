package fr.galsaxx.island;

/**
 * Décision pure pour déplacer le cube de spawn : un seul cube, l’ancien bloc revient.
 */
public final class SpawnMove {
	public enum FloorKind {
		AIR,
		FLUID,
		BARRIER,
		CONTAINER,
		NOT_FULL,
		MARKER,
		SOLID
	}

	public record Result(boolean ok, String messageKey, boolean restorePrevious, String restoreKey) {}

	private SpawnMove() {}

	public static Result plan(
			boolean sameDimension,
			boolean onGround,
			FloorKind floor,
			boolean samePosition,
			String savedBlockKey,
			String floorBlockKey,
			String markerBlockKey) {
		if (!sameDimension) {
			return reject("message.personnalworld.spawn.wrong_dim");
		}
		if (!onGround || floor == FloorKind.AIR || floor == FloorKind.FLUID) {
			return reject("message.personnalworld.spawn.in_air");
		}
		String floorKey = floorBlockKey == null ? "" : floorBlockKey;
		String markerKey = markerBlockKey == null ? "" : markerBlockKey;
		if (samePosition || floor == FloorKind.MARKER || markerKey.equals(floorKey)) {
			return reject("message.personnalworld.spawn.same");
		}
		if (floor != FloorKind.SOLID) {
			return reject("message.personnalworld.spawn.not_solid");
		}
		String saved = savedBlockKey == null ? "" : savedBlockKey;
		String restore = saved.isBlank() || saved.equals(markerKey) ? "minecraft:air" : saved;
		return new Result(true, "message.personnalworld.spawn.moved", true, restore);
	}

	private static Result reject(String key) {
		return new Result(false, key, false, "");
	}
}

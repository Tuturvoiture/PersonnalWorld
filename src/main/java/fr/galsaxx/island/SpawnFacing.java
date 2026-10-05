package fr.galsaxx.island;

/**
 * Facing snap to N/E/S/W (Minecraft yaw: 0=S, 90=W, 180=N, 270=E).
 */
public final class SpawnFacing {
	private SpawnFacing() {}

	/** Nearest 90° yaw in Minecraft degrees. */
	public static float snapYaw(float yaw) {
		float y = yaw % 360.0F;
		if (y < 0.0F) {
			y += 360.0F;
		}
		int sector = Math.round(y / 90.0F) % 4;
		if (sector < 0) {
			sector += 4;
		}
		return sector * 90.0F;
	}
}

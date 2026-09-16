package fr.galsaxx.client;

/**
 * Pose bras « lecture » côté client : active du clic jusqu'à fermeture du GUI,
 * sans exiger de maintenir le bouton (contrairement à {@code UseAction} seul).
 */
public final class AdventureBookClientPose {

	private static boolean localReading;

	private AdventureBookClientPose() {
	}

	public static void setLocalReading(boolean reading) {
		localReading = reading;
	}

	public static boolean isLocalReading() {
		return localReading;
	}
}

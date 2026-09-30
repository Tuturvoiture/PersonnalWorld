package fr.galsaxx.client;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Pose bras « lecture » côté client : active du clic jusqu'à fermeture du GUI,
 * sans exiger de maintenir le bouton (contrairement à {@code UseAction} seul).
 * L’animation 3D est par joueur ({@link #reading}), pas un état unique du carnet.
 */
public final class AdventureBookClientPose {

	private static boolean localReading;
	private static final Set<UUID> READING = ConcurrentHashMap.newKeySet();
	private static UUID renderHolder;

	private AdventureBookClientPose() {
	}

	public static void setLocalReading(boolean reading) {
		localReading = reading;
	}

	public static boolean isLocalReading() {
		return localReading;
	}

	public static void setReading(UUID playerId, boolean reading) {
		if (playerId == null) {
			return;
		}
		if (reading) {
			READING.add(playerId);
		} else {
			READING.remove(playerId);
		}
	}

	public static boolean isReading(UUID playerId) {
		return playerId != null && READING.contains(playerId);
	}

	public static void setRenderHolder(UUID playerId) {
		renderHolder = playerId;
	}

	public static UUID renderHolder() {
		return renderHolder;
	}
}

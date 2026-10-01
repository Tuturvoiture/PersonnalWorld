package fr.galsaxx.client;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Pose bras « lecture » côté client : active du clic jusqu'à la fin de l’anim close,
 * sans exiger de maintenir le bouton (contrairement à {@code UseAction} seul).
 * L’animation 3D est par joueur ({@link #reading} / {@link #closing}), pas un NBT de pile
 * (un NBT en fin de close ferait bobber l’item comme un changement de case).
 */
public final class AdventureBookClientPose {

	/** ~durée anim {@code close} (0.45 s ≈ 9 ticks). */
	public static final int CLOSE_TICKS = 9;

	private static boolean localReading;
	private static UUID localPlayerId;
	private static final Set<UUID> READING = ConcurrentHashMap.newKeySet();
	private static final Map<UUID, Integer> CLOSING = new ConcurrentHashMap<>();
	private static UUID renderHolder;

	private AdventureBookClientPose() {
	}

	public static void setLocalReading(boolean reading) {
		localReading = reading;
	}

	/** Bras en pose lecture tant que le livre est ouvert ou en train de se fermer. */
	public static boolean isLocalReading() {
		return localReading;
	}

	public static void setReading(UUID playerId, boolean reading) {
		if (playerId == null) {
			return;
		}
		if (reading) {
			CLOSING.remove(playerId);
			READING.add(playerId);
		} else {
			READING.remove(playerId);
		}
	}

	public static boolean isReading(UUID playerId) {
		return playerId != null && READING.contains(playerId);
	}

	/**
	 * Démarre la phase close (anim couverture + pose bras) sans toucher au NBT de la pile.
	 * Pour le joueur local, garde {@link #localReading} jusqu’à la fin du compte à rebours.
	 */
	public static void beginClosing(UUID playerId, boolean local) {
		if (playerId == null) {
			return;
		}
		READING.remove(playerId);
		CLOSING.put(playerId, CLOSE_TICKS);
		if (local) {
			localPlayerId = playerId;
			localReading = true;
		}
	}

	public static boolean isClosing(UUID playerId) {
		return playerId != null && CLOSING.containsKey(playerId);
	}

	/** true si le rendu doit laisser jouer open/close (pas forcer la pose fermée). */
	public static boolean allowCoverAnim(UUID playerId) {
		return isReading(playerId) || isClosing(playerId);
	}

	public static void tickClient() {
		if (CLOSING.isEmpty()) {
			return;
		}
		boolean localFinished = false;
		Iterator<Map.Entry<UUID, Integer>> it = CLOSING.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<UUID, Integer> e = it.next();
			int left = e.getValue() - 1;
			if (left < 0) {
				if (e.getKey().equals(localPlayerId)) {
					localFinished = true;
				}
				it.remove();
			} else {
				e.setValue(left);
			}
		}
		if (localFinished) {
			localReading = false;
			localPlayerId = null;
		}
	}

	public static void setRenderHolder(UUID playerId) {
		renderHolder = playerId;
	}

	public static UUID renderHolder() {
		return renderHolder;
	}
}

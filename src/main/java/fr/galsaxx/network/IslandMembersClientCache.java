package fr.galsaxx.network;

import fr.galsaxx.invite.IslandMemberEntry;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Cache client du dernier {@link SyncIslandMembersPayload}.
 * L’UI livre lira {@link #last()} / {@link #forDimension(String)} ; pas de logique métier ici.
 */
public final class IslandMembersClientCache {
	private static volatile SyncIslandMembersPayload last;

	private IslandMembersClientCache() {}

	public static void accept(SyncIslandMembersPayload payload) {
		last = payload;
	}

	public static Optional<SyncIslandMembersPayload> last() {
		return Optional.ofNullable(last);
	}

	public static Optional<SyncIslandMembersPayload> forDimension(String dimensionId) {
		SyncIslandMembersPayload p = last;
		if (p == null || dimensionId == null) {
			return Optional.empty();
		}
		return dimensionId.equals(p.dimensionId()) ? Optional.of(p) : Optional.empty();
	}

	public static List<IslandMemberEntry> membersOrEmpty() {
		SyncIslandMembersPayload p = last;
		return p == null ? List.of() : p.members();
	}

	public static void clear() {
		last = null;
	}
}

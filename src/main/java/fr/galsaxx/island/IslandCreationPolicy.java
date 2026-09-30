package fr.galsaxx.island;

public final class IslandCreationPolicy {
	public enum Reason {
		NONE,
		MAX
	}

	public record Decision(boolean allowed, Reason reason) {}

	private IslandCreationPolicy() {}

	/** {@code max <= 0} = illimité. */
	public static Decision evaluate(int created, int max) {
		if (max > 0 && created >= max) {
			return new Decision(false, Reason.MAX);
		}
		return new Decision(true, Reason.NONE);
	}
}

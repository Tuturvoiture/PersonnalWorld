package fr.galsaxx;

/**
 * Shared bootstrap for all loaders (Architectury common).
 * Loader-specific entrypoints call {@link #init()}.
 */
public final class PersonnalWorld {
	public static final String MOD_ID = "personnalworld";

	private PersonnalWorld() {}

	public static void init() {
		// Shared registration is migrated here in the following step.
	}
}

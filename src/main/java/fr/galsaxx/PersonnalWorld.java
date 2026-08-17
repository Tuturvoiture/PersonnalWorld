package fr.galsaxx;

import net.darchitect.api.ext.DArchitectServices;

/**
 * Shared bootstrap for all loaders (Architectury common).
 * Loader-specific entrypoints call {@link #init()}.
 */
public final class PersonnalWorld {
	public static final String MOD_ID = "personnalworld";
	public static final int DARCHITECT_QUOTA = 64;

	private PersonnalWorld() {}

	public static void init() {
		DArchitectServices.registerMod(MOD_ID, DARCHITECT_QUOTA);
	}
}

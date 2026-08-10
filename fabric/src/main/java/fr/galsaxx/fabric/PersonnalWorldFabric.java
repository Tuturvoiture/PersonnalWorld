package fr.galsaxx.fabric;

import fr.galsaxx.PersonnalWorld;
import fr.galsaxx.PersonnalWorldMod;
import net.fabricmc.api.ModInitializer;

/**
 * Fabric entrypoint. Game logic still lives in {@link PersonnalWorldMod} until the common migration.
 */
public class PersonnalWorldFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		PersonnalWorld.init();
		new PersonnalWorldMod().onInitialize();
	}
}

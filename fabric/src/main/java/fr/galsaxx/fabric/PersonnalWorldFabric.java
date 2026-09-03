package fr.galsaxx.fabric;

import fr.galsaxx.PersonnalWorld;
import net.fabricmc.api.ModInitializer;

/** Fabric entrypoint : bootstrap commun (item, commande, île). */
public class PersonnalWorldFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		PersonnalWorld.init();
	}
}

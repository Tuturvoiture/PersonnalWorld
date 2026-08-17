package fr.galsaxx.neoforge;

import fr.galsaxx.PersonnalWorld;
import net.neoforged.fml.common.Mod;

/**
 * NeoForge entrypoint. Gameplay item/command remain Fabric-only until the common migration.
 */
@Mod(PersonnalWorld.MOD_ID)
public class PersonnalWorldNeoForge {
	public PersonnalWorldNeoForge() {
		PersonnalWorld.init();
	}
}

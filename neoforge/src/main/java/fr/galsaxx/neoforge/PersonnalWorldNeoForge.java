package fr.galsaxx.neoforge;

import fr.galsaxx.PersonnalWorld;
import net.neoforged.fml.common.Mod;

/**
 * NeoForge entrypoint. Dimension features remain stubbed until the dedicated API is wired.
 */
@Mod(PersonnalWorld.MOD_ID)
public class PersonnalWorldNeoForge {
	public PersonnalWorldNeoForge() {
		PersonnalWorld.init();
	}
}

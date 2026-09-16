package fr.galsaxx.neoforge;

import fr.galsaxx.PersonnalWorld;
import net.neoforged.fml.common.Mod;

/** NeoForge entrypoint : même bootstrap commun que Fabric. */
@Mod(PersonnalWorld.MOD_ID)
public class PersonnalWorldNeoForge {
	public PersonnalWorldNeoForge() {
		PersonnalWorld.init();
	}
}

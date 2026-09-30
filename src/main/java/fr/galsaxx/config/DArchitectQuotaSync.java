package fr.galsaxx.config;

import fr.galsaxx.PersonnalWorld;
import net.darchitect.api.DimensionArchitectRuntime;
import net.darchitect.config.DArchitectConfig;
import net.darchitect.config.DimensionLimitsConfig;
import net.darchitect.impl.ConfigLoaderImpl;

/**
 * Aligne {@code [dimensions].max_simultaneous} de DimensionArchitect sur
 * {@link PersonnalWorld#DARCHITECT_QUOTA}, sauf si la conf PersonnalWorld le désactive.
 */
public final class DArchitectQuotaSync {
	private DArchitectQuotaSync() {}

	public static void applyIfEnabled() {
		if (!PersonnalWorldConfig.get().syncDarchitectMaxSimultaneous()) {
			PersonnalWorld.LOGGER.info(
					"syncDarchitectMaxSimultaneous=false — [dimensions].max_simultaneous de DimensionArchitect inchangé");
			return;
		}
		int wanted = PersonnalWorld.DARCHITECT_QUOTA;
		try {
			ConfigLoaderImpl loader = new ConfigLoaderImpl();
			DArchitectConfig current = loader.load();
			DimensionLimitsConfig dims = current.getDimensions();
			if (dims.getMaxSimultaneous() == wanted) {
				PersonnalWorld.LOGGER.info(
						"DimensionArchitect max_simultaneous déjà à {} (quota PersonnalWorld)", wanted);
				return;
			}
			DimensionLimitsConfig updated = new DimensionLimitsConfig(
					wanted,
					dims.isAllowModQuotaOverride(),
					dims.isRequireRegisterModForApi(),
					dims.getDefaultModQuota());
			loader.save(current.withDimensions(updated));
			DimensionArchitectRuntime.reloadConfig();
			PersonnalWorld.LOGGER.info(
					"DimensionArchitect max_simultaneous {} → {} (quota PersonnalWorld)",
					dims.getMaxSimultaneous(),
					wanted);
		} catch (Throwable t) {
			PersonnalWorld.LOGGER.warn(
					"Impossible d’aligner max_simultaneous DimensionArchitect sur {} : {}",
					wanted,
					t.toString());
		}
	}
}

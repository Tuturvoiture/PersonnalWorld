package fr.galsaxx.compat.geckolib;

import dev.architectury.platform.Platform;
import fr.galsaxx.PersonnalWorld;
import fr.galsaxx.PersonnalWorldItem;
import net.minecraft.item.Item;

/**
 * Pont sans import GeckoLib : si le mod n’est pas là, on n’instancie jamais
 * {@link PersonnalWorldGeoItem} (sinon {@code NoClassDefFoundError}).
 */
public final class GeckoLibHooks {
	public static final String GECKOLIB_MOD_ID = "geckolib";
	private static final String GEO_ITEM_CLASS = "fr.galsaxx.compat.geckolib.PersonnalWorldGeoItem";

	private static boolean animationsActive;

	private GeckoLibHooks() {}

	public static boolean isModLoaded() {
		return Platform.isModLoaded(GECKOLIB_MOD_ID);
	}

	/** {@code true} seulement si l’item Geo a bien été créé (GeckoLib + classe OK). */
	public static boolean animationsActive() {
		return animationsActive;
	}

	public static Item createStaffItem(Item.Settings settings) {
		if (!isModLoaded()) {
			PersonnalWorld.LOGGER.info("GeckoLib absent : bâton en modèle 3D statique.");
			return new PersonnalWorldItem(settings);
		}
		try {
			Class<?> clazz = Class.forName(GEO_ITEM_CLASS);
			Item item = (Item) clazz.getConstructor(Item.Settings.class).newInstance(settings);
			animationsActive = true;
			PersonnalWorld.LOGGER.info("GeckoLib détecté : animations du bâton activées.");
			return item;
		} catch (Throwable t) {
			PersonnalWorld.LOGGER.warn(
					"GeckoLib est présent mais l’item animé n’a pas pu être chargé ; modèle 3D statique.",
					t
			);
			return new PersonnalWorldItem(settings);
		}
	}
}

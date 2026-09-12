package fr.galsaxx;

import dev.architectury.networking.NetworkManager;
import net.darchitect.api.ext.DArchitectServices;
import net.darchitect.api.ext.SpawnPoint;
import net.darchitect.api.ext.SpawnRequest;
import net.darchitect.api.ext.SpawnResolver;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shared bootstrap for all loaders (Architectury common).
 * Loader-specific entrypoints call {@link #init()}.
 */
public final class PersonnalWorld {
	public static final String MOD_ID = "personnalworld";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/** Paquet S→C : ouvrir l'interface du carnet d'aventurier. */
	public static final Identifier OPEN_BOOK_PACKET  = Identifier.of(MOD_ID, "open_adventure_book");
	/** Paquet C→S : le joueur ferme l'interface du carnet. */
	public static final Identifier CLOSE_BOOK_PACKET = Identifier.of(MOD_ID, "close_adventure_book");
	public static final int DARCHITECT_QUOTA = 64;
	/** Origine du NBT : l’ancien spawn (24,68,17) relatif à (0,50,0) arrive en (0,90,0). */
	public static final int ISLAND_NBT_ORIGIN_X = -24;
	public static final int ISLAND_NBT_ORIGIN_Y = 72;
	public static final int ISLAND_NBT_ORIGIN_Z = -17;
	public static final int ISLAND_SPAWN_X = 0;
	public static final int ISLAND_SPAWN_Y = 90;
	public static final int ISLAND_SPAWN_Z = 0;
	/** Bloc de référence spawn_marker (premier bloc visible île actuelle). */
	public static final int SPAWN_MARKER_Y = 88;
	/** Colonne de recherche sous le spawn (Y inclusif vers le bas). */
	public static final int SPAWN_FALLBACK_SCAN_DEPTH = 50;
	/** Bedrock de secours à spawnY − cette valeur si aucun sol trouvé. */
	public static final int SPAWN_BEDROCK_OFFSET_Y = 3;

	private PersonnalWorld() {}

	public static void init() {
		DArchitectServices.registerMod(MOD_ID, DARCHITECT_QUOTA);
		DArchitectServices.registerSpawnResolver(new PersonnalWorldSpawnResolver());
		PersonnalWorldContent.register();
		registerNetwork();
	}

	private static void registerNetwork() {
		// Paquet C→S : fermeture de l'interface carnet → trigger anim "close" (via Class.forName)
		NetworkManager.registerReceiver(
				NetworkManager.Side.C2S,
				CLOSE_BOOK_PACKET,
				(buf, ctx) -> ctx.queue(() -> {
					if (ctx.getPlayer() instanceof ServerPlayerEntity sp) {
						try {
							Class<?> clazz = Class.forName("fr.galsaxx.compat.geckolib.AdventureBookGeoItem");
							clazz.getMethod("handleCloseFromClient", ServerPlayerEntity.class).invoke(null, sp);
						} catch (Throwable ignored) {}
					}
				})
		);
	}

	/**
	 * Dims {@code personnalworld:*} : spawn sur l’île NBT, sans plateforme SKYBLOCK 3×3.
	 * Les autres dims gardent le comportement intégré ({@code resolve} = null).
	 */
	private static final class PersonnalWorldSpawnResolver implements SpawnResolver {
		@Override
		public SpawnPoint resolve(SpawnRequest request) {
			String id = request.dimensionId();
			if (id == null || !id.startsWith(MOD_ID + ":")) {
				return null;
			}
			return new SpawnPoint(ISLAND_SPAWN_X, ISLAND_SPAWN_Y, ISLAND_SPAWN_Z);
		}

		@Override
		public void buildStarterPlatform(SpawnRequest request) {
			// no-op : Extensions.buildStarterPlatform retourne true → skip SkyblockPlatformBuilder
		}
	}
}

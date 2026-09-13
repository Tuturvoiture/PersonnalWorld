package fr.galsaxx;

import dev.architectury.networking.NetworkManager;
import fr.galsaxx.client.AdventureBookScreen;
import fr.galsaxx.compat.geckolib.GeckoLibHooks;
import fr.galsaxx.compat.geckolib.client.BuiltinStaffModelWrapper;
import fr.galsaxx.network.OpenAdventureBookPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

/**
 * Avec GeckoLib : remplace le bake bâton/carnet par les JSON {@code *_geckolib}
 * ({@code builtin/entity} + displays calibrés), puis wrap {@code isBuiltin()}.
 * Sans GeckoLib : JSON classique / generated inchangé.
 */
public class PersonnalWolrdClient implements ClientModInitializer {
	private static final Identifier STAFF_GECKO_MODEL = Identifier.of(PersonnalWorld.MOD_ID, "item/personnal_world_item_geckolib");
	private static final Identifier BOOK_GECKO_MODEL = Identifier.of(PersonnalWorld.MOD_ID, "item/adventure_book_geckolib");

	@Override
	public void onInitializeClient() {
		NetworkManager.registerReceiver(
				NetworkManager.Side.S2C,
				OpenAdventureBookPayload.ID,
				OpenAdventureBookPayload.CODEC,
				(payload, ctx) -> ctx.queue(() -> MinecraftClient.getInstance().setScreen(new AdventureBookScreen()))
		);

		if (!GeckoLibHooks.animationsActive()) {
			return;
		}
		ModelLoadingPlugin.register(pluginContext -> {
			pluginContext.addModels(STAFF_GECKO_MODEL, BOOK_GECKO_MODEL);
			pluginContext.modifyModelOnLoad().register((model, ctx) -> {
				if (isStaffUnbaked(ctx.resourceId(), ctx.topLevelId())) {
					return ctx.getOrLoadModel(STAFF_GECKO_MODEL);
				}
				if (isBookUnbaked(ctx.resourceId(), ctx.topLevelId())) {
					return ctx.getOrLoadModel(BOOK_GECKO_MODEL);
				}
				return model;
			});
			pluginContext.modifyModelAfterBake().register((baked, ctx) -> {
				if (baked == null || baked instanceof BuiltinStaffModelWrapper) {
					return baked;
				}
				if (isStaffItemModel(ctx.topLevelId(), ctx.resourceId())
						|| isBookItemModel(ctx.topLevelId(), ctx.resourceId())) {
					return new BuiltinStaffModelWrapper(baked);
				}
				return baked;
			});
		});
	}

	private static boolean isStaffUnbaked(Identifier resourceId, ModelIdentifier topLevel) {
		if (resourceId != null
				&& PersonnalWorld.MOD_ID.equals(resourceId.getNamespace())
				&& "item/personnal_world_item".equals(resourceId.getPath())) {
			return true;
		}
		return topLevel != null
				&& PersonnalWorld.MOD_ID.equals(topLevel.id().getNamespace())
				&& "personnal_world_item".equals(topLevel.id().getPath());
	}

	private static boolean isBookUnbaked(Identifier resourceId, ModelIdentifier topLevel) {
		if (resourceId != null
				&& PersonnalWorld.MOD_ID.equals(resourceId.getNamespace())
				&& "item/adventure_book".equals(resourceId.getPath())) {
			return true;
		}
		return topLevel != null
				&& PersonnalWorld.MOD_ID.equals(topLevel.id().getNamespace())
				&& "adventure_book".equals(topLevel.id().getPath());
	}

	private static boolean isStaffItemModel(ModelIdentifier topLevel, Identifier resourceId) {
		if (topLevel != null && PersonnalWorld.MOD_ID.equals(topLevel.id().getNamespace())
				&& "personnal_world_item".equals(topLevel.id().getPath())) {
			return true;
		}
		return resourceId != null
				&& PersonnalWorld.MOD_ID.equals(resourceId.getNamespace())
				&& ("item/personnal_world_item".equals(resourceId.getPath())
				|| "item/personnal_world_item_geckolib".equals(resourceId.getPath()));
	}

	private static boolean isBookItemModel(ModelIdentifier topLevel, Identifier resourceId) {
		if (topLevel != null && PersonnalWorld.MOD_ID.equals(topLevel.id().getNamespace())
				&& "adventure_book".equals(topLevel.id().getPath())) {
			return true;
		}
		return resourceId != null
				&& PersonnalWorld.MOD_ID.equals(resourceId.getNamespace())
				&& ("item/adventure_book".equals(resourceId.getPath())
				|| "item/adventure_book_geckolib".equals(resourceId.getPath()));
	}
}

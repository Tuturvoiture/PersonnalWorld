package fr.galsaxx;

import fr.galsaxx.compat.geckolib.GeckoLibHooks;
import fr.galsaxx.compat.geckolib.client.BuiltinStaffModelWrapper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

/**
 * Avec GeckoLib : remplace le bake par {@code personnal_world_item_geckolib}
 * (displays calibrés pour {@code builtin/entity} + geo), puis wrap {@code isBuiltin()}.
 * Sans GeckoLib : JSON 3D classique inchangé.
 */
public class PersonnalWolrdClient implements ClientModInitializer {
	private static final Identifier STAFF_GECKO_MODEL = Identifier.of(PersonnalWorld.MOD_ID, "item/personnal_world_item_geckolib");

	@Override
	public void onInitializeClient() {
		if (!GeckoLibHooks.animationsActive()) {
			return;
		}
		ModelLoadingPlugin.register(pluginContext -> {
			pluginContext.addModels(STAFF_GECKO_MODEL);
			pluginContext.modifyModelOnLoad().register((model, ctx) -> {
				if (!isStaffUnbaked(ctx.resourceId(), ctx.topLevelId())) {
					return model;
				}
				return ctx.getOrLoadModel(STAFF_GECKO_MODEL);
			});
			pluginContext.modifyModelAfterBake().register((baked, ctx) -> {
				if (baked == null || baked instanceof BuiltinStaffModelWrapper) {
					return baked;
				}
				if (!isStaffItemModel(ctx.topLevelId(), ctx.resourceId())) {
					return baked;
				}
				return new BuiltinStaffModelWrapper(baked);
			});
		});
	}

	/** Cible le JSON classique uniquement (pas le modèle gecko lui-même). */
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
}

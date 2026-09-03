package fr.galsaxx;

import fr.galsaxx.compat.geckolib.GeckoLibHooks;
import fr.galsaxx.compat.geckolib.client.BuiltinStaffModelWrapper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public class PersonnalWolrdClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		if (!GeckoLibHooks.animationsActive()) {
			return;
		}
		ModelLoadingPlugin.register(pluginContext ->
				pluginContext.modifyModelAfterBake().register((baked, ctx) -> {
					if (baked == null || baked instanceof BuiltinStaffModelWrapper) {
						return baked;
					}
					if (!isStaffItemModel(ctx.topLevelId(), ctx.resourceId())) {
						return baked;
					}
					return new BuiltinStaffModelWrapper(baked);
				})
		);
	}

	private static boolean isStaffItemModel(ModelIdentifier topLevel, Identifier resourceId) {
		if (topLevel != null && PersonnalWorld.MOD_ID.equals(topLevel.id().getNamespace())
				&& "personnal_world_item".equals(topLevel.id().getPath())) {
			return true;
		}
		return resourceId != null
				&& PersonnalWorld.MOD_ID.equals(resourceId.getNamespace())
				&& "item/personnal_world_item".equals(resourceId.getPath());
	}
}

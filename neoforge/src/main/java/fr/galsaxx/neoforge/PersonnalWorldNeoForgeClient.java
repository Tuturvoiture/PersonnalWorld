package fr.galsaxx.neoforge;

import fr.galsaxx.PersonnalWorld;
import fr.galsaxx.compat.geckolib.GeckoLibHooks;
import fr.galsaxx.compat.geckolib.client.BuiltinStaffModelWrapper;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.ArrayList;

@EventBusSubscriber(modid = PersonnalWorld.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class PersonnalWorldNeoForgeClient {
	private PersonnalWorldNeoForgeClient() {}

	@SubscribeEvent
	public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
		if (!GeckoLibHooks.animationsActive()) {
			return;
		}
		var models = event.getModels();
		for (var key : new ArrayList<>(models.keySet())) {
			if (!isStaffItemModel(key)) {
				continue;
			}
			var baked = models.get(key);
			if (baked != null && !(baked instanceof BuiltinStaffModelWrapper)) {
				models.put(key, new BuiltinStaffModelWrapper(baked));
			}
		}
	}

	private static boolean isStaffItemModel(ModelIdentifier id) {
		Identifier name = id.id();
		return PersonnalWorld.MOD_ID.equals(name.getNamespace()) && "personnal_world_item".equals(name.getPath());
	}
}


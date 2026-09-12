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

/**
 * Avec GeckoLib : bake {@code personnal_world_item_geckolib} (displays geo)
 * et remplace les clés du bâton. Sans GeckoLib : rien.
 */
@EventBusSubscriber(modid = PersonnalWorld.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class PersonnalWorldNeoForgeClient {
	private static final Identifier STAFF_GECKO = Identifier.of(PersonnalWorld.MOD_ID, "item/personnal_world_item_geckolib");

	private PersonnalWorldNeoForgeClient() {}

	@SubscribeEvent
	public static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
		if (!GeckoLibHooks.animationsActive()) {
			return;
		}
		event.register(STAFF_GECKO);
	}

	@SubscribeEvent
	public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
		if (!GeckoLibHooks.animationsActive()) {
			return;
		}
		var models = event.getModels();
		var gecko = models.get(ModelIdentifier.ofInventoryVariant(STAFF_GECKO));
		if (gecko == null) {
			for (var entry : models.entrySet()) {
				if (STAFF_GECKO.equals(entry.getKey().id())
						|| "item/personnal_world_item_geckolib".equals(entry.getKey().id().getPath())) {
					gecko = entry.getValue();
					break;
				}
			}
		}
		if (gecko == null) {
			PersonnalWorld.LOGGER.warn("Modèle GeckoLib du bâton introuvable au bake ; displays GeckoLib non appliqués.");
			return;
		}
		var geckoWrapped = new BuiltinStaffModelWrapper(gecko);
		for (var key : new ArrayList<>(models.keySet())) {
			if (!isStaffItemModel(key)) {
				continue;
			}
			models.put(key, geckoWrapped);
		}
	}

	private static boolean isStaffItemModel(ModelIdentifier id) {
		Identifier name = id.id();
		return PersonnalWorld.MOD_ID.equals(name.getNamespace())
				&& ("personnal_world_item".equals(name.getPath())
				|| "item/personnal_world_item".equals(name.getPath())
				|| "personnal_world_item_geckolib".equals(name.getPath())
				|| "item/personnal_world_item_geckolib".equals(name.getPath()));
	}
}

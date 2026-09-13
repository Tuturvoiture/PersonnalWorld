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
 * Avec GeckoLib : bake {@code *_geckolib} (displays geo) et remplace les clés bâton/carnet.
 * Sans GeckoLib : rien.
 */
@EventBusSubscriber(modid = PersonnalWorld.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class PersonnalWorldNeoForgeClient {
	private static final Identifier STAFF_GECKO = Identifier.of(PersonnalWorld.MOD_ID, "item/personnal_world_item_geckolib");
	private static final Identifier BOOK_GECKO = Identifier.of(PersonnalWorld.MOD_ID, "item/adventure_book_geckolib");

	private PersonnalWorldNeoForgeClient() {}

	@SubscribeEvent
	public static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
		if (!GeckoLibHooks.animationsActive()) {
			return;
		}
		event.register(STAFF_GECKO);
		event.register(BOOK_GECKO);
	}

	@SubscribeEvent
	public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
		if (!GeckoLibHooks.animationsActive()) {
			return;
		}
		var models = event.getModels();
		wrapGeoItem(models, STAFF_GECKO, "bâton", PersonnalWorldNeoForgeClient::isStaffItemModel);
		wrapGeoItem(models, BOOK_GECKO, "carnet", PersonnalWorldNeoForgeClient::isBookItemModel);
	}

	private static void wrapGeoItem(
			java.util.Map<ModelIdentifier, net.minecraft.client.render.model.BakedModel> models,
			Identifier geckoId,
			String label,
			java.util.function.Predicate<ModelIdentifier> matcher
	) {
		var gecko = models.get(ModelIdentifier.ofInventoryVariant(geckoId));
		if (gecko == null) {
			for (var entry : models.entrySet()) {
				if (geckoId.equals(entry.getKey().id())
						|| geckoId.getPath().equals(entry.getKey().id().getPath())) {
					gecko = entry.getValue();
					break;
				}
			}
		}
		if (gecko == null) {
			PersonnalWorld.LOGGER.warn("Modèle GeckoLib du {} introuvable au bake ; displays GeckoLib non appliqués.", label);
			return;
		}
		var geckoWrapped = new BuiltinStaffModelWrapper(gecko);
		for (var key : new ArrayList<>(models.keySet())) {
			if (matcher.test(key)) {
				models.put(key, geckoWrapped);
			}
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

	private static boolean isBookItemModel(ModelIdentifier id) {
		Identifier name = id.id();
		return PersonnalWorld.MOD_ID.equals(name.getNamespace())
				&& ("adventure_book".equals(name.getPath())
				|| "item/adventure_book".equals(name.getPath())
				|| "adventure_book_geckolib".equals(name.getPath())
				|| "item/adventure_book_geckolib".equals(name.getPath()));
	}
}

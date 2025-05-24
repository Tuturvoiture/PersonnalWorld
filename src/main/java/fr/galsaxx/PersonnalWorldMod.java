package fr.galsaxx;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;   // ← toujours le bon import

public class PersonnalWorldMod implements ModInitializer {
	public static final Item PERSONNAL_WORLD_ITEM = new PersonnalWorldItem(
			new Item.Settings()
					.maxCount(1)
					.rarity(Rarity.RARE)
	);

	@Override
	public void onInitialize() {
		Registry.register(
				Registries.ITEM,
				Identifier.of("personnalworld", "personnal_world_item"),   // ← changement ici
				PERSONNAL_WORLD_ITEM
		);
	}
}

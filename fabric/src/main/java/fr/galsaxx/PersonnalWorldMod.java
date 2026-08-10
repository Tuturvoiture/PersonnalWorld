package fr.galsaxx;


import fr.galsaxx.util.StructureCopier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Rarity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import fr.galsaxx.command.ReturnWorldCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;



public class PersonnalWorldMod implements ModInitializer {
	public static final Item PERSONNAL_WORLD_ITEM = new PersonnalWorldItem(
			new Item.Settings()
					.maxCount(1)
					.rarity(Rarity.RARE)
	);

	//? if >=1.21.1 {
	public static final String STONECUTTER_TARGET = "1.21.1";
	//?}

	@Override
	public void onInitialize() {
		Registry.register(
				Registries.ITEM,
				Identifier.of("personnalworld", "personnal_world_item"),
				PERSONNAL_WORLD_ITEM
		);
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ReturnWorldCommand.register(dispatcher);
		});
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			StructureCopier.run(server);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
			entries.add(PERSONNAL_WORLD_ITEM);
		});
	}


}

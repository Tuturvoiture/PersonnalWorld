package fr.galsaxx;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import fr.galsaxx.block.PersonalSpawnMarkerBlock;
import fr.galsaxx.command.ReturnWorldCommand;
import fr.galsaxx.compat.geckolib.GeckoLibHooks;
import fr.galsaxx.util.StructureCopier;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Rarity;

/**
 * Item, onglet créatif, commande et copie NBT — communs Fabric / NeoForge.
 */
public final class PersonnalWorldContent {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(PersonnalWorld.MOD_ID, RegistryKeys.BLOCK);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(PersonnalWorld.MOD_ID, RegistryKeys.ITEM);

	public static final RegistrySupplier<PersonalSpawnMarkerBlock> SPAWN_MARKER = BLOCKS.register(
			"spawn_marker",
			PersonalSpawnMarkerBlock::new
	);

	public static final RegistrySupplier<Item> PERSONNAL_WORLD_ITEM = ITEMS.register(
			"personnal_world_item",
			() -> GeckoLibHooks.createStaffItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE))
	);

	private PersonnalWorldContent() {}

	@SuppressWarnings("unchecked")
	public static void register() {
		BLOCKS.register();
		ITEMS.register();
		CreativeTabRegistry.append(ItemGroups.TOOLS, PERSONNAL_WORLD_ITEM);
		CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, environment) ->
				ReturnWorldCommand.register(dispatcher));
		LifecycleEvent.SERVER_STARTED.register(StructureCopier::run);
	}
}

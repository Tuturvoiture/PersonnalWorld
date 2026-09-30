package fr.galsaxx;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import fr.galsaxx.block.PersonalSpawnMarkerBlock;
import fr.galsaxx.command.PersonnalWorldCommand;
import fr.galsaxx.command.ReturnWorldCommand;
import fr.galsaxx.compat.geckolib.GeckoLibHooks;
import fr.galsaxx.config.PersonnalWorldConfig;
import fr.galsaxx.invite.PresenceAndRightsGuard;
import fr.galsaxx.network.SyncIslandMembersPayload;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Rarity;

/**
 * Item, onglet créatif, commande — communs Fabric / NeoForge.
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

	public static final RegistrySupplier<Item> ADVENTURE_BOOK = ITEMS.register(
			"adventure_book",
			() -> GeckoLibHooks.createBookItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON))
	);

	private PersonnalWorldContent() {}

	@SuppressWarnings("unchecked")
	public static void register() {
		BLOCKS.register();
		ITEMS.register();
		CreativeTabRegistry.append(ItemGroups.TOOLS, PERSONNAL_WORLD_ITEM);
		CreativeTabRegistry.append(ItemGroups.TOOLS, ADVENTURE_BOOK);
		CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, environment) -> {
			ReturnWorldCommand.register(dispatcher);
			PersonnalWorldCommand.register(dispatcher);
		});
		LifecycleEvent.SERVER_STARTING.register(server -> {
			PersonnalWorldConfig.load();
			fr.galsaxx.config.DArchitectQuotaSync.applyIfEnabled();
			PresenceAndRightsGuard.onServerStarting(server);
		});
		LifecycleEvent.SERVER_STARTED.register(server ->
				fr.galsaxx.island.IslandGameruleSync.applyLoadedPersonalWorlds(server));
		LifecycleEvent.SERVER_LEVEL_LOAD.register(world -> {
			String id = world.getRegistryKey().getValue().toString();
			if (fr.galsaxx.invite.IslandIds.isPersonalIsland(id)) {
				fr.galsaxx.island.IslandGameruleSync.apply(world);
			}
		});
		fr.galsaxx.island.IslandPvpGuard.register();
		fr.galsaxx.island.IslandRoleGuard.register();
		dev.architectury.event.events.common.PlayerEvent.PLAYER_JOIN.register(player ->
				fr.galsaxx.island.IslandNameRefresh.onJoin(player));
		PresenceAndRightsGuard.register();
		SyncIslandMembersPayload.register();
		fr.galsaxx.network.IslandBookNetworking.registerServer();
		dev.architectury.utils.EnvExecutor.runInEnv(
				dev.architectury.utils.Env.CLIENT,
				() -> SyncIslandMembersPayload::registerClientReceiver);
	}
}

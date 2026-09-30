package fr.galsaxx.island;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
import fr.galsaxx.invite.AccessFileStore;
import fr.galsaxx.invite.AccessRecord;
import fr.galsaxx.invite.IslandAccessService;
import fr.galsaxx.invite.IslandIds;
import fr.galsaxx.invite.IslandRole;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Sur une île perso, un visiteur ne casse pas, ne pose pas et n’ouvre pas les coffres.
 * Builder, co-créateur et owner gardent ces actions.
 */
public final class IslandRoleGuard {
	private IslandRoleGuard() {}

	public static void register() {
		BlockEvent.BREAK.register((world, pos, state, player, xp) ->
				visitor(player) ? EventResult.interruptFalse() : EventResult.pass());
		BlockEvent.PLACE.register((world, pos, state, placer) -> {
			if (placer instanceof ServerPlayerEntity player && visitor(player)) {
				return EventResult.interruptFalse();
			}
			return EventResult.pass();
		});
		InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, direction) -> {
			if (!(player instanceof ServerPlayerEntity serverPlayer) || !visitor(serverPlayer)) {
				return EventResult.pass();
			}
			World world = serverPlayer.getWorld();
			if (isContainer(world, pos, world.getBlockState(pos))) {
				return EventResult.interruptFalse();
			}
			return EventResult.pass();
		});
	}

	public static boolean visitor(PlayerEntity player) {
		if (!(player instanceof ServerPlayerEntity serverPlayer)) {
			return false;
		}
		String dimensionId = serverPlayer.getServerWorld().getRegistryKey().getValue().toString();
		if (!IslandIds.isPersonalIsland(dimensionId)) {
			return false;
		}
		AccessRecord record = AccessFileStore.get().getCached(dimensionId).orElse(null);
		if (record == null) {
			return false;
		}
		IslandRole role = IslandAccessService.get().effectiveRole(record, serverPlayer.getUuid());
		return role == IslandRole.VISITOR || role == IslandRole.TEMP_VISITOR;
	}

	private static boolean isContainer(World world, BlockPos pos, BlockState state) {
		if (state.getBlock() instanceof ChestBlock
				|| state.getBlock() instanceof BarrelBlock
				|| state.getBlock() instanceof ShulkerBoxBlock
				|| state.getBlock() instanceof EnderChestBlock) {
			return true;
		}
		return world.getBlockEntity(pos) instanceof Inventory;
	}
}

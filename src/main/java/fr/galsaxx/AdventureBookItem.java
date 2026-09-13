package fr.galsaxx;

import dev.architectury.networking.NetworkManager;
import fr.galsaxx.network.OpenAdventureBookPayload;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

/**
 * Carnet d'aventurier — item de base (chargé sans GeckoLib).
 * Pose bras en avant ({@link UseAction#BLOCK}) pendant l'utilisation / lecture.
 */
public class AdventureBookItem extends Item {

	public AdventureBookItem(Settings settings) {
		super(settings);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user) {
		return 72000;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		user.setCurrentHand(hand);
		if (!world.isClient() && user instanceof ServerPlayerEntity serverPlayer) {
			NetworkManager.sendToPlayer(serverPlayer, new OpenAdventureBookPayload());
		}
		return TypedActionResult.consume(stack);
	}
}

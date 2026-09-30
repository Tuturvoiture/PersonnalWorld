package fr.galsaxx;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import fr.galsaxx.client.AdventureBookClientPose;
import fr.galsaxx.network.OpenAdventureBookPayload;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

/**
 * Carnet d'aventurier — item de base (chargé sans GeckoLib).
 * Pose bras : {@link UseAction#BLOCK} pendant le hold + flag client jusqu'à fermeture GUI.
 */
public class AdventureBookItem extends Item {

	public AdventureBookItem(Settings settings) {
		super(settings);
	}

	public static boolean isVisuallyOpen(ItemStack stack) {
		if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof AdventureBookItem)) {
			return false;
		}
		NbtComponent data = stack.get(DataComponentTypes.CUSTOM_DATA);
		return data != null && data.copyNbt().getBoolean("pw_book_open");
	}

	public static void setVisuallyOpen(ItemStack stack, boolean open) {
		if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof AdventureBookItem)) {
			return;
		}
		NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
		if (open) {
			nbt.putBoolean("pw_book_open", true);
		} else {
			nbt.remove("pw_book_open");
		}
		stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
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
		EnvExecutor.runInEnv(Env.CLIENT, () -> () -> AdventureBookClientPose.setLocalReading(true));
		if (!world.isClient() && user instanceof ServerPlayerEntity serverPlayer) {
			NetworkManager.sendToPlayer(serverPlayer, new OpenAdventureBookPayload());
		}
		return TypedActionResult.consume(stack);
	}
}

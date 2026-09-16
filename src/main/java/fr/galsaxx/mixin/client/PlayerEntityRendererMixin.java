package fr.galsaxx.mixin.client;

import fr.galsaxx.AdventureBookItem;
import fr.galsaxx.client.AdventureBookClientPose;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Force {@link BipedEntityModel.ArmPose#BLOCK} tant que le carnet est en lecture,
 * même si le joueur a relâché le clic (fin de {@code isUsingItem}).
 */
@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

	@Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
	private static void personnalworld$adventureBookArmPose(
			AbstractClientPlayerEntity player,
			Hand hand,
			CallbackInfoReturnable<BipedEntityModel.ArmPose> cir
	) {
		if (!(player instanceof ClientPlayerEntity) || !AdventureBookClientPose.isLocalReading()) {
			return;
		}
		ItemStack stack = player.getStackInHand(hand);
		if (stack.getItem() instanceof AdventureBookItem) {
			cir.setReturnValue(BipedEntityModel.ArmPose.BLOCK);
		}
	}
}

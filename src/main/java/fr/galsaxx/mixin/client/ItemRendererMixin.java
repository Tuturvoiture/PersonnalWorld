package fr.galsaxx.mixin.client;

import fr.galsaxx.AdventureBookItem;
import fr.galsaxx.client.AdventureBookClientPose;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Le rendu du carnet sait quel joueur le tient, pour n’animer que le sien.
 */
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	@Inject(
			method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V",
			at = @At("HEAD")
	)
	private void personnalworld$bookHolder(
			LivingEntity entity,
			ItemStack stack,
			ModelTransformationMode renderMode,
			boolean leftHanded,
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumers,
			World world,
			int light,
			int overlay,
			int seed,
			CallbackInfo ci
	) {
		if (entity != null && stack.getItem() instanceof AdventureBookItem) {
			AdventureBookClientPose.setRenderHolder(entity.getUuid());
		}
	}

	@Inject(
			method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V",
			at = @At("RETURN")
	)
	private void personnalworld$bookHolderClear(
			LivingEntity entity,
			ItemStack stack,
			ModelTransformationMode renderMode,
			boolean leftHanded,
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumers,
			World world,
			int light,
			int overlay,
			int seed,
			CallbackInfo ci
	) {
		if (stack.getItem() instanceof AdventureBookItem) {
			AdventureBookClientPose.setRenderHolder(null);
		}
	}
}

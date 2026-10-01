package fr.galsaxx.compat.geckolib.client;

import fr.galsaxx.client.AdventureBookClientPose;
import fr.galsaxx.compat.geckolib.AdventureBookGeoItem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.UUID;

/**
 * Les anims JSON restent <strong>identiques</strong> à Blockbench (Y− = ouvrir, close = miroir).
 * Le display main (~−90° Y) inverse le sens perçu : on annule uniquement le Y de {@code cover_front}
 * au rendu (livre fermé inchangé, open/close restent des miroirs l’un de l’autre).
 */
public final class AdventureBookGeoRenderer extends GeoItemRenderer<AdventureBookGeoItem> {

	public AdventureBookGeoRenderer(GeoModel<AdventureBookGeoItem> model) {
		super(model);
	}

	@Override
	public long getInstanceId(AdventureBookGeoItem animatable) {
		java.util.UUID holder = AdventureBookClientPose.renderHolder();
		if (holder == null) {
			return software.bernie.geckolib.animatable.GeoItem.getId(this.getCurrentItemStack());
		}
		return holder.getMostSignificantBits() ^ holder.getLeastSignificantBits();
	}

	@Override
	public void renderRecursively(
			MatrixStack poseStack,
			AdventureBookGeoItem animatable,
			GeoBone bone,
			RenderLayer renderType,
			VertexConsumerProvider bufferSource,
			VertexConsumer buffer,
			boolean isReRender,
			float partialTick,
			int packedLight,
			int packedOverlay,
			int colour
	) {
		boolean cover = "cover_front".equals(bone.getName());
		boolean pages = "pages_front".equals(bone.getName());
		UUID holder = AdventureBookClientPose.renderHolder();
		// Pas de NBT pw_book_open : lecture / closing pilotent l’anim (évite hop + flash en fin de close).
		boolean animate = AdventureBookClientPose.allowCoverAnim(holder);
		if ((cover || pages) && !animate) {
			var rest = bone.getInitialSnapshot();
			bone.setRotX(rest.getRotX());
			bone.setRotY(rest.getRotY());
			bone.setRotZ(rest.getRotZ());
		}
		boolean flipCoverY = cover;
		if (flipCoverY) {
			bone.setRotY(-bone.getRotY());
		}
		super.renderRecursively(
				poseStack, animatable, bone, renderType, bufferSource, buffer,
				isReRender, partialTick, packedLight, packedOverlay, colour
		);
		if (flipCoverY) {
			bone.setRotY(-bone.getRotY());
		}
	}
}

package fr.galsaxx.compat.geckolib.client;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Force {@code isBuiltin()} pour que GeckoLib / le BEWLR prenne le relais du JSON statique.
 */
public final class BuiltinStaffModelWrapper implements BakedModel {
	private final BakedModel wrapped;

	public BuiltinStaffModelWrapper(BakedModel wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
		return this.wrapped.getQuads(state, face, random);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return this.wrapped.useAmbientOcclusion();
	}

	@Override
	public boolean hasDepth() {
		return this.wrapped.hasDepth();
	}

	@Override
	public boolean isSideLit() {
		return this.wrapped.isSideLit();
	}

	@Override
	public boolean isBuiltin() {
		return true;
	}

	@Override
	public Sprite getParticleSprite() {
		return this.wrapped.getParticleSprite();
	}

	@Override
	public ModelTransformation getTransformation() {
		return this.wrapped.getTransformation();
	}

	@Override
	public ModelOverrideList getOverrides() {
		return this.wrapped.getOverrides();
	}
}

package fr.galsaxx.compat.geckolib;

import fr.galsaxx.PersonnalWorld;
import fr.galsaxx.PersonnalWorldItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

/**
 * Variante GeckoLib du bâton. Ne jamais référencer cette classe depuis du code
 * chargé sans GeckoLib (utiliser {@link GeckoLibHooks#createStaffItem}).
 */
public final class PersonnalWorldGeoItem extends PersonnalWorldItem implements GeoItem {
	/** idle = gem spin + léger bob Y (boucle) ; use = impulsion clic-droit. */
	private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
	private static final RawAnimation USE = RawAnimation.begin().thenPlay("use");

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public PersonnalWorldGeoItem(Settings settings) {
		super(settings);
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}

	@Override
	public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
		consumer.accept(new GeoRenderProvider() {
			private GeoItemRenderer<PersonnalWorldGeoItem> renderer;

			@Override
			public GeoItemRenderer<?> getGeoItemRenderer() {
				if (this.renderer == null) {
					this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(
							Identifier.of(PersonnalWorld.MOD_ID, "personnal_world_item")
					));
				}
				return this.renderer;
			}
		});
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "staff", 5, state -> state.setAndContinue(IDLE))
				.triggerableAnim("use", USE));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		if (world instanceof ServerWorld serverWorld) {
			GeoItem.getOrAssignId(stack, serverWorld);
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		TypedActionResult<ItemStack> result = super.use(world, user, hand);
		if (world instanceof ServerWorld serverWorld && result.getResult().isAccepted()) {
			triggerAnim(user, GeoItem.getOrAssignId(user.getStackInHand(hand), serverWorld), "staff", "use");
		}
		return result;
	}
}

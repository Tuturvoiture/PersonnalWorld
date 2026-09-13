package fr.galsaxx.compat.geckolib;

import dev.architectury.networking.NetworkManager;
import fr.galsaxx.AdventureBookItem;
import fr.galsaxx.PersonnalWorld;
import fr.galsaxx.network.OpenAdventureBookPayload;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
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
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Variante GeckoLib du carnet d'aventurier.
 * Ne jamais référencer hors de {@code compat/geckolib/} (chargé via Class.forName).
 * <p>
 * Flux : idle_closed → (use) open → idle_open → (close GUI) close → idle_closed.
 * Bras levés via {@link fr.galsaxx.AdventureBookItem#getUseAction} pendant l'usage.
 */
public final class AdventureBookGeoItem extends AdventureBookItem implements GeoItem {

	private static final RawAnimation IDLE_CLOSED = RawAnimation.begin().thenLoop("idle_closed");
	private static final RawAnimation OPEN = RawAnimation.begin().thenPlay("open").thenLoop("idle_open");
	private static final RawAnimation CLOSE = RawAnimation.begin().thenPlay("close").thenLoop("idle_closed");

	/** ~durée anim {@code open} (0.35 s ≈ 7 ticks). */
	private static final int OPEN_GUI_DELAY_TICKS = 7;

	private static final Map<UUID, Integer> PENDING_GUI_OPEN = new ConcurrentHashMap<>();

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public AdventureBookGeoItem(Settings settings) {
		super(settings);
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}

	@Override
	public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
		consumer.accept(new GeoRenderProvider() {
			private GeoItemRenderer<AdventureBookGeoItem> renderer;

			@Override
			public GeoItemRenderer<?> getGeoItemRenderer() {
				if (this.renderer == null) {
					this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(
							Identifier.of(PersonnalWorld.MOD_ID, "adventure_book")
					));
				}
				return this.renderer;
			}
		});
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "book", 1, state -> {
			if (state.getController().getCurrentRawAnimation() == null) {
				return state.setAndContinue(IDLE_CLOSED);
			}
			return PlayState.CONTINUE;
		})
				.triggerableAnim("open", OPEN)
				.triggerableAnim("close", CLOSE));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (!(world instanceof ServerWorld serverWorld) || !(entity instanceof ServerPlayerEntity player)) {
			return;
		}
		GeoItem.getOrAssignId(stack, serverWorld);

		Integer left = PENDING_GUI_OPEN.get(player.getUuid());
		if (left == null) {
			return;
		}
		if (left <= 0) {
			PENDING_GUI_OPEN.remove(player.getUuid());
			NetworkManager.sendToPlayer(player, new OpenAdventureBookPayload());
			return;
		}
		PENDING_GUI_OPEN.put(player.getUuid(), left - 1);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		user.setCurrentHand(hand);
		if (!world.isClient() && user instanceof ServerPlayerEntity serverPlayer) {
			triggerAnim(user, GeoItem.getOrAssignId(stack, (ServerWorld) world), "book", "open");
			PENDING_GUI_OPEN.put(serverPlayer.getUuid(), OPEN_GUI_DELAY_TICKS);
		}
		return TypedActionResult.consume(stack);
	}

	public static void handleCloseFromClient(ServerPlayerEntity player) {
		PENDING_GUI_OPEN.remove(player.getUuid());
		player.clearActiveItem();
		ItemStack stack = player.getMainHandStack();
		AdventureBookGeoItem book = stack.getItem() instanceof AdventureBookGeoItem b ? b : null;
		if (book == null) {
			stack = player.getOffHandStack();
			if (!(stack.getItem() instanceof AdventureBookGeoItem b2)) {
				return;
			}
			book = b2;
		}
		book.triggerAnim(player, GeoItem.getOrAssignId(stack, player.getServerWorld()), "book", "close");
	}
}

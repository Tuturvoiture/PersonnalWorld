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
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

/**
 * Variante GeckoLib du carnet d'aventurier.
 * Ne jamais référencer hors de {@code compat/geckolib/} (chargé via Class.forName).
 * <p>
 * Flux animations :
 * <ul>
 *   <li>Défaut → {@code idle_closed} (boucle)</li>
 *   <li>Clic droit → {@code open} puis {@code idle_open} (boucle)</li>
 *   <li>Fermeture GUI → {@code close} puis {@code idle_closed} (boucle)</li>
 * </ul>
 */
public final class AdventureBookGeoItem extends AdventureBookItem implements GeoItem {

    private static final RawAnimation IDLE_CLOSED = RawAnimation.begin().thenLoop("idle_closed");
    private static final RawAnimation OPEN        = RawAnimation.begin().thenPlay("open").thenLoop("idle_open");
    private static final RawAnimation CLOSE       = RawAnimation.begin().thenPlay("close").thenLoop("idle_closed");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AdventureBookGeoItem(Settings settings) {
        super(settings);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    /* -------------------------------------------------- rendu -------------------------------------------------- */

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

    /* ----------------------------------------------- animations ----------------------------------------------- */

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "book", 3, state -> state.setAndContinue(IDLE_CLOSED))
                .triggerableAnim("open",  OPEN)
                .triggerableAnim("close", CLOSE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world instanceof ServerWorld serverWorld) {
            GeoItem.getOrAssignId(stack, serverWorld);
        }
    }

    /* -------------------------------------------------- use --------------------------------------------------- */

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient() && user instanceof ServerPlayerEntity serverPlayer) {
            triggerAnim(user, GeoItem.getOrAssignId(stack, (ServerWorld) world), "book", "open");
            NetworkManager.sendToPlayer(serverPlayer, new OpenAdventureBookPayload());
        }
        return TypedActionResult.success(stack);
    }

    /**
     * Appelé depuis le handler C2S {@link fr.galsaxx.network.CloseAdventureBookPayload}.
     * Serveur uniquement.
     */
    public static void handleCloseFromClient(ServerPlayerEntity player) {
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

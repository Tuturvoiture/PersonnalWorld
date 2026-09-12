package fr.galsaxx;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Carnet d'aventurier — item de base (chargé sans GeckoLib).
 * Sur clic droit, envoie un paquet réseau pour ouvrir l'interface côté client.
 * {@link fr.galsaxx.compat.geckolib.AdventureBookGeoItem} ajoute les animations.
 */
public class AdventureBookItem extends Item {

    public AdventureBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            // Le paquet réseau est envoyé depuis GeckoLibHooks (ou la variante Geo)
            // afin de ne pas bloquer le chargement sans GeckoLib.
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}

package fr.galsaxx;

import dev.architectury.networking.NetworkManager;
import fr.galsaxx.network.OpenAdventureBookPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
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
		ItemStack stack = user.getStackInHand(hand);
		if (!world.isClient() && user instanceof ServerPlayerEntity serverPlayer) {
			NetworkManager.sendToPlayer(serverPlayer, new OpenAdventureBookPayload());
		}
		return TypedActionResult.success(stack);
	}
}

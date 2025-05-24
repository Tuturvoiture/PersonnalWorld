package fr.galsaxx;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class PersonnalWorldItem extends Item {
    public PersonnalWorldItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient) {
            player.sendMessage(Text.literal("Tu as utilisé l’objet PersonnalWorld !"), false);
        }
        return TypedActionResult.success(player.getStackInHand(hand), world.isClient());
    }
}

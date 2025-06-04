package fr.galsaxx.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.nbt.NbtCompound;
import fr.galsaxx.util.ReturnPositionSaver;
import net.minecraft.world.World;



import static net.minecraft.server.command.CommandManager.literal;

public class ReturnWorldCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("returnworld")
                .requires(source -> source.getEntity() instanceof ServerPlayerEntity)
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();

                    // Vérification : le joueur doit être dans son monde perso
                    String currentWorldId = player.getWorld().getRegistryKey().getValue().toString();
                    if (!currentWorldId.startsWith("personnalworld:perso_")) {
                        player.sendMessage(Text.literal("§cCette commande ne peut être utilisée que depuis votre monde perso !"), false);
                        return Command.SINGLE_SUCCESS;
                    }

                    NbtCompound posNbt = ((ReturnPositionSaver) player).getReturnPosition();

                    if (posNbt != null && posNbt.contains("x")) {
                        Identifier dimId = Identifier.tryParse(posNbt.getString("dim"));
                        RegistryKey<World> dimKey = RegistryKey.of(RegistryKeys.WORLD, dimId);
                        ServerWorld destination = player.getServer().getWorld(dimKey);

                        if (destination != null) {
                            player.teleport(
                                    destination,
                                    posNbt.getDouble("x"),
                                    posNbt.getDouble("y"),
                                    posNbt.getDouble("z"),
                                    java.util.Set.of(),
                                    posNbt.getFloat("yaw"),
                                    posNbt.getFloat("pitch")
                            );
                            player.sendMessage(Text.literal("Retour à votre emplacement d'origine !"), false);
                        } else {
                            player.sendMessage(Text.literal("La dimension d'origine n'existe plus."), false);
                        }
                    } else {
                        player.sendMessage(Text.literal("Aucune position de retour sauvegardée."), false);
                    }
                    return Command.SINGLE_SUCCESS;
                })
        );
    }

}

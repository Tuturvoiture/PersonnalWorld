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
                        player.sendMessage(Text.translatable("message.personnalworld.command_only_in_personal_world"), false);
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
                            player.sendMessage(Text.translatable("message.personnalworld.returned_to_origin"), false);
                        } else {
                            player.sendMessage(Text.translatable("message.personnalworld.origin_dimension_gone"), false);
                        }
                    } else {
                        player.sendMessage(Text.translatable("message.personnalworld.no_return_position"), false);
                    }
                    return Command.SINGLE_SUCCESS;
                })
        );
    }

}

package fr.galsaxx.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import fr.galsaxx.util.ReturnTeleport;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class ReturnWorldCommand {

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("returnworld")
				.requires(source -> source.getEntity() instanceof ServerPlayerEntity)
				.executes(context -> {
					ServerPlayerEntity player = context.getSource().getPlayer();
					String currentWorldId = player.getWorld().getRegistryKey().getValue().toString();
					if (!currentWorldId.startsWith("personnalworld:perso_")) {
						player.sendMessage(Text.translatable("message.personnalworld.command_only_in_personal_world"), false);
						return Command.SINGLE_SUCCESS;
					}
					ReturnTeleport.teleportHome(player);
					return Command.SINGLE_SUCCESS;
				})
		);
	}
}

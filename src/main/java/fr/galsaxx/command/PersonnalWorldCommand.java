package fr.galsaxx.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.galsaxx.config.PersonnalWorldConfig;
import fr.galsaxx.invite.IslandAccessService;
import fr.galsaxx.invite.IslandMemberEntry;
import fr.galsaxx.invite.IslandReloadService;
import fr.galsaxx.invite.IslandRole;
import fr.galsaxx.invite.PlayerRef;
import fr.galsaxx.invite.TransferOwnershipService;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * Player + debug commands under {@code /pw}.
 * <p>
 * invite / kick / role / visit acceptent {@link GameProfileArgumentType}
 * (joueur connecté <strong>ou</strong> offline via cache / UUID).
 */
public final class PersonnalWorldCommand {
	private PersonnalWorldCommand() {}

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> root = literal("pw")
				.requires(source -> source.getEntity() instanceof ServerPlayerEntity || source.hasPermissionLevel(4));

		root.then(literal("invite")
				.requires(source -> source.getEntity() instanceof ServerPlayerEntity)
				.then(argument("player", GameProfileArgumentType.gameProfile())
						.suggests((ctx, builder) -> CommandSource.suggestMatching(
								ctx.getSource().getPlayerNames(), builder))
						.then(argument("role", StringArgumentType.word())
								.executes(ctx -> {
									ServerPlayerEntity actor = ctx.getSource().getPlayer();
									PlayerRef target = firstProfile(ctx.getSource(),
											GameProfileArgumentType.getProfileArgument(ctx, "player"));
									if (target == null) {
										ctx.getSource().sendError(Text.translatable("message.personnalworld.pw.player_not_found"));
										return 0;
									}
									IslandRole role = IslandRole.parseInviteRole(StringArgumentType.getString(ctx, "role"));
									IslandAccessService.Result r = IslandAccessService.get()
											.invite(ctx.getSource().getServer(), actor, target, role);
									r.send(ctx.getSource());
									return r.ok() ? Command.SINGLE_SUCCESS : 0;
								}))));

		root.then(literal("kick")
				.requires(source -> source.getEntity() instanceof ServerPlayerEntity)
				.then(argument("player", GameProfileArgumentType.gameProfile())
						.suggests((ctx, builder) -> CommandSource.suggestMatching(
								ctx.getSource().getPlayerNames(), builder))
						.executes(ctx -> {
							ServerPlayerEntity actor = ctx.getSource().getPlayer();
							PlayerRef target = firstProfile(ctx.getSource(),
									GameProfileArgumentType.getProfileArgument(ctx, "player"));
							if (target == null) {
								ctx.getSource().sendError(Text.translatable("message.personnalworld.pw.player_not_found"));
								return 0;
							}
							IslandAccessService.Result r = IslandAccessService.get()
									.kick(ctx.getSource().getServer(), actor, target);
							r.send(ctx.getSource());
							return r.ok() ? Command.SINGLE_SUCCESS : 0;
						})));

		root.then(literal("role")
				.requires(source -> source.getEntity() instanceof ServerPlayerEntity)
				.then(argument("player", GameProfileArgumentType.gameProfile())
						.suggests((ctx, builder) -> CommandSource.suggestMatching(
								ctx.getSource().getPlayerNames(), builder))
						.then(argument("role", StringArgumentType.word())
								.executes(ctx -> {
									ServerPlayerEntity actor = ctx.getSource().getPlayer();
									PlayerRef target = firstProfile(ctx.getSource(),
											GameProfileArgumentType.getProfileArgument(ctx, "player"));
									if (target == null) {
										ctx.getSource().sendError(Text.translatable("message.personnalworld.pw.player_not_found"));
										return 0;
									}
									IslandRole role = IslandRole.parseInviteRole(StringArgumentType.getString(ctx, "role"));
									IslandAccessService.Result r = IslandAccessService.get()
											.setRole(ctx.getSource().getServer(), actor, target, role);
									r.send(ctx.getSource());
									return r.ok() ? Command.SINGLE_SUCCESS : 0;
								}))));

		root.then(literal("list")
				.requires(source -> source.getEntity() instanceof ServerPlayerEntity)
				.executes(ctx -> {
					ServerPlayerEntity actor = ctx.getSource().getPlayer();
					ctx.getSource().sendFeedback(() -> Text.translatable("message.personnalworld.pw.list_header"), false);
					for (IslandMemberEntry e : IslandAccessService.get().listMembers(ctx.getSource().getServer(), actor)) {
						String tag = e.temporary() ? "TEMP" : e.role().name();
						String name = e.nameHint().isEmpty() ? e.uuid().toString() : e.nameHint();
						ctx.getSource().sendFeedback(() -> Text.literal(" - [" + tag + "] " + name), false);
					}
					return Command.SINGLE_SUCCESS;
				}));

		root.then(literal("visit")
				.requires(source -> source.getEntity() instanceof ServerPlayerEntity)
				.then(argument("player", GameProfileArgumentType.gameProfile())
						.suggests((ctx, builder) -> CommandSource.suggestMatching(
								ctx.getSource().getPlayerNames(), builder))
						.executes(ctx -> {
							ServerPlayerEntity visitor = ctx.getSource().getPlayer();
							PlayerRef host = firstProfile(ctx.getSource(),
									GameProfileArgumentType.getProfileArgument(ctx, "player"));
							if (host == null) {
								ctx.getSource().sendError(Text.translatable("message.personnalworld.pw.player_not_found"));
								return 0;
							}
							IslandAccessService.Result r = IslandAccessService.get()
									.visit(ctx.getSource().getServer(), visitor, host, null);
							r.send(ctx.getSource());
							return r.ok() ? Command.SINGLE_SUCCESS : 0;
						})
						.then(argument("island", StringArgumentType.greedyString())
								.executes(ctx -> {
									ServerPlayerEntity visitor = ctx.getSource().getPlayer();
									PlayerRef host = firstProfile(ctx.getSource(),
											GameProfileArgumentType.getProfileArgument(ctx, "player"));
									if (host == null) {
										ctx.getSource().sendError(Text.translatable("message.personnalworld.pw.player_not_found"));
										return 0;
									}
									String island = StringArgumentType.getString(ctx, "island");
									IslandAccessService.Result r = IslandAccessService.get()
											.visit(ctx.getSource().getServer(), visitor, host, island);
									r.send(ctx.getSource());
									return r.ok() ? Command.SINGLE_SUCCESS : 0;
								}))));

		root.then(literal("leave")
				.requires(source -> source.getEntity() instanceof ServerPlayerEntity)
				.executes(ctx -> {
					ServerPlayerEntity player = ctx.getSource().getPlayer();
					IslandAccessService.Result r = IslandAccessService.get().leave(player);
					r.send(ctx.getSource());
					return r.ok() ? Command.SINGLE_SUCCESS : 0;
				}));

		root.then(literal("debug")
				.requires(source -> source.hasPermissionLevel(4))
				.then(literal("setowner")
						.then(argument("target", StringArgumentType.string())
								.then(argument("newOwner", EntityArgumentType.player())
										.executes(ctx -> debugSetOwner(ctx.getSource(),
												StringArgumentType.getString(ctx, "target"),
												EntityArgumentType.getPlayer(ctx, "newOwner"))))))
				.then(literal("reload-access")
						.executes(ctx -> debugReloadAccess(ctx.getSource(), null))
						.then(argument("target", StringArgumentType.greedyString())
								.executes(ctx -> debugReloadAccess(ctx.getSource(),
										StringArgumentType.getString(ctx, "target")))))
				.then(literal("reload-island")
						.then(argument("target", StringArgumentType.greedyString())
								.executes(ctx -> debugReloadIsland(ctx.getSource(),
										StringArgumentType.getString(ctx, "target"))))));

		dispatcher.register(root);
	}

	private static PlayerRef firstProfile(ServerCommandSource source, Collection<GameProfile> profiles) {
		if (profiles == null || profiles.isEmpty()) {
			return null;
		}
		GameProfile profile = profiles.iterator().next();
		if (profile.getId() == null) {
			return null;
		}
		return PlayerRef.of(profile, source.getServer());
	}

	private static boolean debugEnabled(ServerCommandSource source) {
		if (!PersonnalWorldConfig.get().enableDebugCommands()) {
			source.sendError(Text.translatable("message.personnalworld.pw.debug_disabled"));
			return false;
		}
		return true;
	}

	private static int debugSetOwner(ServerCommandSource source, String target, ServerPlayerEntity newOwner) {
		if (!debugEnabled(source)) {
			return 0;
		}
		IslandAccessService.Result r = TransferOwnershipService.transferToPlayer(
				source.getServer(), target, newOwner);
		r.send(source);
		return r.ok() ? Command.SINGLE_SUCCESS : 0;
	}

	private static int debugReloadAccess(ServerCommandSource source, String target) {
		if (!debugEnabled(source)) {
			return 0;
		}
		IslandAccessService.Result r = IslandReloadService.reloadAccess(source.getServer(), target);
		r.send(source);
		return r.ok() ? Command.SINGLE_SUCCESS : 0;
	}

	private static int debugReloadIsland(ServerCommandSource source, String target) {
		if (!debugEnabled(source)) {
			return 0;
		}
		IslandAccessService.Result r = IslandReloadService.reloadIsland(source.getServer(), source, target);
		r.send(source);
		return r.ok() ? Command.SINGLE_SUCCESS : 0;
	}
}

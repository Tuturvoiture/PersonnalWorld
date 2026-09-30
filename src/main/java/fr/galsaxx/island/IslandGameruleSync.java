package fr.galsaxx.island;

import fr.galsaxx.PersonnalWorld;
import fr.galsaxx.config.PersonnalWorldConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.Map;

public final class IslandGameruleSync {
	private IslandGameruleSync() {}

	public static void apply(ServerWorld island) {
		if (island == null) {
			return;
		}
		MinecraftServer server = island.getServer();
		ServerWorld overworld = server.getWorld(World.OVERWORLD);
		if (overworld == null) {
			return;
		}
		Map<String, String> normal = snapshot(overworld.getGameRules());
		Map<String, String> current = snapshot(island.getGameRules());
		Map<String, String> overlay = fr.galsaxx.util.PersonnalWorldUtil.getGameruleOverlay(island);
		Map<String, String> resolved = IslandGameruleResolver.resolve(
				PersonnalWorldConfig.get().syncGamerules(),
				normal,
				current,
				overlay);
		write(island.getGameRules(), resolved, server);
	}

	public static void applyLoadedPersonalWorlds(MinecraftServer server) {
		for (ServerWorld world : server.getWorlds()) {
			String id = world.getRegistryKey().getValue().toString();
			if (fr.galsaxx.invite.IslandIds.isPersonalIsland(id)) {
				apply(world);
			}
		}
	}

	private static Map<String, String> snapshot(GameRules rules) {
		Map<String, String> map = new LinkedHashMap<>();
		rules.accept(new GameRules.Visitor() {
			@Override
			public <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
				GameRules.Rule<T> rule = rules.get(key);
				if (rule instanceof GameRules.BooleanRule booleanRule) {
					map.put(key.getName(), Boolean.toString(booleanRule.get()));
				} else if (rule instanceof GameRules.IntRule intRule) {
					map.put(key.getName(), Integer.toString(intRule.get()));
				}
			}
		});
		return map;
	}

	private static void write(GameRules rules, Map<String, String> resolved, MinecraftServer server) {
		rules.accept(new GameRules.Visitor() {
			@Override
			public <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
				String value = resolved.get(key.getName());
				if (value == null) {
					return;
				}
				GameRules.Rule<T> rule = rules.get(key);
				if (rule instanceof GameRules.BooleanRule booleanRule) {
					booleanRule.set(Boolean.parseBoolean(value), server);
				} else if (rule instanceof GameRules.IntRule intRule) {
					try {
						intRule.set(Integer.parseInt(value), server);
					} catch (NumberFormatException e) {
						PersonnalWorld.LOGGER.debug("Skip gamerule {} value {}", key.getName(), value);
					}
				}
			}
		});
	}
}

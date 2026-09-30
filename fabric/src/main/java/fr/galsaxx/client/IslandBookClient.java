package fr.galsaxx.client;

import fr.galsaxx.network.IslandBookNetworking;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public final class IslandBookClient {
	public static List<IslandBookNetworking.Card> owned = List.of();
	public static List<IslandBookNetworking.Card> invited = List.of();
	public static int maxIslands = 3;
	public static List<IslandBookNetworking.PresetInfo> presets = List.of();
	public static IslandBookNetworking.DetailPayload detail;

	private IslandBookClient() {}

	public static void acceptSync(IslandBookNetworking.SyncPayload payload) {
		owned = List.copyOf(payload.owned());
		invited = List.copyOf(payload.invited());
		maxIslands = payload.maxIslands();
		presets = List.copyOf(payload.presets());
		if (MinecraftClient.getInstance().currentScreen instanceof AdventureBookScreen screen) {
			screen.refresh();
		}
	}

	public static void acceptDetail(IslandBookNetworking.DetailPayload payload) {
		detail = payload;
		if (MinecraftClient.getInstance().currentScreen instanceof AdventureBookScreen screen) {
			screen.refresh();
		}
	}
}

package fr.galsaxx.invite;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Résolution joueur online ou offline (user cache) pour /pw kick|visit|…
 */
public final class PlayerRef {
	private final UUID uuid;
	private final String name;
	private final ServerPlayerEntity online;

	private PlayerRef(UUID uuid, String name, ServerPlayerEntity online) {
		this.uuid = uuid;
		this.name = name == null ? "" : name;
		this.online = online;
	}

	public UUID uuid() {
		return uuid;
	}

	public String name() {
		return name.isEmpty() ? uuid.toString() : name;
	}

	public Optional<ServerPlayerEntity> online() {
		return Optional.ofNullable(online);
	}

	public boolean isOnline() {
		return online != null;
	}

	public static PlayerRef of(ServerPlayerEntity player) {
		return new PlayerRef(player.getUuid(), player.getGameProfile().getName(), player);
	}

	public static PlayerRef of(GameProfile profile, MinecraftServer server) {
		ServerPlayerEntity online = server.getPlayerManager().getPlayer(profile.getId());
		String name = profile.getName() != null ? profile.getName() : "";
		return new PlayerRef(profile.getId(), name, online);
	}

	/**
	 * Résout un nom ou UUID : joueur online d’abord, sinon user cache, sinon UUID brut.
	 */
	public static Optional<PlayerRef> resolve(MinecraftServer server, String raw) {
		if (raw == null || raw.isBlank()) {
			return Optional.empty();
		}
		String key = raw.trim();
		ServerPlayerEntity online = server.getPlayerManager().getPlayer(key);
		if (online != null) {
			return Optional.of(of(online));
		}
		try {
			UUID uuid = UUID.fromString(key);
			ServerPlayerEntity byId = server.getPlayerManager().getPlayer(uuid);
			if (byId != null) {
				return Optional.of(of(byId));
			}
			return server.getUserCache().getByUuid(uuid)
					.map(p -> of(p, server))
					.or(() -> Optional.of(new PlayerRef(uuid, "", null)));
		} catch (IllegalArgumentException ignored) {
			// not a UUID
		}
		return server.getUserCache().findByName(key).map(p -> of(p, server));
	}
}

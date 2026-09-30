package fr.galsaxx.island;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import fr.galsaxx.invite.IslandIds;
import fr.galsaxx.util.PersonnalWorldUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

/**
 * 1.21.1 has no per-world {@code pvp} gamerule (it is a global server property).
 * The island overlay key {@code pvp=false} blocks player-versus-player damage in that dimension.
 */
public final class IslandPvpGuard {
	private IslandPvpGuard() {}

	public static void register() {
		EntityEvent.LIVING_HURT.register((entity, source, amount) -> {
			if (entity.getWorld() instanceof ServerWorld hurtWorld
					&& IslandIds.isPersonalIsland(hurtWorld.getRegistryKey().getValue().toString())) {
				PlayerEntity responsible = responsible(source, hurtWorld);
				if (responsible != null && IslandRoleGuard.visitor(responsible)) {
					return EventResult.interruptFalse();
				}
			}
			if (!(entity instanceof PlayerEntity) || !(source.getAttacker() instanceof PlayerEntity)) {
				return EventResult.pass();
			}
			if (!(entity.getWorld() instanceof ServerWorld world)) {
				return EventResult.pass();
			}
			if (!IslandIds.isPersonalIsland(world.getRegistryKey().getValue().toString())) {
				return EventResult.pass();
			}
			if (source.getAttacker() instanceof PlayerEntity attacker && IslandRoleGuard.visitor(attacker)) {
				return EventResult.interruptFalse();
			}
			String value = PersonnalWorldUtil.getGameruleOverlay(world).get("pvp");
			if (value != null && !Boolean.parseBoolean(value)) {
				return EventResult.interruptFalse();
			}
			return EventResult.pass();
		});
	}

	/**
	 * Joueur derrière le coup : attaquant, projectile, familier, ou entité qui porte son pseudo.
	 */
	private static PlayerEntity responsible(DamageSource source, ServerWorld world) {
		PlayerEntity player = asPlayer(source.getAttacker());
		if (player == null) {
			player = asPlayer(source.getSource());
		}
		if (player != null) {
			return player;
		}
		Entity cause = source.getAttacker() != null ? source.getAttacker() : source.getSource();
		if (cause instanceof ProjectileEntity projectile && projectile.getOwner() instanceof PlayerEntity owner) {
			return owner;
		}
		if (cause instanceof TameableEntity tame && tame.getOwner() instanceof PlayerEntity owner) {
			return owner;
		}
		if (cause == null) {
			return null;
		}
		String name = cause.getName().getString();
		for (ServerPlayerEntity online : world.getPlayers()) {
			if (name.equals(online.getName().getString())) {
				return online;
			}
		}
		return null;
	}

	private static PlayerEntity asPlayer(Entity entity) {
		return entity instanceof PlayerEntity player ? player : null;
	}
}

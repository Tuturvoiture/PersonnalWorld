package fr.galsaxx.network;

import fr.galsaxx.PersonnalWorld;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** C→S : le joueur ferme l'interface du carnet (payload vide). */
public record CloseAdventureBookPayload() implements CustomPayload {
	public static final CustomPayload.Id<CloseAdventureBookPayload> ID =
			new CustomPayload.Id<>(Identifier.of(PersonnalWorld.MOD_ID, "close_adventure_book"));
	public static final PacketCodec<RegistryByteBuf, CloseAdventureBookPayload> CODEC =
			PacketCodec.unit(new CloseAdventureBookPayload());

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}

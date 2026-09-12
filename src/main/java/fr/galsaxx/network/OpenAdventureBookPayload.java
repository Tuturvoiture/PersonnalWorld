package fr.galsaxx.network;

import fr.galsaxx.PersonnalWorld;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** S→C : ouvrir l'interface du carnet (payload vide). */
public record OpenAdventureBookPayload() implements CustomPayload {
	public static final CustomPayload.Id<OpenAdventureBookPayload> ID =
			new CustomPayload.Id<>(Identifier.of(PersonnalWorld.MOD_ID, "open_adventure_book"));
	public static final PacketCodec<RegistryByteBuf, OpenAdventureBookPayload> CODEC =
			PacketCodec.unit(new OpenAdventureBookPayload());

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}

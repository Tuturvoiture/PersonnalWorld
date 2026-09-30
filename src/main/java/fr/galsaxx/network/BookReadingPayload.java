package fr.galsaxx.network;

import fr.galsaxx.PersonnalWorld;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

/** S→C : ce joueur lit (ou non) son carnet. N’anime que son exemplaire. */
public record BookReadingPayload(long msb, long lsb, boolean reading) implements CustomPayload {
	public static final CustomPayload.Id<BookReadingPayload> ID =
			new CustomPayload.Id<>(Identifier.of(PersonnalWorld.MOD_ID, "book_reading"));
	public static final PacketCodec<RegistryByteBuf, BookReadingPayload> CODEC = PacketCodec.tuple(
			PacketCodecs.VAR_LONG, BookReadingPayload::msb,
			PacketCodecs.VAR_LONG, BookReadingPayload::lsb,
			PacketCodecs.BOOL, BookReadingPayload::reading,
			BookReadingPayload::new);

	public BookReadingPayload(UUID playerId, boolean reading) {
		this(playerId.getMostSignificantBits(), playerId.getLeastSignificantBits(), reading);
	}

	public UUID playerId() {
		return new UUID(this.msb, this.lsb);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}

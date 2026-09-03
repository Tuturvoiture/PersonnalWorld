package fr.galsaxx.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

/** Bloc de référence spawn — incassable, placement code-only. */
public final class PersonalSpawnMarkerBlock extends Block {
	public PersonalSpawnMarkerBlock() {
		super(AbstractBlock.Settings.create()
				.strength(-1.0f, 3_600_000.0f)
				.dropsNothing());
	}
}

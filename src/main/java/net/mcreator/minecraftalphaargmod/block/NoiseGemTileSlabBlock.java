
package net.mcreator.minecraftalphaargmod.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SlabBlock;

public class NoiseGemTileSlabBlock extends SlabBlock {
	public NoiseGemTileSlabBlock() {
		super(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).strength(1f, 10f).requiresCorrectToolForDrops().dynamicShape());
	}
}

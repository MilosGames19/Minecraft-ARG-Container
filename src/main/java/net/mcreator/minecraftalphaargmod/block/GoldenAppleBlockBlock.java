
package net.mcreator.minecraftalphaargmod.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;

public class GoldenAppleBlockBlock extends Block {
	public GoldenAppleBlockBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.GRASS).strength(0.2f));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}
}

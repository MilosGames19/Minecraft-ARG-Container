
package net.mcreator.minecraftalphaargmod.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;

public class FakeGrassBlock extends Block {
	public FakeGrassBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.GRAVEL).strength(0.6f, 0f));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}
}

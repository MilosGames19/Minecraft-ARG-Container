
package net.mcreator.minecraftalphaargmod.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;

public class EyesBlock extends Block {
	public EyesBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.SLIME_BLOCK).strength(3f));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}
}

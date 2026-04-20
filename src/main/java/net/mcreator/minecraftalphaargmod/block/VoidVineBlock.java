package net.mcreator.minecraftalphaargmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class VoidVineBlock extends VineBlock {

	public VoidVineBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.GRASS).strength(0.2f).noCollission().noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
	}

	@Override
	public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
		return true;
	}
}
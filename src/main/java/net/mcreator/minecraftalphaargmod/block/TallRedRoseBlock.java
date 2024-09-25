
package net.mcreator.minecraftalphaargmod.block;

import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModBlocks;

public class TallRedRoseBlock extends FlowerBlock {
	public TallRedRoseBlock() {
		super(() -> MobEffects.MOVEMENT_SPEED, 100, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS).instabreak().noCollission().offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
	}

	@Override
	public int getEffectDuration() {
		return 100;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 100;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public boolean mayPlaceOn(BlockState groundState, BlockGetter worldIn, BlockPos pos) {
		return groundState.is(TheArgContainerModBlocks.MIXED_GRASS_BLOCK.get()) || groundState.is(TheArgContainerModBlocks.YELLOW_GRASS_BLOCK.get()) || groundState.is(TheArgContainerModBlocks.GRASS_BLOCK.get())
				|| groundState.is(TheArgContainerModBlocks.SPRING_GRASS_BLOCK.get()) || groundState.is(Blocks.GRASS_BLOCK) || groundState.is(TheArgContainerModBlocks.SUMMER_GRASS_BLOCK.get())
				|| groundState.is(TheArgContainerModBlocks.SPRING_GRASS.get()) || groundState.is(TheArgContainerModBlocks.SUMMER_GRASS.get()) || groundState.is(TheArgContainerModBlocks.STONE_GRASS.get())
				|| groundState.is(TheArgContainerModBlocks.DUTYTIME_GRASS.get());
	}

	@Override
	public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
		BlockPos blockpos = pos.below();
		BlockState groundState = worldIn.getBlockState(blockpos);
		return this.mayPlaceOn(groundState, worldIn, blockpos);
	}
}

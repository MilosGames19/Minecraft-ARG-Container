
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

import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModBlocks;

public class LichenBlock extends FlowerBlock {
	public LichenBlock() {
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
		return groundState.is(MinecraftAlphaArgModModBlocks.LICHEN_BRICKS.get()) || groundState.is(MinecraftAlphaArgModModBlocks.LICHEN_MASS.get()) || groundState.is(MinecraftAlphaArgModModBlocks.LICHEN_COBBLESTONE.get())
				|| groundState.is(MinecraftAlphaArgModModBlocks.LOW_RIVER_STONE.get()) || groundState.is(MinecraftAlphaArgModModBlocks.LOW_RIVERBED.get()) || groundState.is(MinecraftAlphaArgModModBlocks.LOW_MYCON.get())
				|| groundState.is(MinecraftAlphaArgModModBlocks.GRASS_PATHWAY.get()) || groundState.is(MinecraftAlphaArgModModBlocks.MIXED_GRASS_BLOCK.get()) || groundState.is(MinecraftAlphaArgModModBlocks.YELLOW_GRASS_BLOCK.get())
				|| groundState.is(MinecraftAlphaArgModModBlocks.ASH_GRASS.get()) || groundState.is(MinecraftAlphaArgModModBlocks.GRASS_BLOCK.get()) || groundState.is(MinecraftAlphaArgModModBlocks.SOUL_GRASS.get())
				|| groundState.is(Blocks.GRASS_BLOCK) || groundState.is(Blocks.STONE) || groundState.is(MinecraftAlphaArgModModBlocks.SOUL_MOSSY_COBBLESTONE.get()) || groundState.is(Blocks.MOSSY_COBBLESTONE) || groundState.is(Blocks.MOSS_BLOCK);
	}

	@Override
	public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
		BlockPos blockpos = pos.below();
		BlockState groundState = worldIn.getBlockState(blockpos);
		return this.mayPlaceOn(groundState, worldIn, blockpos);
	}
}

package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModBlocks;

public class SoulDirtOnRandomClientDisplayTickProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world.isEmptyBlock(BlockPos.containing(x, y + 1, z)) && ((world.getBlockState(BlockPos.containing(x + 1, y, z))).getBlock() == TheArgContainerModBlocks.SOUL_GRASS.get()
				|| (world.getBlockState(BlockPos.containing(x - 1, y, z))).getBlock() == TheArgContainerModBlocks.SOUL_GRASS.get() || (world.getBlockState(BlockPos.containing(x, y, z - 1))).getBlock() == TheArgContainerModBlocks.SOUL_GRASS.get()
				|| (world.getBlockState(BlockPos.containing(x, y, z + 1))).getBlock() == TheArgContainerModBlocks.SOUL_GRASS.get() || (world.getBlockState(BlockPos.containing(x + 1, y + 1, z))).getBlock() == TheArgContainerModBlocks.SOUL_GRASS.get()
				|| (world.getBlockState(BlockPos.containing(x - 1, y + 1, z))).getBlock() == TheArgContainerModBlocks.SOUL_GRASS.get()
				|| (world.getBlockState(BlockPos.containing(x + 1, y - 1, z))).getBlock() == TheArgContainerModBlocks.SOUL_GRASS.get()
				|| (world.getBlockState(BlockPos.containing(x - 1, y - 1, z))).getBlock() == TheArgContainerModBlocks.SOUL_GRASS.get()
				|| (world.getBlockState(BlockPos.containing(x, y + 1, z + 1))).getBlock() == TheArgContainerModBlocks.SOUL_GRASS.get()
				|| (world.getBlockState(BlockPos.containing(x, y + 1, z - 1))).getBlock() == TheArgContainerModBlocks.SOUL_GRASS.get()
				|| (world.getBlockState(BlockPos.containing(x, y - 1, z + 1))).getBlock() == TheArgContainerModBlocks.SOUL_GRASS.get()
				|| (world.getBlockState(BlockPos.containing(x, y - 1, z - 1))).getBlock() == TheArgContainerModBlocks.SOUL_GRASS.get())) {
			world.setBlock(BlockPos.containing(x, y, z), TheArgContainerModBlocks.SOUL_GRASS.get().defaultBlockState(), 3);
		}
	}
}

package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModBlocks;

public class AssetstilesacceptRightclickedOnBlockProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		world.setBlock(BlockPos.containing(x, y, z), MinecraftAlphaArgModModBlocks.UNUSED_BLOCK_3.get().defaultBlockState(), 3);
	}
}

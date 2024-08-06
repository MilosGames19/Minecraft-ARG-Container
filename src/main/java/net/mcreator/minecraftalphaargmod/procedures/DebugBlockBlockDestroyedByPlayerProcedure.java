package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModBlocks;

public class DebugBlockBlockDestroyedByPlayerProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity.isShiftKeyDown()) {
			world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
		} else {
			world.setBlock(BlockPos.containing(x, y, z), MinecraftAlphaArgModModBlocks.DEBUG_BLOCK.get().defaultBlockState(), 3);
		}
	}
}

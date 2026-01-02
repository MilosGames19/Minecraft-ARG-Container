package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModBlocks;
import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;

public class SmileBlockValidPlacementConditionProcedure {
	public static boolean execute(LevelAccessor world, double x, double y, double z) {
		if ((world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == TheArgContainerModBlocks.CHARED_PLANKS.get() && (world.getBlockState(BlockPos.containing(x, y - 2, z))).getBlock() == TheArgContainerModBlocks.CHARED_PLANKS.get()
				|| McconfigConfiguration.PLACE_THE_SMILE_BLOCK_ANYWHERE.get() == true) {
			return true;
		}
		return false;
	}
}

package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

public class SaplingValidPlacementProcedure {
	public static boolean execute(LevelAccessor world, double x, double y, double z) {
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft:dirt"))) {
			return true;
		}
		return false;
	}
}

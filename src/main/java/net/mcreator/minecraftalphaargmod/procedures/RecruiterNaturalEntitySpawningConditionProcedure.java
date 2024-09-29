package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModGameRules;

public class RecruiterNaturalEntitySpawningConditionProcedure {
	public static boolean execute(LevelAccessor world) {
		double raytraceDistance = 0;
		boolean entityFound = false;
		if (world.getLevelData().getGameRules().getBoolean(TheArgContainerModGameRules.SVALLOWRNET) == true) {
			return true;
		}
		return false;
	}
}

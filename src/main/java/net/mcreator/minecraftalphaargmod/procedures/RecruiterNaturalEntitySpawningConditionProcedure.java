package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModGameRules;

public class RecruiterNaturalEntitySpawningConditionProcedure {
	public static boolean execute(LevelAccessor world) {
		if (world.getLevelData().getGameRules().getBoolean(MinecraftAlphaArgModModGameRules.SVALLOWRNET) == true) {
			return true;
		}
		return false;
	}
}

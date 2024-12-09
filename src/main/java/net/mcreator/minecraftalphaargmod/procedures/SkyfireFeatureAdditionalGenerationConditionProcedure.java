package net.mcreator.minecraftalphaargmod.procedures;

import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;

public class SkyfireFeatureAdditionalGenerationConditionProcedure {
	public static boolean execute() {
		if (McconfigConfiguration.SKY_FLAME_GENERATION.get() == true) {
			return true;
		}
		return false;
	}
}

package net.mcreator.minecraftalphaargmod.procedures;

import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;

public class FlamelilyFeatureAdditionalGenerationConditionProcedure {
	public static boolean execute() {
		if (McconfigConfiguration.LILYPAD_GENERATION.get() == true) {
			return true;
		}
		return false;
	}
}

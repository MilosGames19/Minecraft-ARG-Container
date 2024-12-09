package net.mcreator.minecraftalphaargmod.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class McconfigConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;
	public static final ForgeConfigSpec.ConfigValue<Boolean> SKY_FLAME_GENERATION;
	public static final ForgeConfigSpec.ConfigValue<Boolean> LILYPAD_GENERATION;
	static {
		BUILDER.push("generation");
		SKY_FLAME_GENERATION = BUILDER.define("sky_flame_generation", true);
		LILYPAD_GENERATION = BUILDER.define("lilypad_generation", true);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}

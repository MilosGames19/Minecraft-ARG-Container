package net.mcreator.minecraftalphaargmod.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class McconfigConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;
	public static final ForgeConfigSpec.ConfigValue<Boolean> SKY_FLAME_GENERATION;
	public static final ForgeConfigSpec.ConfigValue<Boolean> LILYPAD_GENERATION;
	public static final ForgeConfigSpec.ConfigValue<Boolean> DASH_OVERLAY;
	public static final ForgeConfigSpec.ConfigValue<Boolean> SKY;
	public static final ForgeConfigSpec.ConfigValue<Boolean> GAME_CRASH;
	public static final ForgeConfigSpec.ConfigValue<Boolean> SV_ALLOWRNET;
	static {
		BUILDER.push("generation");
		SKY_FLAME_GENERATION = BUILDER.define("sky_flame_generation", true);
		LILYPAD_GENERATION = BUILDER.define("lilypad_generation", true);
		BUILDER.pop();
		BUILDER.push("DEBUG");
		DASH_OVERLAY = BUILDER.define("dash_overlay", false);
		SKY = BUILDER.define("sky", true);
		BUILDER.pop();
		BUILDER.push("⚠Dangerous⚠");
		GAME_CRASH = BUILDER.comment("This option is disabled by default to prevent accidental crashes.").define("game_crash", false);
		BUILDER.pop();
		BUILDER.push("Entity");
		SV_ALLOWRNET = BUILDER.define("sv_allowrnet", true);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}

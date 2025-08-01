package net.mcreator.minecraftalphaargmod.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class McconfigConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;
	public static final ForgeConfigSpec.ConfigValue<Boolean> SKY_FLAME_GENERATION;
	public static final ForgeConfigSpec.ConfigValue<Boolean> LILYPAD_GENERATION;
	public static final ForgeConfigSpec.ConfigValue<Boolean> DASH_OVERLAY;
	public static final ForgeConfigSpec.ConfigValue<Boolean> GAME_CRASH;
	public static final ForgeConfigSpec.ConfigValue<Boolean> SV_ALLOWRNET;
	public static final ForgeConfigSpec.ConfigValue<Boolean> TBOTV_SKY;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ALPHAVER_NEBULA_EVENT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ALPHAVER_DASH_ABILITY;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ALPHAVER_LONG_JUMP_ABILITY;
	public static final ForgeConfigSpec.ConfigValue<Boolean> FIXER_MOD;
	static {
		BUILDER.push("generation");
		SKY_FLAME_GENERATION = BUILDER.define("sky_flame_generation", true);
		LILYPAD_GENERATION = BUILDER.define("lilypad_generation", true);
		BUILDER.pop();
		BUILDER.push("DEBUG");
		DASH_OVERLAY = BUILDER.define("dash_overlay", false);
		BUILDER.pop();
		BUILDER.push("⚠Dangerous⚠");
		GAME_CRASH = BUILDER.comment("This option is disabled by default to prevent accidental crashes.").define("game_crash", false);
		BUILDER.pop();
		BUILDER.push("Entity");
		SV_ALLOWRNET = BUILDER.define("sv_allowrnet", true);
		BUILDER.pop();
		BUILDER.push("Sky");
		TBOTV_SKY = BUILDER.define("tbotv_sky", false);
		ALPHAVER_NEBULA_EVENT = BUILDER.define("alphaver_nebula_event", false);
		BUILDER.pop();
		BUILDER.push("Features");
		ALPHAVER_DASH_ABILITY = BUILDER.define("alphaver_dash_ability", true);
		ALPHAVER_LONG_JUMP_ABILITY = BUILDER.define("alphaver_long_jump_ability", true);
		FIXER_MOD = BUILDER.define("fixer_mod", false);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}

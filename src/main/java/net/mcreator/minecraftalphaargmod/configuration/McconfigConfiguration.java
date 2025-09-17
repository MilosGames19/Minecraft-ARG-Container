package net.mcreator.minecraftalphaargmod.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class McconfigConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;
	public static final ForgeConfigSpec.ConfigValue<Boolean> SKY_FLAME_GENERATION;
	public static final ForgeConfigSpec.ConfigValue<Boolean> LILYPAD_GENERATION;
	public static final ForgeConfigSpec.ConfigValue<Boolean> GAME_CRASH;
	public static final ForgeConfigSpec.ConfigValue<Boolean> SV_ALLOWRNET;
	public static final ForgeConfigSpec.ConfigValue<Boolean> TBOTV_SKY;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ALPHAVER_NEBULA_EVENT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> TBOTV_MOON_EVENT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ALPHAVER_DASH_ABILITY;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ALPHAVER_LONG_JUMP_ABILITY;
	public static final ForgeConfigSpec.ConfigValue<Boolean> FIXER_MOD;
	public static final ForgeConfigSpec.ConfigValue<Boolean> QUICKLOAD_BUTTON;
	public static final ForgeConfigSpec.ConfigValue<Boolean> DONT_LOOK_AT_THE_MOON;
	static {
		BUILDER.push("generation");
		SKY_FLAME_GENERATION = BUILDER.comment("Enable/disable alphaver celestial flame generation.").define("sky_flame_generation", true);
		LILYPAD_GENERATION = BUILDER.comment("Enable/disable alphaver lilipad generation.").define("lilypad_generation", true);
		BUILDER.pop();
		BUILDER.push("⚠Dangerous⚠");
		GAME_CRASH = BUILDER.comment("This option is disabled by default to prevent accidental crashes.").define("game_crash", false);
		BUILDER.pop();
		BUILDER.push("Entity");
		SV_ALLOWRNET = BUILDER.comment("Enable/disable recruiter connections.").define("sv_allowrnet", true);
		BUILDER.pop();
		BUILDER.push("Sky");
		TBOTV_SKY = BUILDER.comment("Enable/disable TBOTV sky in overworld.").define("tbotv_sky", false);
		ALPHAVER_NEBULA_EVENT = BUILDER.comment("Enable/disable alphaver nebula event.").define("alphaver_nebula_event", false);
		TBOTV_MOON_EVENT = BUILDER.comment("Enable/disable broken moon event.").define("tbotv_moon_event", false);
		BUILDER.pop();
		BUILDER.push("Features");
		ALPHAVER_DASH_ABILITY = BUILDER.comment("Enable/disable dash ability.").define("alphaver_dash_ability", true);
		ALPHAVER_LONG_JUMP_ABILITY = BUILDER.comment("Enable/disable long jump ability.").define("alphaver_long_jump_ability", true);
		FIXER_MOD = BUILDER.comment("Enable/disable Regenerating static.").define("fixer_mod", false);
		QUICKLOAD_BUTTON = BUILDER.comment("Enable/disable the quickload button on the main menu (restart required).").define("quickload_button", true);
		DONT_LOOK_AT_THE_MOON = BUILDER.comment("Enable/desabke don't look at the moon event (restart required)").define("dont_look_at_the_moon", false);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}

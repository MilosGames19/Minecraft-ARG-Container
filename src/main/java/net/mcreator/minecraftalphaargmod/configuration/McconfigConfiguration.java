package net.mcreator.minecraftalphaargmod.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class McconfigConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.ConfigValue<Boolean> SKY_FLAME_GENERATION;
	public static final ForgeConfigSpec.ConfigValue<Boolean> LILYPAD_GENERATION;
	public static final ForgeConfigSpec.ConfigValue<Boolean> TREE_GENERATION;
	public static final ForgeConfigSpec.ConfigValue<Boolean> CUSTOM_PANORAMA;
	public static final ForgeConfigSpec.ConfigValue<Boolean> QUICKLOAD_BUTTON;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ALPHAVER_CONSOLE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> GAME_CRASH;
	public static final ForgeConfigSpec.ConfigValue<Boolean> SV_ALLOWRNET;
	public static final ForgeConfigSpec.ConfigValue<Boolean> TBOTV_SKY;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ALPHAVER_NEBULA_EVENT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> TBOTV_MOON_EVENT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ALPHAVER_DASH_ABILITY;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ALPHAVER_LONG_JUMP_ABILITY;
	public static final ForgeConfigSpec.ConfigValue<Boolean> FIXER_MOD;
	public static final ForgeConfigSpec.ConfigValue<Boolean> DONT_LOOK_AT_THE_MOON;
	public static final ForgeConfigSpec.ConfigValue<Boolean> PLACE_THE_SMILE_BLOCK_ANYWHERE;
	static {
		BUILDER.push("Generation");
		SKY_FLAME_GENERATION = BUILDER.comment("Enable/disable alphaver celestial flame generation.").define("sky_flame_generation", true);
		LILYPAD_GENERATION = BUILDER.comment("Enable/disable alphaver lilypad generation.").define("lilypad_generation", true);
		TREE_GENERATION = BUILDER.comment("Enable/disable alphaver tree generation.").define("alphaver_tree_generation", true);
		BUILDER.pop();
		BUILDER.push("Main Menu");
		CUSTOM_PANORAMA = BUILDER.comment("Enable/disable this mod's custom panoramas.").define("custom_panorama", true);
		BUILDER.pop();
		BUILDER.push("Gui & overlay");
		QUICKLOAD_BUTTON = BUILDER.comment("Enable/disable the quickload button on the main menu (restart required).").define("quickload_button", true);
		ALPHAVER_CONSOLE = BUILDER.comment("Enable/disable 3emj console from alphaver (When this is on all minecraft commads are only executable using this console)").define("alphaver_console", false);
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
		DONT_LOOK_AT_THE_MOON = BUILDER.comment("Enable/disable don't look at the moon event (restart required)").define("dont_look_at_the_moon", false);
		PLACE_THE_SMILE_BLOCK_ANYWHERE = BUILDER.comment("Enable/disable the ClaytonM634 smiling block to be placed anywhere.").define("place_the_smile_block_anywhere", false);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}

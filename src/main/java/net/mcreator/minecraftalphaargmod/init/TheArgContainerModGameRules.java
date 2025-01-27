
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.minecraftalphaargmod.init;

import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.level.GameRules;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TheArgContainerModGameRules {
	public static final GameRules.Key<GameRules.BooleanValue> HUB_ENABLE = GameRules.register("hubEnable", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
	public static final GameRules.Key<GameRules.BooleanValue> SVALLOWRNET = GameRules.register("svallowrnet", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
}

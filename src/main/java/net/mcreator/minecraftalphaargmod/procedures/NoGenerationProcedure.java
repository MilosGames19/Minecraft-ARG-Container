package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.minecraftalphaargmod.network.TheArgContainerModVariables;

public class NoGenerationProcedure {
	public static boolean execute(LevelAccessor world) {
		if (TheArgContainerModVariables.MapVariables.get(world).DesableGen == true) {
			return true;
		}
		return false;
	}
}

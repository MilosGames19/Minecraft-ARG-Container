package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.minecraftalphaargmod.network.TheArgContainerModVariables;

public class Dash5Procedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		if ((entity.getCapability(TheArgContainerModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new TheArgContainerModVariables.PlayerVariables())).guiCooldown == 5) {
			return true;
		}
		return false;
	}
}
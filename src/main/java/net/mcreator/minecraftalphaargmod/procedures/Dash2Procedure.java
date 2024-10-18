package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.minecraftalphaargmod.network.TheArgContainerModVariables;

public class Dash2Procedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		if ((entity.getCapability(TheArgContainerModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new TheArgContainerModVariables.PlayerVariables())).guiCooldown == 2
				&& (entity.getCapability(TheArgContainerModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new TheArgContainerModVariables.PlayerVariables())).Cooldown == 4) {
			{
				double _setval = 3;
				entity.getCapability(TheArgContainerModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.guiCooldown = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			return true;
		}
		return false;
	}
}

package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.minecraftalphaargmod.network.TheArgContainerModVariables;

public class Dash1Procedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		if ((entity.getCapability(TheArgContainerModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new TheArgContainerModVariables.PlayerVariables())).guiCooldown == 1
				&& (entity.getCapability(TheArgContainerModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new TheArgContainerModVariables.PlayerVariables())).Cooldown == 2) {
			{
				double _setval = 2;
				entity.getCapability(TheArgContainerModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.guiCooldown = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			Dash2Procedure.execute(entity);
			return true;
		}
		return false;
	}
}

package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

import net.mcreator.minecraftalphaargmod.network.TheArgContainerModVariables;

public class LogicRightclickedOnBlockProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if ((entity.getCapability(TheArgContainerModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new TheArgContainerModVariables.PlayerVariables())).FpsCap == false) {
			{
				boolean _setval = true;
				entity.getCapability(TheArgContainerModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.FpsCap = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("Shader Code: ON"), false);
		} else {
			{
				boolean _setval = false;
				entity.getCapability(TheArgContainerModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.FpsCap = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("Shader Code: OFF"), false);
		}
	}
}

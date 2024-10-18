package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import net.mcreator.minecraftalphaargmod.world.inventory.WarningMenu;
import net.mcreator.minecraftalphaargmod.network.TheArgContainerModVariables;

public class CloseGUIProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player _plr0 && _plr0.containerMenu instanceof WarningMenu) {
			if (entity instanceof Player _player)
				_player.closeContainer();
			TheArgContainerModVariables.MapVariables.get(world).Warning = true;
			TheArgContainerModVariables.MapVariables.get(world).syncData(world);
		} else {
			if (entity instanceof Player _player)
				_player.closeContainer();
		}
	}
}

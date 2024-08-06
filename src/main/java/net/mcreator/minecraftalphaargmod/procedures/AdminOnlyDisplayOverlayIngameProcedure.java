package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModItems;

public class AdminOnlyDisplayOverlayIngameProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return entity instanceof Player _playerHasItem ? _playerHasItem.getInventory().contains(new ItemStack(MinecraftAlphaArgModModItems.VOID_KEY.get())) : false;
	}
}

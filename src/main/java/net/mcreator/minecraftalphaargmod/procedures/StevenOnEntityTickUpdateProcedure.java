package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.network.chat.Component;

public class StevenOnEntityTickUpdateProcedure {
	public static void execute(LevelAccessor world) {
		if (Math.random() < 0.6) {
			if (!world.isClientSide() && world.getServer() != null)
				world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("<Steven> X"), false);
		} else if (Math.random() < 0.2) {
			if (!world.isClientSide() && world.getServer() != null)
				world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("<Steven> ("), false);
		} else if (Math.random() < 0.1) {
			if (!world.isClientSide() && world.getServer() != null)
				world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("<Steven> )"), false);
		}
	}
}

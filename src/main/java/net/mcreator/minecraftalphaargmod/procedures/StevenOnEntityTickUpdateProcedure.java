package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;

public class StevenOnEntityTickUpdateProcedure {
	public static void execute(LevelAccessor world) {
		if (Mth.nextInt(RandomSource.create(), -100, 100) == 70) {
			if (!world.isClientSide() && world.getServer() != null)
				world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("<Steven> X"), false);
		} else if (Mth.nextInt(RandomSource.create(), -100, 100) == 25) {
			if (!world.isClientSide() && world.getServer() != null)
				world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("<Steven> ("), false);
		} else if (Mth.nextInt(RandomSource.create(), -100, 100) == -12) {
			if (!world.isClientSide() && world.getServer() != null)
				world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("<Steven> )"), false);
		}
	}
}

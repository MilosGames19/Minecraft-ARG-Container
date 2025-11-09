package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraftforge.server.ServerLifecycleHooks;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;
import net.mcreator.minecraftalphaargmod.TheArgContainerMod;

public class CrashProcedure {
	public static void execute(LevelAccessor world) {
		if (McconfigConfiguration.GAME_CRASH.get() == true) {
			TheArgContainerMod.queueServerWork(20, () -> {
				if (!world.isClientSide() && world.getServer() != null)
					ServerLifecycleHooks.getCurrentServer().stopServer();
			});
		}
	}
}

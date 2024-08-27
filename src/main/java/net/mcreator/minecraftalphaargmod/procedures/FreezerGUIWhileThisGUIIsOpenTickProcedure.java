package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;

public class FreezerGUIWhileThisGUIIsOpenTickProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		FreezerRProcedure.execute(world, x, y, z);
		SkyFireFreezerRProcedure.execute(world, x, y, z);
		IceFreezerRProcedure.execute(world, x, y, z);
		SnowBallFreezerRProcedure.execute(world, x, y, z);
		SnowblockFreezerRProcedure.execute(world, x, y, z);
		SnowBricksFreezerRProcedure.execute(world, x, y, z);
	}
}

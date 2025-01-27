package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

public class VoidentityOnEntityTickUpdateProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (world instanceof Level _lvl0 && _lvl0.isDay() && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z)) && entity.getRemainingFireTicks() < 0 && !world.getLevelData().isRaining()) {
			entity.setSecondsOnFire(5);
		}
	}
}

package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.entity.Entity;

public class HeaterEntityCollidesInTheBlockProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		entity.setSecondsOnFire(15);
	}
}

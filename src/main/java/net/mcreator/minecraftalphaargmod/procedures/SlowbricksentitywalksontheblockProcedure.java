package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;

public class SlowbricksentitywalksontheblockProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		entity.setDeltaMovement(new Vec3((entity.getDeltaMovement().x() / 2), (entity.getDeltaMovement().y()), (entity.getDeltaMovement().z() / 2)));
	}
}

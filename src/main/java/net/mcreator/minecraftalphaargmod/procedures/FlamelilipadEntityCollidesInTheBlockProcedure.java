package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class FlamelilipadEntityCollidesInTheBlockProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity.getPersistentData().getDouble("The_ARG_Container_HealTimer") == 0) {
			entity.getPersistentData().putDouble("The_ARG_Container_HealTimer", 20);
		} else {
			entity.getPersistentData().putDouble("The_ARG_Container_HealTimer", (entity.getPersistentData().getDouble("The_ARG_Container_HealTimer") - 1));
		}
		if (entity.getPersistentData().getDouble("The_ARG_Container_HealTimer") == 0) {
			if (entity instanceof LivingEntity _entity)
				_entity.setHealth((float) ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + 1));
		}
	}
}

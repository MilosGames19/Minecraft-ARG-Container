package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;

public class BanTextDisplayOverlayIngameProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		if (ResourceKey.create(Registries.DIMENSION, new ResourceLocation("the_arg_container:ash")) == (entity.level().dimension())) {
			return true;
		}
		return false;
	}
}

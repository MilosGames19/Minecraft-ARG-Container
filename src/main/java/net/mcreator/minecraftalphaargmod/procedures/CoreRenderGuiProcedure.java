package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModEntities;
import net.mcreator.minecraftalphaargmod.entity.COREEntity;

public class CoreRenderGuiProcedure {
	public static Entity execute(LevelAccessor world) {
		return world instanceof Level _level ? new COREEntity(TheArgContainerModEntities.CORE.get(), _level) : null;
	}
}

package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.registries.Registries;

import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;
import net.mcreator.minecraftalphaargmod.TheArgContainerMod;

public class CrashProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (McconfigConfiguration.GAME_CRASH.get() == true) {
			TheArgContainerMod.queueServerWork(20, () -> {
				entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1 / 0));
			});
		}
	}
}

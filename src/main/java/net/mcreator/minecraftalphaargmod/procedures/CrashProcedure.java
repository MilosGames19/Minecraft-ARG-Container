package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.registries.Registries;

import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModGameRules;
import net.mcreator.minecraftalphaargmod.MinecraftAlphaArgModMod;

public class CrashProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (world.getLevelData().getGameRules().getBoolean(MinecraftAlphaArgModModGameRules.ERROR_CRASH) == true) {
			world.getLevelData().getGameRules().getRule(MinecraftAlphaArgModModGameRules.ERROR_CRASH).set(false, world.getServer());
			MinecraftAlphaArgModMod.queueServerWork(10, () -> {
				entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), (float) (1 / 0));
			});
		}
	}
}

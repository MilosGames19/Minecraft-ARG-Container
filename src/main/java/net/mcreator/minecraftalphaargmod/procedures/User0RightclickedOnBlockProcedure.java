package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModMobEffects;

public class User0RightclickedOnBlockProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(MinecraftAlphaArgModModMobEffects.CURSEOF_USER_0.get(), 3100, 1, false, false));
	}
}

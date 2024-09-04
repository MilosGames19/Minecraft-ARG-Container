
package net.mcreator.minecraftalphaargmod.potion;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import net.mcreator.minecraftalphaargmod.procedures.CurseofUser0EffectExpiresProcedure;
import net.mcreator.minecraftalphaargmod.procedures.CurseofUser0ActiveTickConditionProcedure;

public class CurseofUser0MobEffect extends MobEffect {
	public CurseofUser0MobEffect() {
		super(MobEffectCategory.HARMFUL, -1);
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		CurseofUser0ActiveTickConditionProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ());
	}

	@Override
	public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
		super.removeAttributeModifiers(entity, attributeMap, amplifier);
		CurseofUser0EffectExpiresProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ());
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}

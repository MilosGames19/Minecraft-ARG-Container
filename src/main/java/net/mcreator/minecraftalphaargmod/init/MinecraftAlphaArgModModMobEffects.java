
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.minecraftalphaargmod.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.effect.MobEffect;

import net.mcreator.minecraftalphaargmod.potion.CurseofUser0MobEffect;
import net.mcreator.minecraftalphaargmod.MinecraftAlphaArgModMod;

public class MinecraftAlphaArgModModMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MinecraftAlphaArgModMod.MODID);
	public static final RegistryObject<MobEffect> CURSEOF_USER_0 = REGISTRY.register("curseof_user_0", () -> new CurseofUser0MobEffect());
}

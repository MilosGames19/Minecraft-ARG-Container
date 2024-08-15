
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.minecraftalphaargmod.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.level.levelgen.feature.Feature;

import net.mcreator.minecraftalphaargmod.world.features.SaltFeature;
import net.mcreator.minecraftalphaargmod.MinecraftAlphaArgModMod;

@Mod.EventBusSubscriber
public class MinecraftAlphaArgModModFeatures {
	public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.FEATURES, MinecraftAlphaArgModMod.MODID);
	public static final RegistryObject<Feature<?>> SALT = REGISTRY.register("salt", SaltFeature::new);
}


/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.minecraftalphaargmod.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.level.levelgen.feature.Feature;

import net.mcreator.minecraftalphaargmod.world.features.SkyfireFeatureFeature;
import net.mcreator.minecraftalphaargmod.world.features.SaltFeature;
import net.mcreator.minecraftalphaargmod.world.features.FlamelilyFeatureFeature;
import net.mcreator.minecraftalphaargmod.TheArgContainerMod;

@Mod.EventBusSubscriber
public class TheArgContainerModFeatures {
	public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.FEATURES, TheArgContainerMod.MODID);
	public static final RegistryObject<Feature<?>> FLAMELILY_FEATURE = REGISTRY.register("flamelily_feature", FlamelilyFeatureFeature::new);
	public static final RegistryObject<Feature<?>> SKYFIRE_FEATURE = REGISTRY.register("skyfire_feature", SkyfireFeatureFeature::new);
	public static final RegistryObject<Feature<?>> SALT = REGISTRY.register("salt", SaltFeature::new);
}

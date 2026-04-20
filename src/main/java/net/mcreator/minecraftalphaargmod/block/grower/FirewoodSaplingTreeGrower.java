package net.mcreator.minecraftalphaargmod.block.grower;

import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.util.RandomSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.data.worldgen.features.FeatureUtils;

public class FirewoodSaplingTreeGrower extends AbstractTreeGrower {
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource randomSource, boolean hasFlower) {
		if (randomSource.nextFloat() < 0.1) {
			return FeatureUtils.createKey("the_arg_container:big_firewood_tree");
		}
		return FeatureUtils.createKey("the_arg_container:firewood_tree");
	}
}

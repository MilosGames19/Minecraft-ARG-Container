package net.mcreator.minecraftalphaargmod.block.grower;

import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.util.RandomSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.data.worldgen.features.FeatureUtils;

public class AlphaverSaplingTreeGrower extends AbstractTreeGrower {
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource randomSource, boolean hasFlower) {
		if (randomSource.nextFloat() < 0.1) {
			return FeatureUtils.createKey("the_arg_container:big_alphaver_oak_tree");
		}
		return FeatureUtils.createKey("the_arg_container:tree_feature");
	}
}

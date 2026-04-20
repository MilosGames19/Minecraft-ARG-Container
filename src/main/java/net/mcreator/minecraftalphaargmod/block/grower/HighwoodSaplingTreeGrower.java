package net.mcreator.minecraftalphaargmod.block.grower;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;

public class HighwoodSaplingTreeGrower extends AbstractMegaTreeGrower {

    private static final ResourceKey<ConfiguredFeature<?, ?>> HIGHWOOD =
        ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            new ResourceLocation("the_arg_container", "highwood")
        );

    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(
            RandomSource randomSource, boolean hasFlower) {
        return HIGHWOOD;
    }

    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(
            RandomSource randomSource) {
        return HIGHWOOD;
    }
}

package net.mcreator.minecraftalphaargmod.world.features;

import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.RandomPatchFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.WorldGenLevel;

import net.mcreator.minecraftalphaargmod.procedures.FlamelilyFeatureAdditionalGenerationConditionProcedure;

public class FlamelilyFeatureFeature extends RandomPatchFeature {
	public FlamelilyFeatureFeature() {
		super(RandomPatchConfiguration.CODEC);
	}

	public boolean place(FeaturePlaceContext<RandomPatchConfiguration> context) {
		WorldGenLevel world = context.level();
		int x = context.origin().getX();
		int y = context.origin().getY();
		int z = context.origin().getZ();
		if (!FlamelilyFeatureAdditionalGenerationConditionProcedure.execute())
			return false;
		return super.place(context);
	}
}

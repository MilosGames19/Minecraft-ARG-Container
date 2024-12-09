
package net.mcreator.minecraftalphaargmod.world.features;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.WorldGenLevel;

import net.mcreator.minecraftalphaargmod.world.features.configurations.StructureFeatureConfiguration;
import net.mcreator.minecraftalphaargmod.procedures.SkyfireFeatureAdditionalGenerationConditionProcedure;

public class SkyfireFeatureFeature extends StructureFeature {
	public SkyfireFeatureFeature() {
		super(StructureFeatureConfiguration.CODEC);
	}

	public boolean place(FeaturePlaceContext<StructureFeatureConfiguration> context) {
		WorldGenLevel world = context.level();
		int x = context.origin().getX();
		int y = context.origin().getY();
		int z = context.origin().getZ();
		if (!SkyfireFeatureAdditionalGenerationConditionProcedure.execute())
			return false;
		return super.place(context);
	}
}

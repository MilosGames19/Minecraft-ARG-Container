package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

import net.mcreator.minecraftalphaargmod.network.TheArgContainerModVariables;
import net.mcreator.minecraftalphaargmod.init.TheArgContainerModBlocks;

public class MoonfalldimensionPlayerEntersDimensionProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (TheArgContainerModVariables.WorldVariables.get(world).Is_it_placable == 0 && (entity.level().dimension()) == ResourceKey.create(Registries.DIMENSION, new ResourceLocation("the_arg_container:moonfalldimension"))) {
			world.setBlock(BlockPos.containing(x, y - 5, z), TheArgContainerModBlocks.MOONFALL_GENERATOR.get().defaultBlockState(), 3);
			TheArgContainerModVariables.WorldVariables.get(world).Is_it_placable = 1;
			TheArgContainerModVariables.WorldVariables.get(world).syncData(world);
		}
	}
}

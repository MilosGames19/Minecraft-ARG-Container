package net.mcreator.minecraftalphaargmod.world.features;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.mcreator.minecraftalphaargmod.init.TheArgContainerModBlocks;

public class MyconFeatureFeature extends Feature<NoneFeatureConfiguration> {
	public MyconFeatureFeature() {
		super(NoneFeatureConfiguration.CODEC);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel world = context.level();
		BlockPos pos = context.origin();
		RandomSource random = context.random();

		generateMycon(world, pos, random);
		return true;
	}

	private void generateMycon(WorldGenLevel world, BlockPos pos, RandomSource random) {

		BlockState stemState = TheArgContainerModBlocks.MYCON_STEM.get().defaultBlockState();
		for (int i = 0; i < 6; ++i) {
			world.setBlock(pos.above(i), stemState, 3);
		}

		BlockPos capCenter = pos.above(3);
		generateCap(world, capCenter, random);
	}

	private void generateCap(WorldGenLevel world, BlockPos center, RandomSource random) {

		for (int dx = -1; dx <= 1; ++dx) {
			for (int dz = -1; dz <= 1; ++dz) {
				for (int dy = 0; dy <= 1; ++dy) {
					placeCapCluster(world, center.offset(dx, dy, dz));
				}
			}
		}

		for (int i = 0; i < 3; ++i) {
			int dirX = random.nextInt(2) * 2 - 1;
			int dirZ = random.nextInt(2) * 2 - 1;
			int lenX = random.nextInt(2) + 2;
			int lenZ = random.nextInt(2) + 2;

			for (int dx = 0; dx < lenX; ++dx) {
				for (int dz = 0; dz < lenZ; ++dz) {
					placeCapCluster(world, center.offset(dx * dirX, 0, dz * dirZ));
				}
			}
		}

		BlockState glowingCap = TheArgContainerModBlocks.GLOWING_MYCON_CAP.get().defaultBlockState();

		for (int d = -2; d <= 2; ++d) {

			if (world.getBlockState(center.offset(d, 0, 2)).is(glowingCap.getBlock()) && random.nextInt(6) == 0) {
				placeCapCluster(world, center.offset(d, 1, 2));
			}

			if (world.getBlockState(center.offset(d, 0, -2)).is(glowingCap.getBlock()) && random.nextInt(6) == 0) {
				placeCapCluster(world, center.offset(d, 1, -2));
			}

			if (world.getBlockState(center.offset(2, 0, d)).is(glowingCap.getBlock()) && random.nextInt(6) == 0) {
				placeCapCluster(world, center.offset(2, 1, d));
			}

			if (world.getBlockState(center.offset(-2, 0, d)).is(glowingCap.getBlock()) && random.nextInt(6) == 0) {
				placeCapCluster(world, center.offset(-2, 1, d));
			}
		}

		int startX = -random.nextInt(2);
		int startZ = -random.nextInt(2);

		for (int dx = startX; dx < 2 + startX; ++dx) {
			for (int dz = startZ; dz < 2 + startZ; ++dz) {
				placeCapCluster(world, center.offset(dx, 2, dz));
			}
		}
	}

	private void placeCapCluster(WorldGenLevel world, BlockPos pos) {

		world.setBlock(pos, TheArgContainerModBlocks.GLOWING_MYCON_CAP.get().defaultBlockState(), 3);

		BlockState cap = TheArgContainerModBlocks.MYCON_CAP.get().defaultBlockState();

		BlockPos[] neighbors = {pos.west(), pos.north(), pos.east(), pos.south(), pos.above()};

		for (BlockPos n : neighbors) {
			if (world.isEmptyBlock(n)) {
				world.setBlock(n, cap, 3);
			}
		}
	}
}

package net.mcreator.minecraftalphaargmod.world.features;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.Mth;
import net.mcreator.minecraftalphaargmod.init.TheArgContainerModBlocks;

import java.util.Random;

public class HighwoodFeature extends Feature<NoneFeatureConfiguration> {

	public HighwoodFeature() {
		super(NoneFeatureConfiguration.CODEC);
	}

	private static final double LEAF_ANGLE_STEP = 0.2243994752564138;

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel world = context.level();
		BlockPos origin = context.origin();
		RandomSource source = context.random();
		return generateEpicTree(world, source, origin.getX(), origin.getY(), origin.getZ());
	}

	private boolean generateEpicTree(WorldGenLevel world, RandomSource source, int x, int y, int z) {

		BlockState wood = TheArgContainerModBlocks.HIGHWOOD_LOG.get().defaultBlockState();
		BlockState leaves = TheArgContainerModBlocks.HIGHWOOD_LEAVES.get().defaultBlockState().setValue(net.minecraft.world.level.block.LeavesBlock.PERSISTENT, true);
		BlockState roots = TheArgContainerModBlocks.HIGHWOOD_ROOTS.get().defaultBlockState();

		BlockPos below = new BlockPos(x, y - 1, z);
		if (world.isOutsideBuildHeight(below))
			return false;

		BlockState soil = world.getBlockState(below);
		if (!soil.is(Blocks.GRASS_BLOCK) && !soil.is(Blocks.DIRT)) {
			return false;
		}

		long seed = source.nextLong() + (long) x + (long) y + (long) z + world.getSeed();
		Random treeRandom = new Random(seed);
		Random foliageRandom = new RandomSourceAdapter(source);

		int height = 8 + treeRandom.nextInt(8);
		if (y + height + 5 > world.getMaxBuildHeight())
			return false;

		float baseRadius = 2.0f + treeRandom.nextFloat();

		generateSurfaceRoots(world, treeRandom, x, y, z, roots, height, baseRadius);

		for (int dy = 0; dy <= height; dy++) {

			float progress = (float) dy / height;
			float currentRadius = baseRadius * (1.0f - progress * 0.8f);

			int radiusInt = (int) Math.ceil(currentRadius + 1.0f);

			for (int dx = -radiusInt; dx <= radiusInt; dx++) {
				for (int dz = -radiusInt; dz <= radiusInt; dz++) {

					float distSq = dx * dx + dz * dz;
					float noise = treeRandom.nextFloat() * 1.5f;

					if (distSq <= (currentRadius * currentRadius) + noise) {
						BlockPos trunkPos = new BlockPos(x + dx, y + dy, z + dz);
						if (canReplace(world.getBlockState(trunkPos))) {
							world.setBlock(trunkPos, wood, 3);
						}
					}
				}
			}

			if (dy > 4 && dy < height - 3 && treeRandom.nextInt(6) == 0) {
				int bdx = (treeRandom.nextBoolean() ? 1 : -1) * (int) (currentRadius + 1);
				int bdz = (treeRandom.nextBoolean() ? 1 : -1) * (int) (currentRadius + 1);
				connectToTrunk(world, x, y + dy, z, x + bdx, z + bdz, wood);
				generateFoliage(world, foliageRandom, x + bdx, y + dy, z + bdz, leaves, wood);
			}
		}

		generateTrunkFoundation(world, x, y, z, wood, baseRadius);

		int topBranches = 5 + treeRandom.nextInt(4);
		for (int i = 0; i < topBranches; i++) {
			int tx = x + treeRandom.nextInt(3) - 1;
			int tz = z + treeRandom.nextInt(3) - 1;
			int topY = y + height - treeRandom.nextInt(3);
			generateFoliage(world, foliageRandom, tx, topY, tz, leaves, wood);
		}

		return true;
	}

	private void generateTrunkFoundation(WorldGenLevel world, int x, int y, int z, BlockState wood, float baseRadius) {
		int radiusInt = (int) Math.ceil(baseRadius);
		for (int dx = -radiusInt; dx <= radiusInt; dx++) {
			for (int dz = -radiusInt; dz <= radiusInt; dz++) {
				if (dx * dx + dz * dz > baseRadius * baseRadius + 1.0f)
					continue;

				for (int dy = -1; dy >= -64; dy--) {
					BlockPos pos = new BlockPos(x + dx, y + dy, z + dz);
					if (world.isOutsideBuildHeight(pos))
						break;
					BlockState state = world.getBlockState(pos);
					if (state.isAir() || state.canBeReplaced()) {
						world.setBlock(pos, wood, 3);
					} else {
						break;
					}
				}
			}
		}
	}

	private void generateSurfaceRoots(WorldGenLevel world, Random random, int x, int y, int z, BlockState roots, int treeHeight, float baseRadius) {
		int rootCount = Math.min(8, 5 + (treeHeight - 8) / 3);

		for (int i = 0; i < rootCount; i++) {
			float angle = (float) i * ((float) (Math.PI * 2.0) / rootCount) + (random.nextFloat() - 0.5f) * 0.7f;
			float drift = (random.nextFloat() - 0.5f) * 0.25f;
			int length = 5 + random.nextInt(7);

			float rx = x + Mth.cos(angle) * baseRadius;
			float rz = z + Mth.sin(angle) * baseRadius;
			int ry = y - 1;

			for (int s = 0; s < length; s++) {
				angle += drift;
				rx += Mth.cos(angle);
				rz += Mth.sin(angle);

				if (s % 3 == 2 && ry > y - 4) {
					ry--;
				}

				BlockPos rp = new BlockPos(Math.round(rx), ry, Math.round(rz));
				if (world.isOutsideBuildHeight(rp) || world.getBlockState(rp).is(Blocks.BEDROCK))
					break;

				placeRootBlockUnderground(world, rp, roots);

				if (random.nextInt(9) == 0) {
					BlockPos surfacePos = new BlockPos(Math.round(rx), y, Math.round(rz));
					if (!world.isOutsideBuildHeight(surfacePos)) {
						BlockState above = world.getBlockState(surfacePos);
						if (above.is(Blocks.GRASS_BLOCK) || above.is(Blocks.DIRT) || above.is(Blocks.COARSE_DIRT)) {
							world.setBlock(surfacePos, roots, 3);
						}
					}
				}
			}
		}
	}

	private void placeRootBlockUnderground(WorldGenLevel world, BlockPos pos, BlockState roots) {
		BlockState here = world.getBlockState(pos);
		if (here.is(Blocks.GRASS_BLOCK) || here.is(Blocks.DIRT) || here.is(Blocks.COARSE_DIRT) || here.is(Blocks.STONE) || here.is(Blocks.GRAVEL) || here.is(Blocks.SAND) || here.is(Blocks.DIRT_PATH) || here.is(Blocks.ROOTED_DIRT)) {
			world.setBlock(pos, roots, 3);
		}
	}

	private void connectToTrunk(WorldGenLevel world, int x, int y, int z, int bx, int bz, BlockState wood) {
		float dx = bx - x;
		float dz = bz - z;
		int steps = (int) Math.ceil(Math.sqrt(dx * dx + dz * dz));
		for (int s = 1; s <= steps; s++) {
			float t = (float) s / steps;
			BlockPos bp = new BlockPos(Math.round(x + dx * t), y, Math.round(z + dz * t));
			if (!world.isOutsideBuildHeight(bp) && canReplace(world.getBlockState(bp))) {
				world.setBlock(bp, wood, 3);
			}
		}
	}

	private void generateFoliage(WorldGenLevel world, Random random, int x, int y, int z, BlockState leaves, BlockState wood) {
		float angle = random.nextFloat() * (float) Math.PI * 2.0f;
		int steps = random.nextInt(8) + 7;

		float fbx = (float) x;
		float fy = (float) y;
		float fbz = (float) z;

		for (int i = 0; i < steps; i++) {
			angle += (float) ((random.nextFloat() - 0.5) * 0.15);
			float fraction = (float) (i + 1) / (float) steps;
			fbx += Mth.cos(angle) * (1.0f - fraction);
			fy += fraction;
			fbz += Mth.sin(angle) * (1.0f - fraction);

			BlockPos bp = new BlockPos(Math.round(fbx), Math.round(fy), Math.round(fbz));
			if (world.isOutsideBuildHeight(bp))
				continue;

			if (canReplace(world.getBlockState(bp))) {
				world.setBlock(bp, wood, 3);
			}
		}

		int finalBx = Math.round(fbx);
		int finalBz = Math.round(fbz);
		int ly = Math.round(fy) + random.nextInt(2) + 1;
		int rings = random.nextInt(3) + 3;

		for (int ring = 0; ring < rings; ring++) {
			BlockPos center = new BlockPos(finalBx, ly - ring, finalBz);
			if (!world.isOutsideBuildHeight(center) && canReplace(world.getBlockState(center))) {
				world.setBlock(center, leaves, 3);
			}

			for (float a = 0.0f; (double) a < Math.PI * 2.0; a += LEAF_ANGLE_STEP) {
				int spokes = random.nextInt(ring + 2) + ring + 3;
				float lx = (float) finalBx;
				float lz = (float) finalBz;

				for (int s = 0; s < spokes; s++) {
					lz += Mth.sin(a);
					lx += Mth.cos(a);
					BlockPos lp = new BlockPos(Math.round(lx), ly - ring, Math.round(lz));

					if (world.isOutsideBuildHeight(lp))
						continue;

					BlockState ls = world.getBlockState(lp);
					if (!ls.canOcclude() || ls.is(leaves.getBlock())) {
						if (canReplace(ls)) {
							world.setBlock(lp, leaves, 3);
						}
					}
				}
			}
		}
	}

	private boolean canReplace(BlockState state) {
		return state.isAir() || state.canBeReplaced() || state.is(Blocks.WATER) || state.is(Blocks.LAVA) || state.is(Blocks.SNOW) || state.is(Blocks.VINE);
	}

	private static final class RandomSourceAdapter extends java.util.Random {
		private final RandomSource src;

		RandomSourceAdapter(RandomSource src) {
			super(0);
			this.src = src;
		}

		@Override
		public int nextInt(int bound) {
			return src.nextInt(bound);
		}

		@Override
		public float nextFloat() {
			return src.nextFloat();
		}

		@Override
		public long nextLong() {
			return src.nextLong();
		}

		@Override
		public double nextDouble() {
			return src.nextDouble();
		}

		@Override
		public boolean nextBoolean() {
			return src.nextBoolean();
		}
	}
}
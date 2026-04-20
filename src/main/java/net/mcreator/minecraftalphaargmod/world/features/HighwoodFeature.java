package net.mcreator.minecraftalphaargmod.world.features;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.mcreator.minecraftalphaargmod.init.TheArgContainerModBlocks;

import java.util.ArrayList;
import java.util.List;
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
		BlockState soil = world.getBlockState(below);
		if (!soil.is(Blocks.GRASS_BLOCK) && !soil.is(Blocks.DIRT)) {
			return true;

		}

		long seed = source.nextLong() + (long) x + (long) y + (long) z + world.getSeed();
		Random treeRandom = new Random(seed);
		Random foliageRandom = new RandomSourceAdapter(source);

		int height = 7 + treeRandom.nextInt(10);

		generateSurfaceRoots(world, treeRandom, x, y, z, roots, height);

		placeThickBase(world, x, y, z, wood, Math.min(3, height));

		List<Vec3> points = new ArrayList<>();
		points.add(new Vec3(x, y + height, z));

		int iterations = 0;

		while (!points.isEmpty()) {
			List<Vec3> snapshot = new ArrayList<>(points);

			for (Vec3 point : snapshot) {
				int px = (int) Math.round(point.x);
				int py = (int) Math.round(point.y);
				int pz = (int) Math.round(point.z);
				BlockPos pPos = new BlockPos(px, py, pz);

				BlockState cur = world.getBlockState(pPos);
				boolean isSolid = cur.canOcclude();
				boolean isLeaves = cur.is(leaves.getBlock());

				if ((!isSolid || isLeaves) && py >= 0 && (treeRandom.nextInt(3) != 0 || points.size() <= 3)) {

					if (canReplace(cur)) {
						world.setBlock(pPos, wood, 3);
					}

					int branches = 0;
					while (treeRandom.nextInt(points.size() / 30 + 2) <= 1 && points.size() <= 10000) {
						branches++;
						if (branches >= 4)
							break;
						points.add(point.add(treeRandom.nextInt(3) - 1, -1.0, treeRandom.nextInt(3) - 1));
					}

					if (iterations > 2 && treeRandom.nextInt(Math.max(40, 78 - iterations)) == 3) {
						generateFoliage(world, foliageRandom, px, py, pz, leaves, wood);
					}

					points.remove(point);
					points.add(point.add(0, -1.0, 0));

				} else {
					points.remove(point);

					if (cur.is(Blocks.GRASS_BLOCK) || cur.is(Blocks.DIRT)) {
						generateRoots(world, treeRandom, px, py, pz, roots);
					}
				}
			}
			iterations++;
		}

		return true;
	}

	private void placeThickBase(WorldGenLevel world, int x, int y, int z, BlockState wood, int baseHeight) {
		int[][] offsets = {{0, 0}, {1, 0}, {0, 1}, {1, 1}};
		for (int dy = 0; dy < baseHeight; dy++) {
			for (int[] off : offsets) {
				BlockPos bp = new BlockPos(x + off[0], y + dy, z + off[1]);
				if (canReplace(world.getBlockState(bp))) {
					world.setBlock(bp, wood, 3);
				}
			}
		}
	}

	private void generateSurfaceRoots(WorldGenLevel world, Random random, int x, int y, int z, BlockState roots, int treeHeight) {

		int rootCount = Math.min(7, 3 + (treeHeight - 7) / 4);

		for (int i = 0; i < rootCount; i++) {

			int length = random.nextInt(5) + 3;

			float angle = (float) (random.nextInt(8)) * (float) (Math.PI / 4.0);

			float drift = (random.nextFloat() - 0.5f) * 0.4f;

			float rx = x;
			float rz = z;
			int ry = y;

			for (int s = 0; s < length; s++) {
				angle += drift;
				rx += Mth.cos(angle);
				rz += Mth.sin(angle);

				if (s % 2 == 1)
					ry--;

				BlockPos rp = new BlockPos(Math.round(rx), ry, Math.round(rz));
				placeRootBlock(world, rp, roots);

				if (s == length - 1) {
					int dive = random.nextInt(3) + 1;
					for (int d = 1; d <= dive; d++) {
						BlockPos dp = new BlockPos(Math.round(rx), ry - d, Math.round(rz));
						if (world.getBlockState(dp).is(Blocks.BEDROCK))
							break;
						world.setBlock(dp, roots, 3);
					}
				}
			}
		}
	}

	private void placeRootBlock(WorldGenLevel world, BlockPos pos, BlockState roots) {
		BlockState here = world.getBlockState(pos);

		if (here.is(Blocks.GRASS_BLOCK) || here.is(Blocks.DIRT) || here.is(Blocks.COARSE_DIRT) || here.isAir() || here.is(Blocks.SNOW) || here.canBeReplaced()) {
			world.setBlock(pos, roots, 3);
		}
	}

	private void generateFoliage(WorldGenLevel world, Random random, int x, int y, int z, BlockState leaves, BlockState wood) {

		float angle = random.nextFloat() * (float) Math.PI * 2.0f;

		int steps = random.nextInt(8) + 7;
		float fy = (float) y;

		int bx = x;
		int bz = z;

		for (int i = 0; i < steps; i++) {

			angle += (float) ((random.nextFloat() - 0.5) * 0.15);
			float fraction = (float) (i + 1) / (float) steps;
			bx = (int) ((float) bx + Mth.cos(angle) * (1.0f - fraction));
			fy += fraction;
			bz = (int) ((float) bz + Mth.sin(angle) * (1.0f - fraction));
			int by = Math.round(fy);

			BlockPos bp = new BlockPos(bx, by, bz);
			if (canReplace(world.getBlockState(bp))) {
				world.setBlock(bp, wood, 3);
			}
		}

		int ly = Math.round(fy) + random.nextInt(2) + 1;

		int rings = random.nextInt(3) + 3;

		for (int ring = 0; ring < rings; ring++) {
			BlockPos center = new BlockPos(bx, ly - ring, bz);
			if (canReplace(world.getBlockState(center))) {
				world.setBlock(center, leaves, 3);
			}

			for (float a = 0.0f; (double) a < Math.PI * 2.0; a += LEAF_ANGLE_STEP) {

				int spokes = random.nextInt(ring + 2) + ring + 3;
				float lx = (float) bx;
				float lz = (float) bz;

				for (int s = 0; s < spokes; s++) {
					lz += Mth.sin(a);
					lx += Mth.cos(a);
					BlockPos lp = new BlockPos(Math.round(lx), ly - ring, Math.round(lz));
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

	private void generateRoots(WorldGenLevel world, Random random, int x, int y, int z, BlockState roots) {
		int count = random.nextInt(3);

		for (int i = 0; i < count; i++) {
			int len = random.nextInt(4) + 2;

			int rx = x;
			int ry = y;
			int rz = z;

			for (int s = 0; s < len; s++) {
				rx += random.nextInt(3) - 1;
				ry--;
				rz += random.nextInt(3) - 1;
				BlockPos rp = new BlockPos(rx, ry, rz);
				if (!world.getBlockState(rp).is(Blocks.BEDROCK)) {
					world.setBlock(rp, roots, 3);
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
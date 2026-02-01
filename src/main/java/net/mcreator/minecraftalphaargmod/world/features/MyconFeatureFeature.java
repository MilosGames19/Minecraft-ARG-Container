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
        // Stem generation (Old code: Y=4 to Y=9)
        // We assume pos is at the base of the stem (Old Y=4)
        BlockState stemState = TheArgContainerModBlocks.MYCON_STEM.get().defaultBlockState();
        for (int i = 0; i < 6; ++i) {
            world.setBlock(pos.above(i), stemState, 3);
        }

        // Cap generation starts at Old Y=7, which is pos.above(3)
        BlockPos capCenter = pos.above(3);
        generateCap(world, capCenter, random);
    }

    private void generateCap(WorldGenLevel world, BlockPos center, RandomSource random) {
        // Loop 1: 3x3 area, 2 blocks high (Old Y=7 and Y=8 -> Relative 0 and 1)
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                for (int dy = 0; dy <= 1; ++dy) {
                    placeCapCluster(world, center.offset(dx, dy, dz));
                }
            }
        }

        // Loop 2: Random extensions at base level (Old Y=7 -> Relative 0)
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

        // Loop 3: Hanging/Thickening parts (Check Old Y=7, Place Old Y=8 -> Check Rel 0, Place Rel 1)
        BlockState glowingCap = TheArgContainerModBlocks.GLOWING_MYCON_CAP.get().defaultBlockState();

        for (int d = -2; d <= 2; ++d) {
            // Z + 2
            if (world.getBlockState(center.offset(d, 0, 2)).is(glowingCap.getBlock()) && random.nextInt(6) == 0) {
                placeCapCluster(world, center.offset(d, 1, 2));
            }
            // Z - 2
            if (world.getBlockState(center.offset(d, 0, -2)).is(glowingCap.getBlock()) && random.nextInt(6) == 0) {
                placeCapCluster(world, center.offset(d, 1, -2));
            }
            // X + 2
            if (world.getBlockState(center.offset(2, 0, d)).is(glowingCap.getBlock()) && random.nextInt(6) == 0) {
                placeCapCluster(world, center.offset(2, 1, d));
            }
            // X - 2
            if (world.getBlockState(center.offset(-2, 0, d)).is(glowingCap.getBlock()) && random.nextInt(6) == 0) {
                placeCapCluster(world, center.offset(-2, 1, d));
            }
        }

        // Loop 4: Top crown (Old Y=9 -> Relative 2)
        int startX = -random.nextInt(2);
        int startZ = -random.nextInt(2);

        for (int dx = startX; dx < 2 + startX; ++dx) {
            for (int dz = startZ; dz < 2 + startZ; ++dz) {
                placeCapCluster(world, center.offset(dx, 2, dz));
            }
        }
    }

    // Corresponds to func_9990
    private void placeCapCluster(WorldGenLevel world, BlockPos pos) {
        // Center is Glowing Mycon Cap
        world.setBlock(pos, TheArgContainerModBlocks.GLOWING_MYCON_CAP.get().defaultBlockState(), 3);

        BlockState cap = TheArgContainerModBlocks.MYCON_CAP.get().defaultBlockState();

        // Neighbors: West, North, East, South, Above
        BlockPos[] neighbors = {
            pos.west(),
            pos.north(),
            pos.east(),
            pos.south(),
            pos.above()
        };

        for (BlockPos n : neighbors) {
            if (world.isEmptyBlock(n)) {
                world.setBlock(n, cap, 3);
            }
        }
    }
}

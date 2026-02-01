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

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();

        return generateDeEpicTree(world, random, pos.getX(), pos.getY(), pos.getZ());
    }

    private boolean generateDeEpicTree(WorldGenLevel world, RandomSource sourceRandom, int x, int y, int z) {
        // Use java.util.Random to match the original mod's generation logic 1:1
        long seed = sourceRandom.nextLong() + (long)x + (long)y + (long)z + world.getSeed();
        Random treeRandom = new Random(seed);
        
        // Foliage uses a different random sequence in the original code (passed from generate)
        Random foliageRandom = new Random(sourceRandom.nextLong());

        BlockState wood = TheArgContainerModBlocks.HIGHWOOD_LOG.get().defaultBlockState();
        BlockState leaves = TheArgContainerModBlocks.HIGHWOOD_LEAVES.get().defaultBlockState().setValue(net.minecraft.world.level.block.LeavesBlock.PERSISTENT, true);
        BlockState roots = TheArgContainerModBlocks.HIGHWOOD_ROOTS.get().defaultBlockState();

        // Check soil
        BlockPos below = new BlockPos(x, y - 1, z);
        if (!world.getBlockState(below).is(Blocks.GRASS_BLOCK) && !world.getBlockState(below).is(Blocks.DIRT) && !world.getBlockState(below).is(Blocks.SNOW_BLOCK)) {
            return false;
        }

        List<Vec3> points = new ArrayList<>();
        // Increased height range to make it generate tall
        int height = 32 + treeRandom.nextInt(52); // Was 16 + nextInt(26)
        points.add(new Vec3(x, y + height, z));

        int iterations = 0;
        while (!points.isEmpty()) {
            List<Vec3> currentPoints = new ArrayList<>(points);
            
            for (Vec3 point : currentPoints) {
                int px = (int) Math.round(point.x);
                int py = (int) Math.round(point.y);
                int pz = (int) Math.round(point.z);
                BlockPos pPos = new BlockPos(px, py, pz);

                BlockState currentState = world.getBlockState(pPos);
                boolean isSolid = currentState.canOcclude();
                boolean isLeaves = currentState.is(leaves.getBlock());

                // Reverted py >= y check to allow branches to hang down as per original design
                if ((!isSolid || isLeaves) && py >= 0 && (treeRandom.nextInt(3) != 0 || points.size() <= 3)) {
                    if (canReplace(currentState)) {
                        world.setBlock(pPos, wood, 3);
                    }
                    
                    int branches = 0;
                    while (treeRandom.nextInt(points.size() / 20 + 2) <= 1 && points.size() <= 10000) {
                        branches++;
                        if (branches >= 4) break;
                        
                        Vec3 newBranch = point.add(treeRandom.nextInt(3) - 1, -1.0, treeRandom.nextInt(3) - 1);
                        points.add(newBranch);
                    }

                    if (iterations > 2 && treeRandom.nextInt(Math.max(50, 89 - iterations)) == 3) {
                        generateDeEpicTreeFoliage(world, foliageRandom, px, py, pz, leaves, wood);
                    }

                    points.remove(point);
                    points.add(point.add(0, -1.0, 0));
                } else {
                    points.remove(point);
                    if (currentState.is(Blocks.DIRT) || currentState.is(Blocks.GRASS_BLOCK) || currentState.is(Blocks.SNOW_BLOCK)) {
                        generateDeEpicRoots(world, treeRandom, px, py, pz, roots);
                    }
                }
            }
            iterations++;
        }

        return true;
    }

    private void generateDeEpicTreeFoliage(WorldGenLevel world, Random random, int x, int y, int z, BlockState leaves, BlockState wood) {
        float var8 = 0.0F;
        float var9 = random.nextFloat() * (float)Math.PI * 2.0F;
        int var10 = random.nextInt(9) + 8;
        float var11 = (float)y;

        for(int var13 = 0; var13 < var10; ++var13) {
            var9 = (float)((double)var9 + ((double)random.nextFloat() - 0.5D) * 0.1D);
            ++var8;
            float var12 = var8 / (float)var10;
            int nx = (int)((float)x + Mth.cos(var9) * (1.0F - var12));
            var11 += var12;
            int nz = (int)((float)z + Mth.sin(var9) * (1.0F - var12));
            int ny = Math.round(var11);
            BlockPos branchPos = new BlockPos(nx, ny, nz);
            if (canReplace(world.getBlockState(branchPos))) {
                world.setBlock(branchPos, wood, 3);
            }
        }

        y += random.nextInt(2) + 1;
        int var13 = random.nextInt(3) + 2;

        for(int var14 = 0; var14 < var13; ++var14) {
            BlockPos centerPos = new BlockPos(x, y, z);
            if (canReplace(world.getBlockState(centerPos))) {
                world.setBlock(centerPos, leaves, 3);
            }

            for(float var15 = 0.0F; (double)var15 < Math.PI * 2.0D; var15 = (float)((double)var15 + 0.2243994752564138D)) {
                int var16 = random.nextInt(var14 + 4) + var14 + 4;
                float var17 = (float)x;
                float var12 = (float)z;

                for(int var18 = 0; var18 < var16; ++var18) {
                    var12 += Mth.sin(var15);
                    var17 += Mth.cos(var15);
                    BlockPos leafPos = new BlockPos(Math.round(var17), y - var14, Math.round(var12));
                    if(!world.getBlockState(leafPos).canOcclude() || world.getBlockState(leafPos).is(leaves.getBlock())) {
                        if (canReplace(world.getBlockState(leafPos))) {
                            world.setBlock(leafPos, leaves, 3);
                        }
                    }
                }
            }
        }
    }

    private void generateDeEpicRoots(WorldGenLevel world, Random random, int x, int y, int z, BlockState roots) {
        int var7 = random.nextInt(4);

        for(int var8 = 0; var8 < var7; ++var8) {
            int var9 = random.nextInt(8) + 3;
            int var10 = x;
            int var11 = y;
            int var12 = z;

            for(int var13 = 0; var13 < var9; ++var13) {
                var10 += random.nextInt(3) - 1;
                --var11;
                var12 += random.nextInt(3) - 1;
                BlockPos rootPos = new BlockPos(var10, var11, var12);
                if(!world.getBlockState(rootPos).is(Blocks.BEDROCK)) {
                    // Allow roots to replace ground blocks
                    if (canReplaceRoot(world.getBlockState(rootPos))) {
                        world.setBlock(rootPos, roots, 3);
                    }
                }
            }
        }
    }

    private boolean canReplace(BlockState state) {
        return state.isAir() || state.canBeReplaced() || state.is(Blocks.WATER) || state.is(Blocks.LAVA) || state.is(Blocks.SNOW) || state.is(Blocks.VINE);
    }

    private boolean canReplaceRoot(BlockState state) {
        return canReplace(state) || state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.STONE) || state.is(Blocks.GRAVEL) || state.is(Blocks.SAND) || state.is(Blocks.DEEPSLATE);
    }
}

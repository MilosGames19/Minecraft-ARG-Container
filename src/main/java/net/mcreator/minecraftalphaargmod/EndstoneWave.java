package net.mcreator.minecraftalphaargmod.gaze;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = MoonGazeServerListener.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EndstoneWave {
    private static final int DEFAULT_VISITS_PER_TICK = 20_000;
    private static final List<Task> TASKS = new ArrayList<>();

    /**
     * The blacklist – add any blocks here that you don't want converted.
     * Example: Bedrock, Water, Lava, etc.
     */
    public static final Set<String> BLOCK_BLACKLIST = new HashSet<>(Arrays.asList(
            "minecraft:bedrock",
            "minecraft:water",
            "minecraft:lava",
            "minecraft:end_portal",
            "minecraft:end_gateway",
            "minecraft:nether_portal",
            "the_arg_container:celestial_flame",
            "the_arg_container:water_lily"
    ));

    private EndstoneWave() {}

    public static void start(ServerPlayer player, int radius, Runnable onComplete) {
        ServerLevel level = player.serverLevel();
        BlockPos center = player.blockPosition();
        TASKS.add(new Task(level, center, radius, DEFAULT_VISITS_PER_TICK, onComplete));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || TASKS.isEmpty()) return;

        Iterator<Task> it = TASKS.iterator();
        while (it.hasNext()) {
            Task t = it.next();
            if (t.isInvalid()) {
                it.remove();
                continue;
            }
            t.tick();
            if (t.isDone()) {
                it.remove();
            }
        }
    }

    private static final class Task {
        private final ServerLevel level;
        private final int cx, cy, cz;
        private final int radius;
        private final long r2;
        private final int visitsPerTick;
        private final int minY;
        private final int maxY;
        private final BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        private final Runnable onComplete;

        private int dx, dy, dz;
        private boolean done;

        Task(ServerLevel level, BlockPos center, int radius, int visitsPerTick, Runnable onComplete) {
            this.level = level;
            this.cx = center.getX();
            this.cy = center.getY();
            this.cz = center.getZ();
            this.radius = radius;
            this.r2 = (long) radius * (long) radius;
            this.visitsPerTick = Math.max(1_000, visitsPerTick);
            this.minY = level.getMinBuildHeight();
            this.maxY = level.getMaxBuildHeight() - 1;
            this.dx = -radius;
            this.dy = -radius;
            this.dz = -radius;
            this.onComplete = onComplete;
        }

        void tick() {
            if (done) return;

            int visited = 0;
            while (visited < visitsPerTick && !done) {
                int x = cx + dx;
                int y = cy + dy;
                int z = cz + dz;

                if (y >= minY && y <= maxY) {
                    long dist2 = (long) dx * dx + (long) dy * dy + (long) dz * dz;
                    if (dist2 <= r2) {
                        mpos.set(x, y, z);

                        // Skip air
                        if (!level.isEmptyBlock(mpos)) {
                            Block currentBlock = level.getBlockState(mpos).getBlock();
                            FluidState fluid = level.getFluidState(mpos);

                            // Skip if in blacklist or is any fluid
                            if (!BLOCK_BLACKLIST.contains(currentBlock)
                                    && fluid.getType() == Fluids.EMPTY) {
                                level.setBlockAndUpdate(mpos, Blocks.END_STONE.defaultBlockState());
                            }
                        }
                    }
                }

                visited++;
                advance();
            }

            if (done && onComplete != null) {
                level.getServer().execute(onComplete);
            }
        }

        private void advance() {
            dz++;
            if (dz > radius) {
                dz = -radius;
                dy++;
                if (dy > radius) {
                    dy = -radius;
                    dx++;
                    if (dx > radius) {
                        done = true;
                    }
                }
            }
        }

        boolean isDone() { return done; }
        boolean isInvalid() { return level == null || !level.getServer().isRunning(); }
    }
}

package net.mcreator.minecraftalphaargmod.gaze;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EndstoneWave {
    // Increased throughput due to optimizations
    private static final int DEFAULT_VISITS_PER_TICK = 50_000;
    private static final List<Task> TASKS = new ArrayList<>();

    // Cache the blacklist as Block objects for fast lookup
    private static Set<Block> CACHED_BLACKLIST = null;
    
    // Raw strings for the blacklist
    private static final Set<String> BLACKLIST_IDS = new HashSet<>(Arrays.asList(
            "minecraft:bedrock",
            "minecraft:water",
            "minecraft:lava",
            "minecraft:end_portal",
            "minecraft:end_gateway",
            "minecraft:nether_portal",
            "minecraft:barrier",
            "minecraft:command_block",
            "minecraft:repeating_command_block",
            "minecraft:chain_command_block",
            "minecraft:structure_void",
            "minecraft:light",
            "minecraft:jigsaw",
            "minecraft:structure_block",
            "the_arg_container:celestial_flame",
            "the_arg_container:water_lily"
    ));

    private EndstoneWave() {}

    private static Set<Block> getBlacklist() {
        if (CACHED_BLACKLIST == null) {
            CACHED_BLACKLIST = new HashSet<>();
            for (String id : BLACKLIST_IDS) {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id));
                if (block != null && block != Blocks.AIR) {
                    CACHED_BLACKLIST.add(block);
                }
            }
        }
        return CACHED_BLACKLIST;
    }

    public static void start(ServerPlayer player, int radius, Runnable onComplete) {
        ServerLevel level = player.serverLevel();
        BlockPos center = player.blockPosition();
        // User requested "much bigger" radius. Multiplying by 3 to significantly increase the area.
        TASKS.add(new Task(level, center, radius * 3, DEFAULT_VISITS_PER_TICK, onComplete));
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
        private final Set<Block> blacklist;

        private int dx, dy, dz; // dz is now used as the vertical iterator (y-offset)
        private boolean done;
        
        // Chunk caching
        private LevelChunk cachedChunk;
        private int lastChunkX = Integer.MIN_VALUE;
        private int lastChunkZ = Integer.MIN_VALUE;
        
        // Track chunks we've already cleaned to avoid repeated entity searches
        private final Set<Long> cleanedChunks = new HashSet<>();

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
            this.dy = -radius; // dy is now the Z-iterator
            this.dz = maxY; // dz is now the Y-iterator, starting from top
            this.onComplete = onComplete;
            this.blacklist = getBlacklist();
        }

        void tick() {
            if (done) return;

            int visited = 0;
            Block endStone = Blocks.END_STONE;
            BlockState endStoneState = endStone.defaultBlockState();
            
            // We iterate columns (dx, dy) where dx=x-offset, dy=z-offset
            // Inside each column, we scan from top to bottom to find surface
            
            while (visited < visitsPerTick && !done) {
                // Check if column is within radius
                long distSq2D = (long)dx * dx + (long)dy * dy;
                if (distSq2D > r2) {
                    advanceColumn();
                    continue;
                }

                int x = cx + dx;
                int z = cz + dy; // dy is used as z-offset
                
                // Optimized Chunk Access
                int chunkX = x >> 4;
                int chunkZ = z >> 4;
                if (chunkX != lastChunkX || chunkZ != lastChunkZ) {
                    if (level.hasChunk(chunkX, chunkZ)) {
                        cachedChunk = level.getChunk(chunkX, chunkZ);
                        long chunkKey = ChunkPos.asLong(chunkX, chunkZ);
                        if (cleanedChunks.add(chunkKey)) {
                            cleanupItems(chunkX, chunkZ);
                        }
                    } else {
                        cachedChunk = null;
                    }
                    lastChunkX = chunkX;
                    lastChunkZ = chunkZ;
                }

                if (cachedChunk != null) {
                    // Find surface and convert depth
                    // We scan from maxY down to minY
                    // To optimize, we don't scan the whole world height every tick.
                    // Instead, we process one column fully in one go (it's fast enough)
                    // or we process a segment.
                    // Given the request, it's better to process the whole column at once
                    // because finding the surface requires scanning down.
                    
                    processColumn(x, z, cachedChunk, endStone, endStoneState);
                }

                visited++; // Count one column as one "visit" roughly, or we can count blocks.
                // Since we process a whole column, let's count it as 1 unit of work for the loop,
                // but maybe increase cost if it's heavy.
                // Actually, scanning air is fast.
                
                advanceColumn();
            }

            if (done && onComplete != null) {
                level.getServer().execute(onComplete);
            }
        }

        private void processColumn(int x, int z, LevelChunk chunk, Block endStone, BlockState endStoneState) {
            int surfaceY = chunk.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, x & 15, z & 15);
            
            // Calculate organic depth using noise
            // Simple pseudo-noise based on coordinates
            int noise = (int)(Mth.sin(x * 0.3f) * Mth.cos(z * 0.3f) * 2 + Mth.sin(x * 0.7f + z * 0.7f) * 1.5f);
            int depth = 5 + noise; // Base depth 5, varying by +/- ~3
            if (depth < 2) depth = 2;

            int blocksConverted = 0;
            
            // Start from surface and go down
            for (int y = surfaceY; y >= minY; y--) {
                mpos.set(x, y, z);
                BlockState state = chunk.getBlockState(mpos);
                
                if (state.isAir()) continue;
                
                // Stop if we hit something we shouldn't convert (like bedrock) or if we reached depth limit
                if (blacklist.contains(state.getBlock())) {
                    // If we hit a blacklisted block (like water/bedrock), we stop this column
                    break;
                }

                if (state.getBlock() != endStone) {
                    FluidState fluid = state.getFluidState();
                    if (fluid.isEmpty()) {
                        level.setBlock(mpos, endStoneState, 2);
                    }
                }
                
                blocksConverted++;
                if (blocksConverted >= depth) {
                    break;
                }
            }
        }

        private void cleanupItems(int chunkX, int chunkZ) {
            AABB chunkBox = new AABB(chunkX * 16, minY, chunkZ * 16, chunkX * 16 + 16, maxY, chunkZ * 16 + 16);
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, chunkBox);
            for (ItemEntity item : items) {
                item.discard();
            }
        }

        private void advanceColumn() {
            // We iterate dx (x-offset) and dy (z-offset)
            dx++;
            if (dx > radius) {
                dx = -radius;
                dy++;
                if (dy > radius) {
                    done = true;
                }
            }
        }

        boolean isDone() { return done; }
        boolean isInvalid() { return level == null || !level.getServer().isRunning(); }
    }
}

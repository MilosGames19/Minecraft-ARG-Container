package net.mcreator.minecraftalphaargmod.gaze;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EndstoneWave {

	private static final int COLUMNS_PER_TICK = 2_000;
	private static final int MAX_CONCURRENT_TASKS = 3;
	private static final int DEPTH_BASE = 5;
	private static final int DEPTH_VARIANCE = 3;

	private static final List<Task> TASKS = new ArrayList<>();
	private static Set<Block> CACHED_BLACKLIST = null;

	private static final Set<String> BLACKLIST_IDS = new HashSet<>(
			Arrays.asList(
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

	private EndstoneWave() {
	}

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
		if (TASKS.size() >= MAX_CONCURRENT_TASKS)
			return;
		ServerLevel level = player.serverLevel();
		BlockPos center = player.blockPosition();
		TASKS.add(new Task(level, center, radius, COLUMNS_PER_TICK, onComplete));
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || TASKS.isEmpty())
			return;

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
		private final int cx, cz;
		private final int radius;
		private final long r2;
		private final int columnsPerTick;
		private final int minY;
		private final Runnable onComplete;
		private final Set<Block> blacklist;
		private final BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

		private int ox, oz;
		private boolean done;

		private LevelChunk cachedChunk;
		private int lastChunkX = Integer.MIN_VALUE;
		private int lastChunkZ = Integer.MIN_VALUE;
		private final Set<Long> itemCleanedChunks = new HashSet<>();

		Task(ServerLevel level, BlockPos center, int radius, int columnsPerTick, Runnable onComplete) {
			this.level = level;
			this.cx = center.getX();
			this.cz = center.getZ();
			this.radius = radius;
			this.r2 = (long) radius * radius;
			this.columnsPerTick = Math.max(500, columnsPerTick);
			this.minY = level.getMinBuildHeight();
			this.onComplete = onComplete;
			this.blacklist = getBlacklist();
			this.ox = -radius;
			this.oz = -radius;
		}

		void tick() {
			if (done)
				return;

			BlockState endStoneState = Blocks.END_STONE.defaultBlockState();
			int processed = 0;

			while (processed < columnsPerTick && !done) {
				long distSq = (long) ox * ox + (long) oz * oz;

				if (distSq <= r2) {
					int x = cx + ox;
					int z = cz + oz;

					int chunkX = x >> 4;
					int chunkZ = z >> 4;

					if (chunkX != lastChunkX || chunkZ != lastChunkZ) {
						cachedChunk = level.hasChunk(chunkX, chunkZ) ? level.getChunk(chunkX, chunkZ) : null;
						lastChunkX = chunkX;
						lastChunkZ = chunkZ;

						if (cachedChunk != null) {
							long key = ChunkPos.asLong(chunkX, chunkZ);
							if (itemCleanedChunks.add(key)) {
								removeItemEntities(chunkX, chunkZ);
							}
						}
					}

					if (cachedChunk != null) {
						processColumn(x, z, endStoneState);
					}

					processed++;
				}

				advance();
			}

			if (done && onComplete != null) {
				level.getServer().execute(onComplete);
			}
		}

		private void processColumn(int x, int z, BlockState endStoneState) {
			int surfaceY = cachedChunk.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, x & 15, z & 15);

			int depth = DEPTH_BASE + depthNoise(x, z);

			int converted = 0;
			for (int y = surfaceY; y >= minY && converted < depth; y--) {
				mpos.set(x, y, z);
				BlockState state = cachedChunk.getBlockState(mpos);

				if (state.isAir())
					continue;
				if (blacklist.contains(state.getBlock()))
					break;
				if (state.getFluidState().isEmpty() && state.getBlock() != Blocks.END_STONE) {
					level.setBlock(mpos, endStoneState, 2);
				}
				converted++;
			}
		}

		private static int depthNoise(int x, int z) {
			int h = x * 374761393 + z * 668265263;
			h = (h ^ (h >> 13)) * 1274126177;
			h = h ^ (h >> 16);
			return Math.abs(h % (DEPTH_VARIANCE * 2 + 1)) - DEPTH_VARIANCE;
		}

		private void removeItemEntities(int chunkX, int chunkZ) {
			AABB box = new AABB(chunkX * 16, minY, chunkZ * 16, chunkX * 16 + 16, level.getMaxBuildHeight(), chunkZ * 16 + 16);
			for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, box)) {
				item.discard();
			}
		}

		private void advance() {
			ox++;
			if (ox > radius) {
				ox = -radius;
				oz++;
				if (oz > radius) {
					done = true;
				}
			}
		}

		boolean isDone() {
			return done;
		}

		boolean isInvalid() {
			return !level.getServer().isRunning();
		}
	}
}
package net.mcreator.minecraftalphaargmod.minesweeper;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class MinesweeperBoard {
    private final Level level;
    private final BlockPos origin;
    private final int width;
    private final int height;
    private final int mines;
    private final boolean[][] mineMap;
    private final boolean[][] revealed;
    private final int[][] adjacent;
    private final Map<BlockPos, BlockState> terrainSnapshot = new HashMap<>();
    private boolean gameOver = false;
    private boolean won = false;
    private int revealedCount = 0;
    private boolean initialized = false;
    private int flagCount = 0;
    private long startTime = -1;
    private long endTime = -1;
    private long gameOverTime = -1;

    public MinesweeperBoard(Level level, BlockPos origin, int width, int height, int mines) {
        this.level = level;
        this.origin = origin;
        this.width = width;
        this.height = height;
        this.mines = mines;
        this.mineMap = new boolean[width][height];
        this.revealed = new boolean[width][height];
        this.adjacent = new int[width][height];
        saveTerrainSnapshot();
    }

    private void saveTerrainSnapshot() {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                BlockPos pos = origin.offset(x, 0, z);
                terrainSnapshot.put(pos, level.getBlockState(pos));
            }
        }
    }

    public void generate(BlockPos firstClick) {
        if (initialized) return;
        initialized = true;
        startTime = System.currentTimeMillis();
        
        Random rand = new Random();
        Set<BlockPos> excluded = new HashSet<>();
        excluded.add(firstClick);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                excluded.add(firstClick.offset(dx, 0, dz));
            }
        }

        int placed = 0;
        while (placed < mines) {
            int x = rand.nextInt(width);
            int z = rand.nextInt(height);
            BlockPos pos = origin.offset(x, 0, z);
            if (!mineMap[x][z] && !excluded.contains(pos)) {
                mineMap[x][z] = true;
                placed++;
            }
        }

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                if (!mineMap[x][z]) {
                    adjacent[x][z] = countAdjacentMines(x, z);
                }
            }
        }
    }

    private int countAdjacentMines(int x, int z) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                int nx = x + dx;
                int nz = z + dz;
                if (nx >= 0 && nx < width && nz >= 0 && nz < height && mineMap[nx][nz]) {
                    count++;
                }
            }
        }
        return count;
    }

    public void reveal(int x, int z, BlockPos clickPos) {
        if (!initialized) {
            generate(clickPos);
        }
        
        if (gameOver || revealed[x][z]) return;
        
        if (mineMap[x][z]) {
            gameOver = true;
            endTime = System.currentTimeMillis();
            gameOverTime = System.currentTimeMillis();
            revealAllMines(x, z);
            return;
        }

        revealTile(x, z);
        
        if (revealedCount == width * height - mines) {
            won = true;
            gameOver = true;
            endTime = System.currentTimeMillis();
            gameOverTime = System.currentTimeMillis();
        }
    }

    private void revealTile(int x, int z) {
        if (revealed[x][z] || mineMap[x][z]) return;
        
        revealed[x][z] = true;
        revealedCount++;
        
        BlockPos pos = origin.offset(x, 0, z);
        if (adjacent[x][z] == 0) {
            level.setBlock(pos, getBlock("minesweeper_empty"), 3);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0) continue;
                    int nx = x + dx;
                    int nz = z + dz;
                    if (nx >= 0 && nx < width && nz >= 0 && nz < height) {
                        revealTile(nx, nz);
                    }
                }
            }
        } else {
            level.setBlock(pos, getBlock("minesweeper_" + adjacent[x][z]), 3);
        }
    }

    private void revealAllMines(int triggeredX, int triggeredZ) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                BlockPos pos = origin.offset(x, 0, z);
                if (mineMap[x][z]) {
                    if (x == triggeredX && z == triggeredZ) {
                        level.setBlock(pos, getBlock("minesweeper_triggered_mine"), 3);
                    } else {
                        level.setBlock(pos, getBlock("minesweeper_mine"), 3);
                    }
                }
            }
        }
    }

    public void placeBoard() {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                level.setBlock(origin.offset(x, 0, z), getBlock("minesweeper_block"), 3);
            }
        }
    }

    public void restoreTerrain() {
        terrainSnapshot.forEach((pos, state) -> level.setBlock(pos, state, 3));
    }

    public void updateFlagCount(boolean added) {
        flagCount += added ? 1 : -1;
    }

    public int getRemainingMines() {
        return mines - flagCount;
    }

    public long getElapsedTime() {
        if (startTime == -1) return 0;
        if (endTime != -1) return (endTime - startTime) / 1000;
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    public long getGameOverTime() {
        return gameOverTime;
    }

    public boolean isInBounds(BlockPos pos) {
        int x = pos.getX() - origin.getX();
        int z = pos.getZ() - origin.getZ();
        return x >= 0 && x < width && z >= 0 && z < height && pos.getY() == origin.getY();
    }

    public int[] getCoords(BlockPos pos) {
        return new int[]{pos.getX() - origin.getX(), pos.getZ() - origin.getZ()};
    }

    private BlockState getBlock(String name) {
        return ForgeRegistries.BLOCKS.getValue(new net.minecraft.resources.ResourceLocation("the_arg_container", name)).defaultBlockState();
    }

    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("width", width);
        tag.putInt("height", height);
        tag.putInt("mines", mines);
        tag.putInt("originX", origin.getX());
        tag.putInt("originY", origin.getY());
        tag.putInt("originZ", origin.getZ());
        tag.putBoolean("gameOver", gameOver);
        tag.putBoolean("won", won);
        tag.putInt("revealedCount", revealedCount);
        tag.putBoolean("initialized", initialized);
        tag.putInt("flagCount", flagCount);
        tag.putLong("startTime", startTime);
        tag.putLong("endTime", endTime);
        tag.putLong("gameOverTime", gameOverTime);
        
        ListTag mineList = new ListTag();
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                if (mineMap[x][z]) {
                    CompoundTag pos = new CompoundTag();
                    pos.putInt("x", x);
                    pos.putInt("z", z);
                    mineList.add(pos);
                }
            }
        }
        tag.put("mines_pos", mineList);
        
        ListTag revealedList = new ListTag();
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                if (revealed[x][z]) {
                    CompoundTag pos = new CompoundTag();
                    pos.putInt("x", x);
                    pos.putInt("z", z);
                    revealedList.add(pos);
                }
            }
        }
        tag.put("revealed_pos", revealedList);
        
        return tag;
    }

    public static MinesweeperBoard loadFromNBT(Level level, CompoundTag tag) {
        int width = tag.getInt("width");
        int height = tag.getInt("height");
        int mines = tag.getInt("mines");
        BlockPos origin = new BlockPos(tag.getInt("originX"), tag.getInt("originY"), tag.getInt("originZ"));
        
        MinesweeperBoard board = new MinesweeperBoard(level, origin, width, height, mines);
        board.gameOver = tag.getBoolean("gameOver");
        board.won = tag.getBoolean("won");
        board.revealedCount = tag.getInt("revealedCount");
        board.initialized = tag.getBoolean("initialized");
        board.flagCount = tag.getInt("flagCount");
        board.startTime = tag.getLong("startTime");
        board.endTime = tag.getLong("endTime");
        board.gameOverTime = tag.getLong("gameOverTime");
        
        ListTag mineList = tag.getList("mines_pos", Tag.TAG_COMPOUND);
        for (int i = 0; i < mineList.size(); i++) {
            CompoundTag pos = mineList.getCompound(i);
            board.mineMap[pos.getInt("x")][pos.getInt("z")] = true;
        }
        
        ListTag revealedList = tag.getList("revealed_pos", Tag.TAG_COMPOUND);
        for (int i = 0; i < revealedList.size(); i++) {
            CompoundTag pos = revealedList.getCompound(i);
            board.revealed[pos.getInt("x")][pos.getInt("z")] = true;
        }
        
        if (board.initialized) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < height; z++) {
                    if (!board.mineMap[x][z]) {
                        board.adjacent[x][z] = board.countAdjacentMines(x, z);
                    }
                }
            }
        }
        
        return board;
    }

    public boolean isGameOver() { return gameOver; }
    public boolean isWon() { return won; }
    public boolean isRevealed(int x, int z) { return revealed[x][z]; }
}
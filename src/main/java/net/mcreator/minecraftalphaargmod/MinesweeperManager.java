package net.mcreator.minecraftalphaargmod.minesweeper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MinesweeperManager {
    private static final Map<UUID, MinesweeperBoard> activeBoards = new HashMap<>();
    private static final Map<BlockPos, UUID> boardLocations = new HashMap<>();

    public static void createBoard(Level level, BlockPos origin, Difficulty difficulty, UUID playerUUID) {
        removeBoard(playerUUID);
        
        MinesweeperBoard board = new MinesweeperBoard(level, origin, 
            difficulty.width, difficulty.height, difficulty.mines);
        board.placeBoard();
        activeBoards.put(playerUUID, board);
        
        for (int x = 0; x < difficulty.width; x++) {
            for (int z = 0; z < difficulty.height; z++) {
                boardLocations.put(origin.offset(x, 0, z), playerUUID);
            }
        }
    }

    public static MinesweeperBoard getBoard(BlockPos pos) {
        UUID uuid = boardLocations.get(pos);
        return uuid != null ? activeBoards.get(uuid) : null;
    }

    public static MinesweeperBoard getBoard(UUID playerUUID) {
        return activeBoards.get(playerUUID);
    }

    public static void removeBoard(UUID playerUUID) {
        MinesweeperBoard board = activeBoards.remove(playerUUID);
        if (board != null) {
            boardLocations.values().removeIf(uuid -> uuid.equals(playerUUID));
        }
    }

    public static void restoreBoard(UUID playerUUID) {
        MinesweeperBoard board = activeBoards.get(playerUUID);
        if (board != null) {
            board.restoreTerrain();
            removeBoard(playerUUID);
        }
    }

    public enum Difficulty {
        BEGINNER(9, 9, 10),
        INTERMEDIATE(16, 16, 40),
        EXPERT(30, 16, 99);

        public final int width, height, mines;
        Difficulty(int w, int h, int m) { width = w; height = h; mines = m; }
    }
}
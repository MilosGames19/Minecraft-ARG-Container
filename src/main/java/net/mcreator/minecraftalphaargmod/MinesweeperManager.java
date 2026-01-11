package net.mcreator.minecraftalphaargmod.minesweeper;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE)
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

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID playerUUID = event.getEntity().getUUID();
        MinesweeperBoard board = activeBoards.get(playerUUID);
        if (board != null && event.getEntity().level() instanceof ServerLevel serverLevel) {
            saveBoard(serverLevel, playerUUID, board);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        UUID playerUUID = event.getEntity().getUUID();
        if (event.getEntity().level() instanceof ServerLevel serverLevel) {
            loadBoard(serverLevel, playerUUID);
        }
    }

    private static void saveBoard(ServerLevel level, UUID playerUUID, MinesweeperBoard board) {
        File saveDir = new File(level.getServer().getServerDirectory(), "minesweeper_saves");
        if (!saveDir.exists()) saveDir.mkdirs();
        
        File saveFile = new File(saveDir, playerUUID.toString() + ".dat");
        try {
            NbtIo.writeCompressed(board.saveToNBT(), saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadBoard(ServerLevel level, UUID playerUUID) {
        File saveDir = new File(level.getServer().getServerDirectory(), "minesweeper_saves");
        File saveFile = new File(saveDir, playerUUID.toString() + ".dat");
        
        if (saveFile.exists()) {
            try {
                CompoundTag tag = NbtIo.readCompressed(saveFile);
                MinesweeperBoard board = MinesweeperBoard.loadFromNBT(level, tag);
                activeBoards.put(playerUUID, board);
                
                BlockPos origin = new BlockPos(tag.getInt("originX"), tag.getInt("originY"), tag.getInt("originZ"));
                int width = tag.getInt("width");
                int height = tag.getInt("height");
                
                for (int x = 0; x < width; x++) {
                    for (int z = 0; z < height; z++) {
                        boardLocations.put(origin.offset(x, 0, z), playerUUID);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
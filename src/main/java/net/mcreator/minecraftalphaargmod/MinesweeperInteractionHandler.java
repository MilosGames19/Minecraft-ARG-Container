package net.mcreator.minecraftalphaargmod;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MinesweeperInteractionHandler {

    // ------------------------------------------------------------------
    // LEFT-CLICK — reveal tile
    // ------------------------------------------------------------------

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        BlockPos pos   = event.getPos();
        Level    level = event.getLevel();

        String blockName = getBlockName(level, pos);
        if (!blockName.startsWith("minesweeper_")) return;

        MinesweeperBoard board = MinesweeperManager.getBoard(pos);
        if (board == null || board.isGameOver()) return;

        // Cancel regardless — we never want the default mining behaviour
        event.setCanceled(true);

        if (blockName.equals("minesweeper_block") && !level.isClientSide) {
            int[] coords = board.getCoords(pos);
            board.reveal(coords[0], coords[1], pos);

            // Sync updated state (flag count, game-over, timer) to the client
            if (event.getEntity() instanceof ServerPlayer sp) {
                MinesweeperManager.syncToPlayer(sp);
            }
        }
        // Flags / question marks: event cancelled → block protected, no further action.
    }

    // ------------------------------------------------------------------
    // BLOCK BREAK EVENT — the definitive guard against block destruction.
    //
    // LeftClickBlock only fires on the initial click frame. If the player
    // holds the mouse (survival mining) or uses creative-mode insta-break,
    // the game sends a separate BLOCK_BREAK packet that raises this event.
    // Cancelling here is what actually prevents the block from disappearing.
    // ------------------------------------------------------------------

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockPos pos       = event.getPos();
        String   blockName = ForgeRegistries.BLOCKS.getKey(event.getState().getBlock()).getPath();

        if (!blockName.startsWith("minesweeper_")) return;

        MinesweeperBoard board = MinesweeperManager.getBoard(pos);
        // Only protect while a game is active and not yet over
        if (board != null && !board.isGameOver()) {
            event.setCanceled(true);
        }
    }

    // ------------------------------------------------------------------
    // RIGHT-CLICK — cycle flag / question mark / blank
    // ------------------------------------------------------------------

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        BlockPos pos   = event.getPos();
        Level    level = event.getLevel();

        String blockName = getBlockName(level, pos);
        if (!blockName.startsWith("minesweeper_")) return;

        MinesweeperBoard board = MinesweeperManager.getBoard(pos);
        if (board == null || board.isGameOver()) return;

        int[] coords = board.getCoords(pos);
        if (board.isRevealed(coords[0], coords[1])) return;

        event.setCanceled(true);

        if (!level.isClientSide) {
            switch (blockName) {
                case "minesweeper_block" ->  {
                    level.setBlock(pos, getBlock("minesweeper_flag"), 3);
                    board.updateFlagCount(true);
                }
                case "minesweeper_flag" -> {
                    level.setBlock(pos, getBlock("minesweeper_questionmark_button"), 3);
                    board.updateFlagCount(false);
                }
                case "minesweeper_questionmark_button" ->
                        level.setBlock(pos, getBlock("minesweeper_block"), 3);
            }

            // Sync mine-counter change to client
            if (event.getEntity() instanceof ServerPlayer sp) {
                MinesweeperManager.syncToPlayer(sp);
            }
        }
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private static String getBlockName(Level level, BlockPos pos) {
        return ForgeRegistries.BLOCKS.getKey(level.getBlockState(pos).getBlock()).getPath();
    }

    private static BlockState getBlock(String name) {
        return ForgeRegistries.BLOCKS.getValue(
                        new net.minecraft.resources.ResourceLocation("the_arg_container", name))
                .defaultBlockState();
    }
}
package net.mcreator.minecraftalphaargmod.minesweeper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MinesweeperInteractionHandler {

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        BlockPos pos = event.getPos();
        Level level = event.getLevel();

        String blockName = ForgeRegistries.BLOCKS.getKey(level.getBlockState(pos).getBlock()).getPath();
        // Check if the block is part of the Minesweeper game
        if (!blockName.startsWith("minesweeper_")) {
            return;
        }

        MinesweeperBoard board = MinesweeperManager.getBoard(pos);
        // If there's no active game, or the game is over, do nothing (allow breaking)
        if (board == null || board.isGameOver()) {
            return;
        }

        // An active game is in progress, so we take over the left-click behavior.
        // This prevents the block from breaking, even in creative mode.
        event.setCanceled(true);

        // If the player clicks a regular, un-flagged block, reveal it.
        if (blockName.equals("minesweeper_block")) {
            if (!level.isClientSide) {
                int[] coords = board.getCoords(pos);
                board.reveal(coords[0], coords[1], pos);
            }
        }
        // If the player clicks a flag or question mark, the event is simply cancelled,
        // preventing the block from being broken. No other action is taken.
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        BlockPos pos = event.getPos();
        Level level = event.getLevel();

        String blockName = ForgeRegistries.BLOCKS.getKey(level.getBlockState(pos).getBlock()).getPath();
        if (!blockName.startsWith("minesweeper_")) return;

        MinesweeperBoard board = MinesweeperManager.getBoard(pos);
        if (board == null || board.isGameOver()) return;

        int[] coords = board.getCoords(pos);
        if (board.isRevealed(coords[0], coords[1])) return;

        event.setCanceled(true);

        if (!level.isClientSide) {
            switch (blockName) {
                case "minesweeper_block":
                    level.setBlock(pos, getBlock("minesweeper_flag"), 3);
                    board.updateFlagCount(true);
                    break;
                case "minesweeper_flag":
                    level.setBlock(pos, getBlock("minesweeper_questionmark_button"), 3);
                    board.updateFlagCount(false);
                    break;
                case "minesweeper_questionmark_button":
                    level.setBlock(pos, getBlock("minesweeper_block"), 3);
                    break;
            }
        }
    }

    private static net.minecraft.world.level.block.state.BlockState getBlock(String name) {
        return ForgeRegistries.BLOCKS.getValue(new net.minecraft.resources.ResourceLocation("the_arg_container", name)).defaultBlockState();
    }
}

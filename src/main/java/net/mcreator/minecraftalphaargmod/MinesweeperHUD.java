package net.mcreator.minecraftalphaargmod.minesweeper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MinesweeperHUD {
    
    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        MinesweeperBoard board = MinesweeperManager.getBoard(mc.player.getUUID());
        if (board == null) return;
        
        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        
        int minesRemaining = board.getRemainingMines();
        long elapsedTime = board.getElapsedTime();
        
        String mineText = "§c💣 " + minesRemaining;
        String timeText = "§e⏱ " + formatTime(elapsedTime);
        
        guiGraphics.drawString(mc.font, mineText, 10, 10, 0xFFFFFF);
        guiGraphics.drawString(mc.font, timeText, screenWidth - mc.font.width(timeText) - 10, 10, 0xFFFFFF);
        
        if (board.isGameOver()) {
            String status = board.isWon() ? "§a§lVICTORY!" : "§c§lGAME OVER";
            int width = mc.font.width(status);
            guiGraphics.drawString(mc.font, status, (screenWidth - width) / 2, 30, 0xFFFFFF);
        }
    }
    
    private static String formatTime(long seconds) {
        long mins = seconds / 60;
        long secs = seconds % 60;
        return mins > 0 ? String.format("%d:%02d", mins, secs) : String.valueOf(secs);
    }
}
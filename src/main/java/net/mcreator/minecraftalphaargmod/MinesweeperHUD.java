package net.mcreator.minecraftalphaargmod.minesweeper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MinesweeperHUD {
    
    private static final long GAME_OVER_DISPLAY_TIME = 3000;
    
    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        MinesweeperBoard board = MinesweeperManager.getBoard(mc.player.getUUID());
        if (board == null) return;
        
        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        
        int minesRemaining = board.getRemainingMines();
        long elapsedTime = board.getElapsedTime();
        
        int hudY = screenHeight - 51;
        
        String mineIcon = "💣";
        String mineCount = String.valueOf(minesRemaining);
        int mineIconWidth = mc.font.width(mineIcon);
        int mineCountWidth = mc.font.width(mineCount);
        int mineTotalWidth = mineIconWidth + mineCountWidth + 12;
        
        int mineX = (screenWidth / 2) - 95;
        
        guiGraphics.fill(mineX - 1, hudY - 1, mineX + mineTotalWidth + 1, hudY + 12, 0xFFFFFFFF);
        guiGraphics.fill(mineX, hudY, mineX + mineTotalWidth, hudY + 11, 0xFF2A2A2A);
        guiGraphics.drawString(mc.font, mineIcon, mineX + 3, hudY + 2, 0xFFFF5555);
        guiGraphics.drawString(mc.font, mineCount, mineX + mineIconWidth + 6, hudY + 2, 0xFFFFFFFF);
        
        String timeIcon = "⏱";
        String timeCount = formatTime(elapsedTime);
        int timeIconWidth = mc.font.width(timeIcon);
        int timeCountWidth = mc.font.width(timeCount);
        int timeTotalWidth = timeIconWidth + timeCountWidth + 12;
        
        int timeX = (screenWidth / 2) + 95 - timeTotalWidth;
        
        guiGraphics.fill(timeX - 1, hudY - 1, timeX + timeTotalWidth + 1, hudY + 12, 0xFFFFFFFF);
        guiGraphics.fill(timeX, hudY, timeX + timeTotalWidth, hudY + 11, 0xFF2A2A2A);
        guiGraphics.drawString(mc.font, timeIcon, timeX + 3, hudY + 2, 0xFFFFAA00);
        guiGraphics.drawString(mc.font, timeCount, timeX + timeIconWidth + 6, hudY + 2, 0xFFFFFFFF);
        
        if (board.isGameOver() && board.getGameOverTime() != -1) {
            long timeSinceGameOver = System.currentTimeMillis() - board.getGameOverTime();
            
            if (timeSinceGameOver < GAME_OVER_DISPLAY_TIME) {
                String status = board.isWon() ? "✦ VICTORY ✦" : "💣 GAME OVER 💣";
                String timeMsg = board.isWon() ? "Time: " + formatTime(board.getElapsedTime()) : "";
                
                int statusWidth = mc.font.width(status) + 20;
                int statusX = (screenWidth - statusWidth) / 2;
                int statusY = screenHeight / 2 - 30;
                
                int bgColor = board.isWon() ? 0xFF2D5016 : 0xFF501616;
                int borderColor = board.isWon() ? 0xFF55FF55 : 0xFFFF5555;
                int textColor = board.isWon() ? 0xFFAAFFAA : 0xFFFFAAAA;
                
                guiGraphics.fill(statusX - 2, statusY - 2, statusX + statusWidth + 2, statusY + 14, borderColor);
                guiGraphics.fill(statusX, statusY, statusX + statusWidth, statusY + 12, bgColor);
                guiGraphics.fill(statusX, statusY, statusX + statusWidth, statusY + 1, borderColor);
                guiGraphics.drawString(mc.font, status, statusX + 10, statusY + 2, textColor);
                
                if (board.isWon() && !timeMsg.isEmpty()) {
                    int timeMsgWidth = mc.font.width(timeMsg) + 12;
                    int timeMsgX = (screenWidth - timeMsgWidth) / 2;
                    int timeMsgY = statusY + 16;
                    
                    guiGraphics.fill(timeMsgX - 1, timeMsgY - 1, timeMsgX + timeMsgWidth + 1, timeMsgY + 11, 0xFF55FF55);
                    guiGraphics.fill(timeMsgX, timeMsgY, timeMsgX + timeMsgWidth, timeMsgY + 10, 0xFF1A3A0D);
                    guiGraphics.drawString(mc.font, timeMsg, timeMsgX + 6, timeMsgY + 1, 0xFFFFFF55);
                }
            }
        }
    }
    
    private static String formatTime(long seconds) {
        long mins = seconds / 60;
        long secs = seconds % 60;
        if (mins > 0) {
            return String.format("%d:%02d", mins, secs);
        }
        return secs + "s";
    }
}
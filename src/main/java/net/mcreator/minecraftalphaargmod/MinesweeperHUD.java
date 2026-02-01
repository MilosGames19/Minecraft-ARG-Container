package net.mcreator.minecraftalphaargmod.minesweeper;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MinesweeperHUD {

    private static final long GAME_OVER_DISPLAY_TIME = 5000; // 5 seconds

    private static boolean shouldRenderMinesweeperHud(MinesweeperBoard board) {
        if (board == null) {
            return false;
        }
        if (board.isGameOver()) {
            long timeSinceGameOver = System.currentTimeMillis() - board.getGameOverTime();
            return timeSinceGameOver < GAME_OVER_DISPLAY_TIME;
        }
        return true;
    }

    @SubscribeEvent
    public static void onRenderOverlayPre(RenderGuiOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        MinesweeperBoard board = MinesweeperManager.getBoard(mc.player.getUUID());
        if (!shouldRenderMinesweeperHud(board)) {
            return;
        }

        // Hide vanilla HUD elements if our HUD is active
        if (event.getOverlay() == VanillaGuiOverlay.HOTBAR.type() ||
            event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type() ||
            event.getOverlay() == VanillaGuiOverlay.FOOD_LEVEL.type() ||
            event.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type() ||
            event.getOverlay() == VanillaGuiOverlay.EXPERIENCE_BAR.type() ||
            event.getOverlay() == VanillaGuiOverlay.AIR_LEVEL.type()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderOverlayPost(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        MinesweeperBoard board = MinesweeperManager.getBoard(mc.player.getUUID());
        if (!shouldRenderMinesweeperHud(board)) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // Render the main HUD elements (timer and mine count)
        renderHudElements(guiGraphics, board, screenWidth, screenHeight);
    }

    private static void renderHudElements(GuiGraphics guiGraphics, MinesweeperBoard board, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        int minesRemaining = board.getRemainingMines();
        long elapsedTime = board.getElapsedTime();

        int hudY = screenHeight - 24; // Moved up slightly to accommodate borders
        int hudHeight = 20;

        // Standard Minecraft Tooltip Colors
        int backgroundColor = 0xFF161616;
        int borderColorStart = 0x505000FF;
        int borderColorEnd = 0x5028007F;

        // --- Mine Counter ---
        String mineIcon = "💣 ";
        String mineCount = String.valueOf(minesRemaining);
        int mineTextWidth = mc.font.width(mineIcon + " " + mineCount);
        int mineBoxWidth = mineTextWidth + 12;
        int mineX = (screenWidth / 2) - 91; // Aligns with left of hotbar

        renderMinecraftStyleBox(guiGraphics, mineX, hudY, mineBoxWidth, hudHeight, backgroundColor, borderColorStart, borderColorEnd);
        guiGraphics.drawString(mc.font, mineIcon, mineX + 6, hudY + 6, 0xFFFF5555, true);
        guiGraphics.drawString(mc.font, mineCount, mineX + 6 + mc.font.width(mineIcon) + 2, hudY + 6, 0xFFFFFFFF, true);

        // --- Timer ---
        String timeIcon = "⏱ ";
        String timeCount = formatTime(elapsedTime);
        int timeTextWidth = mc.font.width(timeIcon + " " + timeCount);
        int timeBoxWidth = timeTextWidth + 12;
        int timeX = (screenWidth / 2) + 91 - timeBoxWidth; // Aligns with right of hotbar

        renderMinecraftStyleBox(guiGraphics, timeX, hudY, timeBoxWidth, hudHeight, backgroundColor, borderColorStart, borderColorEnd);
        guiGraphics.drawString(mc.font, timeIcon, timeX + 6, hudY + 6, 0xFFFFAA00, true);
        guiGraphics.drawString(mc.font, timeCount, timeX + 6 + mc.font.width(timeIcon) + 2, hudY + 6, 0xFFFFFFFF, true);

        // --- Game Over Status (Centered in Hotbar) ---
        if (board.isGameOver()) {
            boolean won = board.isWon();
            Component status = Component.literal(won ? "VICTORY" : "GAME OVER");
            
            int statusWidth = mc.font.width(status);
            int statusBoxWidth = statusWidth + 20;
            int statusX = (screenWidth - statusBoxWidth) / 2;
            
            // Custom border colors for status
            int statusBorderStart = won ? 0xFF00FF00 : 0xFFFF0000;
            int statusBorderEnd = won ? 0xFF005500 : 0xFF550000;
            int textColor = won ? 0xFF55FF55 : 0xFFFF5555;

            renderMinecraftStyleBox(guiGraphics, statusX, hudY, statusBoxWidth, hudHeight, backgroundColor, statusBorderStart, statusBorderEnd);
            guiGraphics.drawCenteredString(mc.font, status, screenWidth / 2, hudY + 6, textColor);
        }
    }

    private static void renderMinecraftStyleBox(GuiGraphics guiGraphics, int x, int y, int width, int height, int backgroundColor, int borderColorStart, int borderColorEnd) {
        int zLevel = 0;
        
        // Background
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, backgroundColor);
        
        // Borders (Gradient)
        // Top
        guiGraphics.fillGradient(x + 1, y, x + width - 1, y + 1, zLevel, borderColorStart, borderColorStart);
        // Bottom
        guiGraphics.fillGradient(x + 1, y + height - 1, x + width - 1, y + height, zLevel, borderColorEnd, borderColorEnd);
        // Left
        guiGraphics.fillGradient(x, y, x + 1, y + height, zLevel, borderColorStart, borderColorEnd);
        // Right
        guiGraphics.fillGradient(x + width - 1, y, x + width, y + height, zLevel, borderColorStart, borderColorEnd);
    }

    private static String formatTime(long seconds) {
        long mins = seconds / 60;
        long secs = seconds % 60;
        if (mins > 0) {
            return String.format("%d:%02d", mins, secs);
        }
        return String.valueOf(secs);
    }
}

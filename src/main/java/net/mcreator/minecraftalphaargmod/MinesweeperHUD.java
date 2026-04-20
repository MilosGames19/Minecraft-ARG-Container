package net.mcreator.minecraftalphaargmod;

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

	private static final long GAME_OVER_DISPLAY_MS = 5000L;

	private static boolean shouldRenderHud() {
		if (!ClientBoardCache.hasBoard())
			return false;
		if (ClientBoardCache.isGameOver()) {
			return (System.currentTimeMillis() - ClientBoardCache.getGameOverTime()) < GAME_OVER_DISPLAY_MS;
		}
		return true;
	}

	@SubscribeEvent
	public static void onRenderOverlayPre(RenderGuiOverlayEvent.Pre event) {
		if (!shouldRenderHud())
			return;

		if (event.getOverlay() == VanillaGuiOverlay.HOTBAR.type() || event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type() || event.getOverlay() == VanillaGuiOverlay.FOOD_LEVEL.type()
				|| event.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type() || event.getOverlay() == VanillaGuiOverlay.EXPERIENCE_BAR.type() || event.getOverlay() == VanillaGuiOverlay.AIR_LEVEL.type()) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onRenderOverlayPost(RenderGuiOverlayEvent.Post event) {
		if (!shouldRenderHud())
			return;

		Minecraft mc = Minecraft.getInstance();
		GuiGraphics guiGraphics = event.getGuiGraphics();
		int screenWidth = mc.getWindow().getGuiScaledWidth();
		int screenHeight = mc.getWindow().getGuiScaledHeight();

		renderHudElements(guiGraphics, screenWidth, screenHeight, mc);
	}

	private static void renderHudElements(GuiGraphics gui, int sw, int sh, Minecraft mc) {
		int minesRemaining = ClientBoardCache.getMinesRemaining();
		long elapsedTime = ClientBoardCache.getElapsedSeconds();

		int hudY = sh - 24;
		int hudHeight = 20;

		int backgroundColor = 0xFF161616;
		int borderColorStart = 0x505000FF;
		int borderColorEnd = 0x5028007F;

		String mineIcon = "💣 ";
		String mineCount = String.valueOf(minesRemaining);
		int mineTextWidth = mc.font.width(mineIcon + " " + mineCount);
		int mineBoxWidth = mineTextWidth + 12;
		int mineX = (sw / 2) - 91;

		renderBox(gui, mineX, hudY, mineBoxWidth, hudHeight, backgroundColor, borderColorStart, borderColorEnd);
		gui.drawString(mc.font, mineIcon, mineX + 6, hudY + 6, 0xFFFF5555, true);
		gui.drawString(mc.font, mineCount, mineX + 6 + mc.font.width(mineIcon) + 2, hudY + 6, 0xFFFFFFFF, true);

		String timeIcon = "⏱ ";
		String timeStr = formatTime(elapsedTime);
		int timeTextWidth = mc.font.width(timeIcon + " " + timeStr);
		int timeBoxWidth = timeTextWidth + 12;
		int timeX = (sw / 2) + 91 - timeBoxWidth;

		renderBox(gui, timeX, hudY, timeBoxWidth, hudHeight, backgroundColor, borderColorStart, borderColorEnd);
		gui.drawString(mc.font, timeIcon, timeX + 6, hudY + 6, 0xFFFFAA00, true);
		gui.drawString(mc.font, timeStr, timeX + 6 + mc.font.width(timeIcon) + 2, hudY + 6, 0xFFFFFFFF, true);

		if (ClientBoardCache.isGameOver()) {
			boolean won = ClientBoardCache.isWon();
			Component status = Component.literal(won ? "VICTORY" : "GAME OVER");

			int statusWidth = mc.font.width(status);
			int statusBoxWidth = statusWidth + 20;
			int statusX = (sw - statusBoxWidth) / 2;

			int statusBorderStart = won ? 0xFF00FF00 : 0xFFFF0000;
			int statusBorderEnd = won ? 0xFF005500 : 0xFF550000;
			int textColor = won ? 0xFF55FF55 : 0xFFFF5555;

			renderBox(gui, statusX, hudY, statusBoxWidth, hudHeight, backgroundColor, statusBorderStart, statusBorderEnd);
			gui.drawCenteredString(mc.font, status, sw / 2, hudY + 6, textColor);
		}
	}

	private static void renderBox(GuiGraphics gui, int x, int y, int w, int h, int bg, int borderStart, int borderEnd) {
		gui.fill(x + 1, y + 1, x + w - 1, y + h - 1, bg);
		gui.fillGradient(x + 1, y, x + w - 1, y + 1, 0, borderStart, borderStart);
		gui.fillGradient(x + 1, y + h - 1, x + w - 1, y + h, 0, borderEnd, borderEnd);
		gui.fillGradient(x, y, x + 1, y + h, 0, borderStart, borderEnd);
		gui.fillGradient(x + w - 1, y, x + w, y + h, 0, borderStart, borderEnd);
	}

	private static String formatTime(long seconds) {
		long mins = seconds / 60;
		long secs = seconds % 60;
		return mins > 0 ? String.format("%d:%02d", mins, secs) : String.valueOf(secs);
	}
}
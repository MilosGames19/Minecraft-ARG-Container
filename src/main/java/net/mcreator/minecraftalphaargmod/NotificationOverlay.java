package net.mcreator.minecraftalphaargmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class NotificationOverlay {

	private static final int DISPLAY_TICKS = 120;
	private static final int FADE_TICKS = 20;

	public static final int HOTBAR_HEIGHT = 23;
	public static final int BASE_INNER_W = 162;
	public static final int LINE_HEIGHT_BASE = 20;
	public static final int LINE_HEIGHT_STEP = 10;
	public static final int PAD_X = 4;
	public static final int PAD_Y = 3;

	private static final int DEFAULT_OFFSET_X = 20;
	private static final int DEFAULT_OFFSET_Y = 30;

	private static final int BG_COLOR_TOP = 0xA0000000;
	private static final int BG_COLOR_BOTTOM = 0x70000000;

	private static int anchorX = DEFAULT_OFFSET_X;
	private static int anchorY = DEFAULT_OFFSET_Y;
	private static float scale = 1.0f;

	public static int getAnchorX() {
		return anchorX;
	}

	public static int getAnchorY() {
		return anchorY;
	}

	public static float getScale() {
		return scale;
	}

	public static void setAnchor(int x, int y) {
		anchorX = Math.max(0, x);
		anchorY = Math.max(0, y);
	}

	public static void setScale(float s) {
		scale = Math.max(0.5f, Math.min(2.0f, s));
	}

	private static final List<Entry> entries = new ArrayList<>();

	public static void push(String text, int color, boolean fromServer) {
		entries.add(new Entry(text, color, fromServer));
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END)
			return;
		Iterator<Entry> it = entries.iterator();
		while (it.hasNext()) {
			Entry e = it.next();
			e.tick++;
			if (e.tick >= DISPLAY_TICKS)
				it.remove();
		}
	}

	@SubscribeEvent
	public static void onRenderGui(RenderGuiEvent.Post event) {
		if (entries.isEmpty())
			return;

		Minecraft mc = Minecraft.getInstance();
		GuiGraphics gfx = event.getGuiGraphics();
		Font font = mc.font;
		int screenH = mc.getWindow().getGuiScaledHeight();
		float s = scale;

		int hotbarBaseline = screenH - HOTBAR_HEIGHT;
		int stackOffset = 0;

		for (Entry e : entries) {
			int remaining = DISPLAY_TICKS - e.tick;
			if (remaining <= 0)
				continue;

			float alpha = (remaining < FADE_TICKS) ? remaining / (float) FADE_TICKS : 1.0f;
			int aInt = (int) (alpha * 255);

			List<String> lines = new ArrayList<>();
			if (e.fromServer)
				lines.add("SERVER MESSAGE:");
			lines.addAll(wrapText(font, e.text, BASE_INNER_W));

			int extraLines = Math.max(0, lines.size() - 1);
			int blockHeight = LINE_HEIGHT_BASE + extraLines * LINE_HEIGHT_STEP;
			int localW = BASE_INNER_W + PAD_X * 2;
			int localH = blockHeight + PAD_Y * 2;
			int scaledH = (int) (localH * s);

			int bgScreenX = anchorX;
			int bgScreenY = hotbarBaseline - anchorY - scaledH - stackOffset;

			gfx.pose().pushPose();
			gfx.pose().translate(bgScreenX, bgScreenY, 0);
			gfx.pose().scale(s, s, 1.0f);

			gfx.fillGradient(0, 0, localW, localH, applyAlpha(BG_COLOR_TOP, aInt), applyAlpha(BG_COLOR_BOTTOM, aInt));

			int argb = (aInt << 24) | (e.color & 0x00FFFFFF);
			for (int i = 0; i < lines.size(); i++) {
				int lineY = PAD_Y + (i == 0 ? 0 : LINE_HEIGHT_BASE + (i - 1) * LINE_HEIGHT_STEP);
				gfx.drawString(font, lines.get(i), PAD_X, lineY, argb, true);
			}

			gfx.pose().popPose();

			stackOffset += scaledH + (int) (2 * s);
		}
	}

	private static int applyAlpha(int argbColor, int alpha255) {
		int stored = (argbColor >>> 24) & 0xFF;
		int blended = (stored * alpha255) / 255;
		return (argbColor & 0x00FFFFFF) | (blended << 24);
	}

	private static List<String> wrapText(Font font, String text, int maxWidth) {
		List<String> result = new ArrayList<>();
		String[] words = text.split(" ");
		StringBuilder current = new StringBuilder();
		for (String word : words) {
			String candidate = current.isEmpty() ? word : current + " " + word;
			if (font.width(candidate) > maxWidth && !current.isEmpty()) {
				result.add(current.toString());
				current = new StringBuilder(word);
			} else {
				current = new StringBuilder(candidate);
			}
		}
		if (!current.isEmpty())
			result.add(current.toString());
		return result;
	}

	private static class Entry {
		final String text;
		final int color;
		final boolean fromServer;
		int tick = 0;

		Entry(String text, int color, boolean fromServer) {
			this.text = text;
			this.color = color;
			this.fromServer = fromServer;
		}
	}
}
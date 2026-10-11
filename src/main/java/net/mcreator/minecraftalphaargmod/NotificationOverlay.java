package net.mcreator.minecraftalphaargmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "the_arg_container")
public class NotificationOverlay {

	private static final long DISPLAY_NANOS = 6000L * 1_000_000L;
	private static final long FADE_NANOS = 1000L * 1_000_000L;

	private static final int MAX_ENTRIES = 8;

	private static final int MAX_TITLE_LEN = 256;
	private static final int MAX_TEXT_LEN = 1024;

	public static final int HOTBAR_HEIGHT = 23;
	public static final int BASE_INNER_W = 162;
	public static final int MAX_INNER_W = 320;
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

	private static boolean configLoaded = false;

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
		anchorY = Math.max(0, Math.min(y, 4096));
	}

	public static void setScale(float s) {
		scale = Math.max(0.5f, Math.min(2.0f, s));
	}

	public static void saveConfig() {
		try {
			Path cfg = FMLPaths.CONFIGDIR.get().resolve("argmod_notification_pos.properties");
			Properties props = new Properties();
			props.setProperty("anchorX", String.valueOf(anchorX));
			props.setProperty("anchorY", String.valueOf(anchorY));
			props.setProperty("scale", String.valueOf(scale));
			try (var writer = Files.newBufferedWriter(cfg)) {
				props.store(writer, null);
			}
		} catch (Exception ignored) {
		}
	}

	private static void loadConfig() {
		try {
			Path cfg = FMLPaths.CONFIGDIR.get().resolve("argmod_notification_pos.properties");
			if (!Files.exists(cfg))
				return;
			Properties props = new Properties();
			try (var reader = Files.newBufferedReader(cfg)) {
				props.load(reader);
			}
			int x = Integer.parseInt(props.getProperty("anchorX", String.valueOf(DEFAULT_OFFSET_X)));
			int y = Integer.parseInt(props.getProperty("anchorY", String.valueOf(DEFAULT_OFFSET_Y)));
			float s = Float.parseFloat(props.getProperty("scale", "1.0"));
			setAnchor(x, y);
			setScale(s);
		} catch (Exception ignored) {
		}
	}

	private static final List<Entry> entries = new ArrayList<>();

	public static void push(@Nullable String title, String text, int titleColor, int messageColor, boolean fromServer) {
		Objects.requireNonNull(text, "text");
		if (title != null && title.length() > MAX_TITLE_LEN) {
			title = title.substring(0, MAX_TITLE_LEN);
		}
		if (text.length() > MAX_TEXT_LEN) {
			text = text.substring(0, MAX_TEXT_LEN);
		}
		while (entries.size() >= MAX_ENTRIES) {
			entries.remove(0);
		}
		entries.add(new Entry(title, text, titleColor, messageColor, fromServer));
	}

	@SubscribeEvent
	public static void onLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
		entries.clear();
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END)
			return;
		long now = System.nanoTime();
		Iterator<Entry> it = entries.iterator();
		while (it.hasNext()) {
			Entry e = it.next();
			if (now - e.createdAtNanos >= DISPLAY_NANOS)
				it.remove();
		}
	}

	@SubscribeEvent
	public static void onRenderGui(RenderGuiEvent.Post event) {
		if (!configLoaded) {
			loadConfig();
			configLoaded = true;
		}

		if (entries.isEmpty())
			return;

		Minecraft mc = Minecraft.getInstance();
		GuiGraphics gfx = event.getGuiGraphics();
		Font font = mc.font;
		int screenW = mc.getWindow().getGuiScaledWidth();
		int screenH = mc.getWindow().getGuiScaledHeight();
		float s = scale;

		int hotbarBaseline = screenH - HOTBAR_HEIGHT;
		int stackOffset = 0;
		long now = System.nanoTime();

		int maxContentW = computeMaxContentWidth(screenW, anchorX, s);

		for (Entry e : entries) {
			long age = now - e.createdAtNanos;
			long remaining = DISPLAY_NANOS - age;
			if (remaining <= 0)
				continue;

			float alpha = (remaining < FADE_NANOS) ? remaining / (float) FADE_NANOS : 1.0f;
			int aInt = (int) (alpha * 255);

			Layout layout = computeLayout(font, e.resolvedTitle(), e.text, maxContentW);

			int localW = layout.innerW + PAD_X * 2;
			int localH = layout.blockHeight + PAD_Y * 2;
			int scaledH = (int) (localH * s);

			int bgScreenX = anchorX;
			int desiredScreenY = hotbarBaseline - anchorY - stackOffset;
			int maxScreenY = hotbarBaseline - scaledH;
			int bgScreenY = Math.min(desiredScreenY, maxScreenY);

			gfx.pose().pushPose();
			gfx.pose().translate(bgScreenX, bgScreenY, 0);
			gfx.pose().scale(s, s, 1.0f);

			gfx.fillGradient(0, 0, localW, localH, applyAlpha(BG_COLOR_TOP, aInt), applyAlpha(BG_COLOR_BOTTOM, aInt));

			int titleArgb = (aInt << 24) | (e.titleColor & 0x00FFFFFF);
			int messageArgb = (aInt << 24) | (e.messageColor & 0x00FFFFFF);

			for (int i = 0; i < layout.lines.size(); i++) {
				boolean isTitleLine = layout.hasTitle && i == 0;
				String line = layout.lines.get(i);
				int lineY = PAD_Y + lineYOffset(layout.hasTitle, i);

				int lineX = PAD_X;
				if (!layout.hasTitle) {
					lineX = PAD_X + Math.max(0, (layout.innerW - font.width(line)) / 2);
				}

				gfx.drawString(font, line, lineX, lineY, isTitleLine ? titleArgb : messageArgb, true);
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

	public static Layout computeLayout(Font font, @Nullable String title, String message, int maxInnerW) {
		List<String> lines = new ArrayList<>();
		boolean hasTitle = title != null && !title.isEmpty();
		if (hasTitle) {
			lines.add(title);
		}
		lines.addAll(wrapText(font, message, maxInnerW));

		int widest = 1;
		for (String line : lines) {
			widest = Math.max(widest, font.width(line));
		}
		widest = Math.min(widest, maxInnerW);

		int blockHeight;
		if (hasTitle) {
			int extraLines = Math.max(0, lines.size() - 1);
			blockHeight = LINE_HEIGHT_BASE + extraLines * LINE_HEIGHT_STEP;
		} else {
			blockHeight = Math.max(1, lines.size()) * LINE_HEIGHT_STEP;
		}

		return new Layout(lines, hasTitle, widest, blockHeight);
	}

	public static int lineYOffset(boolean hasTitle, int i) {
		if (i == 0)
			return 0;
		if (hasTitle)
			return LINE_HEIGHT_BASE + (i - 1) * LINE_HEIGHT_STEP;
		return i * LINE_HEIGHT_STEP;
	}

	public static int computeMaxContentWidth(int screenW, int anchorXPos, float scaleFactor) {
		return Math.max(40, (int) ((screenW - anchorXPos - 12) / Math.max(scaleFactor, 0.01f)));
	}

	private static List<String> wrapText(Font font, String text, int maxWidth) {
		List<String> result = new ArrayList<>();
		for (String paragraph : text.split("\n", -1)) {
			result.addAll(wrapParagraph(font, paragraph, maxWidth));
		}
		return result;
	}

	private static List<String> wrapParagraph(Font font, String text, int maxWidth) {
		List<String> result = new ArrayList<>();
		String[] words = text.split(" ");
		StringBuilder current = new StringBuilder();
		for (String word : words) {
			if (font.width(word) > maxWidth) {
				if (!current.isEmpty()) {
					result.add(current.toString());
					current = new StringBuilder();
				}
				result.addAll(splitLongWord(font, word, maxWidth));
				continue;
			}
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

	private static List<String> splitLongWord(Font font, String word, int maxWidth) {
		List<String> result = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			String candidate = current.toString() + c;
			if (font.width(candidate) > maxWidth && !current.isEmpty()) {
				result.add(current.toString());
				current = new StringBuilder().append(c);
			} else {
				current.append(c);
			}
		}
		if (!current.isEmpty())
			result.add(current.toString());
		return result;
	}

	public static final class Layout {
		public final List<String> lines;
		public final boolean hasTitle;
		public final int innerW;
		public final int blockHeight;

		Layout(List<String> lines, boolean hasTitle, int innerW, int blockHeight) {
			this.lines = lines;
			this.hasTitle = hasTitle;
			this.innerW = innerW;
			this.blockHeight = blockHeight;
		}
	}

	private static class Entry {
		final String title;
		final String text;
		final int titleColor;
		final int messageColor;
		final boolean fromServer;
		final long createdAtNanos = System.nanoTime();

		Entry(String title, String text, int titleColor, int messageColor, boolean fromServer) {
			this.title = (title != null && !title.isEmpty()) ? title : null;
			this.text = text;
			this.titleColor = titleColor;
			this.messageColor = messageColor;
			this.fromServer = fromServer;
		}

		@Nullable
		String resolvedTitle() {
			if (title != null) return title;
			return fromServer ? "SERVER MESSAGE:" : null;
		}
	}
}
package net.mcreator.minecraftalphaargmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.Random;

public class NoSignalScreen extends Screen {
    private final Screen parent;

    // Tunable safety settings (lower values = gentler)
    private static final boolean ENABLE_BURSTS = true;      // set false to remove bursts entirely
    private static final float SPECKLE_CHANCE = 0.01f;      // 1% chance per small grain
    private static final int GRAIN_SIZE = 4;                // small grains for fine noise
    private static final float BURST_PROBABILITY = 0.003f; // ~0.3% chance per frame for a dim burst
    private static final int MAX_PULSE_ALPHA = 0x18;       // max extra alpha for slow pulse (keeps contrast low)

    // Glitch line tuning
    private static final float VERTICAL_GLITCH_CHANCE = 0.008f; // chance per column
    private static final int VERTICAL_GLITCH_WIDTH = 2;        // width in pixels
    private static final int VERTICAL_GLITCH_SEGMENT_MIN = 6;  // min segment height
    private static final int VERTICAL_GLITCH_SEGMENT_MAX = 28; // max segment height
    private static final float HORIZONTAL_GLITCH_CHANCE = 0.01f; // chance per horizontal line
    private static final int HORIZONTAL_SEGMENT_MIN = 8;
    private static final int HORIZONTAL_SEGMENT_MAX = 40;

    public NoSignalScreen(Screen parent) {
        super(Component.literal("NO SIGNAL"));
        this.parent = parent;
    }

    /**
     * TRIGGER: Call this from any procedure or code block.
     * net.mcreator.minecraftalphaargmod.NoSignalScreen.trigger();
     */
    public static void trigger() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.execute(() -> mc.setScreen(new NoSignalScreen(mc.screen)));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        long timeSeed = getTimeSeed();
        Random seeded = new Random(timeSeed ^ 0x9E3779B97F4A7C15L);

        // 1. Base dark background (very dark, not black)
        guiGraphics.fill(0, 0, this.width, this.height, 0xFF050505);

        // 2. Slow, low-contrast pulse (no sudden bright flashes)
        double pulse = Math.abs(Math.sin(timeSeed * 0.015)); // slow oscillation
        int pulseAlpha = (int) (pulse * MAX_PULSE_ALPHA);    // 0 .. MAX_PULSE_ALPHA
        int pulseGrey = 0x10; // keep base grey low
        int pulseColor = (pulseAlpha << 24) | (pulseGrey << 16) | (pulseGrey << 8) | pulseGrey;
        guiGraphics.fill(0, 0, this.width, this.height, pulseColor);

        // 3. Fine static speckles (very subtle, small grains)
        for (int x = 0; x < this.width; x += GRAIN_SIZE) {
            for (int y = 0; y < this.height; y += GRAIN_SIZE) {
                if (seeded.nextFloat() < SPECKLE_CHANCE) {
                    int g = 0x30 + seeded.nextInt(0x30); // mid-dark speckle
                    int a = 0x10 + seeded.nextInt(0x20); // low alpha
                    int col = (a << 24) | (g << 16) | (g << 8) | g;
                    guiGraphics.fill(x, y, Math.min(this.width, x + GRAIN_SIZE), Math.min(this.height, y + GRAIN_SIZE), col);
                }
            }
        }

        // 4. Very rare, dim artifact bursts (muted color, small)
        if (ENABLE_BURSTS && seeded.nextFloat() < BURST_PROBABILITY) {
            int burstW = 40 + seeded.nextInt(120);
            int burstH = 20 + seeded.nextInt(80);
            int bx = seeded.nextInt(Math.max(1, this.width - burstW));
            int by = seeded.nextInt(Math.max(1, this.height - burstH));
            for (int x = bx; x < bx + burstW; x += 6) {
                for (int y = by; y < by + burstH; y += 6) {
                    if (seeded.nextFloat() < 0.5f) {
                        int v = 0x28 + seeded.nextInt(0x40);
                        int a = 0x20 + seeded.nextInt(0x20);
                        int r = seeded.nextBoolean() ? (v / 2) : v;
                        int g = v / 2;
                        int b = seeded.nextBoolean() ? v : (v / 2);
                        int col = (a << 24) | (r << 16) | (g << 8) | b;
                        guiGraphics.fill(x, y, Math.min(this.width, x + 6), Math.min(this.height, y + 6), col);
                    }
                }
            }
        }

        // 5. Subtle scanlines (thin, low alpha)
        int scanlineAlpha = 0x06; // very subtle
        int scanlineColor = (scanlineAlpha << 24) | 0x000000;
        for (int y = 0; y < this.height; y += 3) {
            guiGraphics.fill(0, y, this.width, y + 1, scanlineColor);
        }

        // 6. Gentle vignette (soften edges)
        int vignetteAlpha = 0x20;
        guiGraphics.fill(0, 0, this.width, this.height / 10, (vignetteAlpha << 24) | 0x000000); // top
        guiGraphics.fill(0, this.height - this.height / 10, this.width, this.height, (vignetteAlpha << 24) | 0x000000); // bottom
        guiGraphics.fill(0, 0, this.width / 14, this.height, (vignetteAlpha << 24) | 0x000000); // left
        guiGraphics.fill(this.width - this.width / 14, 0, this.width, this.height, (vignetteAlpha << 24) | 0x000000); // right

        // 7. Thin horizontal artifact line (rare and dim)
        if (seeded.nextFloat() > 0.96f) {
            int lineY = seeded.nextInt(this.height);
            int lineHeight = 1 + seeded.nextInt(2);
            int lineColor = (0x22 << 24) | (0xCC << 16) | (0xCC << 8) | 0xCC;
            guiGraphics.fill(0, lineY, this.width, lineY + lineHeight, lineColor);
        }

        // 8. Glitch lines: vertical columns and broken horizontal segments
        // Vertical glitch columns (animated by timeSeed)
        int columns = Math.max(8, this.width / 40);
        int colStep = Math.max(1, this.width / columns);
        for (int cx = 0; cx < this.width; cx += colStep) {
            if (seeded.nextFloat() < VERTICAL_GLITCH_CHANCE) {
                int gx = cx + seeded.nextInt(Math.max(1, colStep - VERTICAL_GLITCH_WIDTH));
                int segCount = 2 + seeded.nextInt(4);
                for (int s = 0; s < segCount; s++) {
                    int segH = VERTICAL_GLITCH_SEGMENT_MIN + seeded.nextInt(VERTICAL_GLITCH_SEGMENT_MAX - VERTICAL_GLITCH_SEGMENT_MIN + 1);
                    int sy = seeded.nextInt(Math.max(1, this.height - segH));
                    // low alpha, slightly tinted grey-blue
                    int a = 0x18 + seeded.nextInt(0x10);
                    int r = 0x40;
                    int g = 0x48;
                    int b = 0x58;
                    int col = (a << 24) | (r << 16) | (g << 8) | b;
                    guiGraphics.fill(gx, sy, Math.min(this.width, gx + VERTICAL_GLITCH_WIDTH), Math.min(this.height, sy + segH), col);
                }
            }
        }

        // Broken horizontal glitch segments (short segments with gaps)
        for (int hy = 0; hy < this.height; hy += 12) {
            if (seeded.nextFloat() < HORIZONTAL_GLITCH_CHANCE) {
                int x = 0;
                while (x < this.width) {
                    int segLen = HORIZONTAL_SEGMENT_MIN + seeded.nextInt(HORIZONTAL_SEGMENT_MAX - HORIZONTAL_SEGMENT_MIN + 1);
                    int gap = 6 + seeded.nextInt(20);
                    int segH = 1 + seeded.nextInt(2);
                    // subtle tint and low alpha
                    int a = 0x14 + seeded.nextInt(0x10);
                    int tint = 0xB0; // desaturated light grey tint
                    int col = (a << 24) | (tint << 16) | (tint << 8) | tint;
                    guiGraphics.fill(x, hy, Math.min(this.width, x + segLen), Math.min(this.height, hy + segH), col);
                    x += segLen + gap;
                }
            }
        }

        // 9. Text overlays with low contrast and gentle chromatic shadow
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        String main = "NO SIGNAL";
        String sub = "INTERFERENCE DETECTED";

        // gentle jitter (very small)
        int jitterX = (int) ((Math.sin(timeSeed * 0.08) + Math.cos(timeSeed * 0.06)) * 0.8);
        int jitterY = (int) ((Math.cos(timeSeed * 0.07) + Math.sin(timeSeed * 0.04)) * 0.6);

        // chromatic shadows but dim and desaturated
        guiGraphics.drawCenteredString(this.font, main, centerX + jitterX - 1, centerY - 12 + jitterY, 0xFF8A4A4A); // muted red shadow
        guiGraphics.drawCenteredString(this.font, main, centerX + jitterX + 1, centerY - 12 + jitterY, 0xFF4A6A8A); // muted blue shadow
        guiGraphics.drawCenteredString(this.font, main, centerX + jitterX, centerY - 12 + jitterY, 0xFFDDDDDD); // soft off-white

        guiGraphics.drawCenteredString(this.font, sub, centerX, centerY + 6 + jitterY, 0xFF9A9A9A);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.minecraft.setScreen(parent);
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // Helper: produce a time-based seed (uses world game time if available)
    private long getTimeSeed() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            return mc.level.getGameTime();
        } else {
            return System.currentTimeMillis() / 50L;
        }
    }
}

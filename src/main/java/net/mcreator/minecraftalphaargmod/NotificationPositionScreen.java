package net.mcreator.minecraftalphaargmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraftforge.fml.loading.FMLPaths;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NotificationPositionScreen extends Screen {

    private static final int FOOTER_H = 32;

    private static final int COL_VIGNETTE = 0xE60A0D14;
    private static final int COL_DOT_GRID = 0x10FFFFFF;
    private static final int COL_DOT_HIGHLIGHT = 0x40FFFFFF;
    private static final int COL_SNAP_LINE = 0x8000E5FF;

    private static final int COL_FOOTER_BG = 0xFF0A0D14;
    private static final int COL_FOOTER_LINE = 0xFF1F2937;
    private static final int COL_FOOTER_ACCENT = 0xFF00E5FF;
    private static final int COL_TOOLBAR_BG = 0xE6121721;

    private static final int COL_PANEL_TOP = 0xF2121721;
    private static final int COL_PANEL_BOT = 0xF00A0D14;
    private static final int COL_PANEL_BORDER_IDLE = 0xFF2D3748;
    private static final int COL_PANEL_BORDER_ACTIVE = 0xFF00E5FF;

    private static final int COL_TITLE = 0xFFE5E7EB;
    private static final int COL_SUBTITLE = 0xFF6B7280;
    private static final int COL_COORDS = 0xFF9CA3AF;
    private static final int COL_COORDS_ACTIVE = 0xFF00E5FF;

    private static final int COL_SERVER_HEADER = 0xFF00E5FF;
    private static final int COL_SAMPLE_TEXT = 0xFFE5E7EB;

    private int panelX, panelY;
    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;
    private boolean snapCenterX = false;
    private boolean snapCenterY = false;

    private float openProgress = 0f;
    private int tickCount = 0;

    private float smoothPanelX, smoothPanelY;

    private final int[] rippleX = new int[3];
    private final int[] rippleY = new int[3];
    private final int[] rippleAge = new int[3];
    private static final int RIPPLE_MAX_AGE = 10;

    private static final class PreviewSample {
        final String title;
        final String message;
        PreviewSample(String title, String message) {
            this.title = title;
            this.message = message;
        }
    }

    private final PreviewSample[] previewSamples = {
        new PreviewSample("SERVER MESSAGE:", "Login servers will be closed later today."),
        new PreviewSample(null, "Entering HIGH ACTIVITY AREA! You may not break or place blocks here."),
        new PreviewSample(null, "Quest completed!"),
        new PreviewSample("ALERT", "Danger: mobs nearby"),
    };
    private int currentMsgIndex = 0;
    private int previewTimer = 0;

    private static final int UNDO_MAX = 10;
    private final int[] undoX = new int[UNDO_MAX];
    private final int[] undoY = new int[UNDO_MAX];
    private final float[] undoScale = new float[UNDO_MAX];
    private int undoIndex = -1;
    private int undoCount = 0;

    private int nudgeIdleTicks = 0;

    private static final Path TUTORIAL_FILE = FMLPaths.CONFIGDIR.get().resolve("argmod_notification_tutorial.txt");
    private boolean tutorialDone = false;

    public NotificationPositionScreen() {
        super(Component.literal("Notification Position"));
    }

    private NotificationOverlay.Layout currentLayout() {
        PreviewSample sample = previewSamples[currentMsgIndex];
        // Fixed cap so the preview box stays the same size regardless of where on screen it
        // is dragged. The live overlay uses a dynamic cap derived from the anchor position;
        // applying that same cap here would make the preview shrink and re-wrap as the user
        // moves it toward the right edge, which makes the tool unusable.
        return NotificationOverlay.computeLayout(this.font, sample.title, sample.message, NotificationOverlay.MAX_INNER_W);
    }

    private int getPanelW() {
        NotificationOverlay.Layout layout = currentLayout();
        return (int) ((layout.innerW + NotificationOverlay.PAD_X * 2) * NotificationOverlay.getScale());
    }

    private int getPanelH() {
        NotificationOverlay.Layout layout = currentLayout();
        return (int) ((layout.blockHeight + NotificationOverlay.PAD_Y * 2) * NotificationOverlay.getScale());
    }

    @Override
    protected void init() {
        restorePanelFromAnchor();

        smoothPanelX = panelX;
        smoothPanelY = panelY;
        openProgress = 0f;
        tickCount = 0;

        if (this.minecraft.player != null) {
            tutorialDone = hasPlayerDoneTutorial(this.minecraft.player.getUUID().toString());
        }

        int btnY = this.height - 26;
        int cx = this.width / 2;

        addRenderableWidget(Button.builder(Component.literal("Reset"), btn -> {
            pushUndo();
            NotificationOverlay.setScale(1.0f);
            panelX = clampX(this.width / 2 - getPanelW() / 2);
            panelY = clampY(this.height / 2 - getPanelH() / 2);
            smoothPanelX = panelX;
            smoothPanelY = panelY;
            saveAnchor();
        }).pos(cx - 156, btnY).size(104, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Scale -"), btn -> {
            pushUndo();
            changeScalePreservingCenter(Math.max(0.5f, NotificationOverlay.getScale() - 0.1f));
        }).pos(cx - 48, btnY).size(46, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Scale +"), btn -> {
            pushUndo();
            changeScalePreservingCenter(Math.min(2.0f, NotificationOverlay.getScale() + 0.1f));
        }).pos(cx + 2, btnY).size(46, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Done"), btn -> {
            playClick();
            saveAnchor();
            this.minecraft.setScreen(null);
        }).pos(cx + 52, btnY).size(104, 20).build());
    }

    private void changeScalePreservingCenter(float newScale) {
        float centerX = panelX + getPanelW() / 2f;
        float centerY = panelY + getPanelH() / 2f;
        NotificationOverlay.setScale(newScale);
        panelX = clampX(Math.round(centerX - getPanelW() / 2f));
        panelY = clampY(Math.round(centerY - getPanelH() / 2f));
        smoothPanelX = panelX;
        smoothPanelY = panelY;
        saveAnchor();
    }

    private void restorePanelFromAnchor() {
        int hotbarBaseline = this.height - NotificationOverlay.HOTBAR_HEIGHT;
        panelX = clampX(NotificationOverlay.getAnchorX());
        panelY = clampY(hotbarBaseline - NotificationOverlay.getAnchorY());
    }

    @Override
    public void onClose() {
        saveAnchor();
        super.onClose();
    }

    private void saveAnchor() {
        int hotbarBaseline = this.height - NotificationOverlay.HOTBAR_HEIGHT;
        NotificationOverlay.setAnchor(panelX, hotbarBaseline - panelY);
        NotificationOverlay.saveConfig();
    }

    private void playClick() {
        Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    private void pushUndo() {
        undoIndex = (undoIndex + 1) % UNDO_MAX;
        undoX[undoIndex] = panelX;
        undoY[undoIndex] = panelY;
        undoScale[undoIndex] = NotificationOverlay.getScale();
        if (undoCount < UNDO_MAX)
            undoCount++;
    }

    private boolean popUndo() {
        if (undoCount == 0)
            return false;

        NotificationOverlay.setScale(undoScale[undoIndex]);
        panelX = undoX[undoIndex];
        panelY = undoY[undoIndex];
        undoIndex = (undoIndex - 1 + UNDO_MAX) % UNDO_MAX;
        undoCount--;
        smoothPanelX = panelX;
        smoothPanelY = panelY;
        saveAnchor();
        return true;
    }

    @Override
    public void tick() {
        if (openProgress < 1f) {
            openProgress = Math.min(1f, openProgress + 0.1f);
        }

        if (nudgeIdleTicks > 0) {
            nudgeIdleTicks--;
        }

        tickCount++;
        for (int i = 0; i < rippleAge.length; i++) {
            if (rippleAge[i] > 0)
                rippleAge[i]--;
        }
        if (++previewTimer >= 60) {
            previewTimer = 0;
            currentMsgIndex = (currentMsgIndex + 1) % previewSamples.length;
            if (!dragging) {
                restorePanelFromAnchor();
            }
        }
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        int W = this.width;
        int H = this.height;
        int footerTop = H - FOOTER_H;
        int panelW = getPanelW();
        int panelH = getPanelH();
        float s = NotificationOverlay.getScale();
        float alpha = Mth.clamp(openProgress, 0f, 1f);
        float time = tickCount / 20f + partialTick;

        if (!dragging) {
            smoothPanelX = Mth.lerp(0.4f, smoothPanelX, panelX);
            smoothPanelY = Mth.lerp(0.4f, smoothPanelY, panelY);
        } else {
            smoothPanelX = panelX;
            smoothPanelY = panelY;
        }

        int spx = (int) smoothPanelX;
        int spy = (int) smoothPanelY;
        boolean hovered = mouseX >= spx && mouseX <= spx + panelW && mouseY >= spy && mouseY <= spy + panelH;
        boolean active = hovered || dragging;

        gfx.fill(0, 0, W, footerTop, COL_VIGNETTE);
        drawDotGrid(gfx, 0, footerTop, active, spx, spy, panelW, panelH);

        if (dragging && snapCenterX) {
            gfx.fill(W / 2, 0, W / 2 + 1, footerTop, COL_SNAP_LINE);
        }
        if (dragging && snapCenterY) {
            gfx.fill(0, H / 2, W, H / 2 + 1, COL_SNAP_LINE);
        }

        gfx.fill(0, footerTop, W, H, COL_FOOTER_BG);
        gfx.fill(0, footerTop, W, footerTop + 1, COL_FOOTER_ACCENT);
        gfx.fill(0, footerTop + 1, W, footerTop + 2, COL_FOOTER_LINE);

        int textY = this.height - 21;
        gfx.drawString(this.font, "NOTIFICATION POSITION", 12, textY, COL_TITLE, false);
        String infoStr = String.format("X: %d  Y: %d  |  %.1f\u00d7", NotificationOverlay.getAnchorX(), NotificationOverlay.getAnchorY(), NotificationOverlay.getScale());
        gfx.drawString(this.font, infoStr, W - this.font.width(infoStr) - 12, textY, active ? COL_COORDS_ACTIVE : COL_COORDS, false);

        float entranceScale = Mth.lerp(openProgress * openProgress * (3 - 2 * openProgress), 0.8f, 1.0f);

        int btnY = this.height - 26;
        int tbW = 312;
        int tbX = W / 2 - tbW / 2;
        drawRoundedRect(gfx, tbX - 6, btnY - 4, tbW + 12, 28, COL_TOOLBAR_BG);
        drawRoundedBorder(gfx, tbX - 6, btnY - 4, tbW + 12, 28, COL_FOOTER_LINE);

        for (int i = 1; i <= 4; i++) {
            gfx.fill(spx + i, spy + i, spx + panelW + i, spy + panelH + i, 0x15000000);
        }

        gfx.pose().pushPose();
        gfx.pose().translate(smoothPanelX + panelW / 2f, smoothPanelY + panelH / 2f, 0);
        gfx.pose().scale(entranceScale, entranceScale, 1.0f);
        gfx.pose().translate(-panelW / 2f, -panelH / 2f, 0);

        gfx.fillGradient(0, 0, panelW, panelH, COL_PANEL_TOP, COL_PANEL_BOT);

        int borderColor = active ? COL_PANEL_BORDER_ACTIVE : COL_PANEL_BORDER_IDLE;
        drawRect1px(gfx, 0, 0, panelW, panelH, borderColor);

        NotificationOverlay.Layout layout = currentLayout();
        gfx.pose().pushPose();
        gfx.pose().scale(s, s, 1.0f);
        for (int i = 0; i < layout.lines.size(); i++) {
            boolean isTitleLine = layout.hasTitle && i == 0;
            String line = layout.lines.get(i);
            int lineY = NotificationOverlay.PAD_Y + NotificationOverlay.lineYOffset(layout.hasTitle, i);
            int lineX = NotificationOverlay.PAD_X;
            if (!layout.hasTitle) {
                lineX = NotificationOverlay.PAD_X + Math.max(0, (layout.innerW - this.font.width(line)) / 2);
            }
            gfx.drawString(this.font, line, lineX, lineY, isTitleLine ? COL_SERVER_HEADER : COL_SAMPLE_TEXT, true);
        }
        gfx.pose().popPose();

        gfx.pose().popPose();

        for (int i = 0; i < rippleAge.length; i++) {
            if (rippleAge[i] > 0) {
                float progress = 1f - rippleAge[i] / (float) RIPPLE_MAX_AGE;
                float size = 2 + progress * 18;
                int rippleAlpha = ((int) ((1 - progress) * 0x80 * alpha)) & 0xFF;
                int rippleCol = (rippleAlpha << 24) | 0x0000E5FF;
                drawRoundedRect(gfx, (int) (rippleX[i] - size / 2), (int) (rippleY[i] - size / 2), (int) size, (int) size, rippleCol);
            }
        }

        if (!tutorialDone) {
            String hint = "Drag me to reposition";
            int hintW = this.font.width(hint) + 12;
            int hintH = 18;
            int hintX = spx + panelW / 2 - hintW / 2;
            int hintY = spy + panelH + 10;
            if (hintY + hintH > footerTop - 4) {
                hintY = spy - hintH - 8;
            }

            float breath = Mth.sin(time * 3f) * 0.02f + 1.0f;
            float pivotX = hintX + hintW / 2f;
            float pivotY = hintY + hintH / 2f;

            gfx.pose().pushPose();
            gfx.pose().translate(pivotX, pivotY, 0);
            gfx.pose().scale(breath, breath, 1.0f);
            gfx.pose().translate(-hintW / 2f, -hintH / 2f, 0);

            drawRoundedRect(gfx, 0, 0, hintW, hintH, 0xCC11151C);
            drawRoundedBorder(gfx, 0, 0, hintW, hintH, active ? COL_PANEL_BORDER_ACTIVE : COL_FOOTER_LINE);
            gfx.drawString(this.font, hint, 6, 5, active ? COL_COORDS_ACTIVE : COL_SUBTITLE, false);
            gfx.pose().popPose();
        }

        super.render(gfx, mouseX, mouseY, partialTick);
    }

    private void updateSnapping() {
        int pw = getPanelW();
        int ph = getPanelH();

        int targetCenterX = this.width / 2 - pw / 2;
        snapCenterX = Math.abs(panelX - targetCenterX) < 5;
        if (snapCenterX)
            panelX = targetCenterX;

        int targetCenterY = clampY(this.height / 2 - ph / 2);
        snapCenterY = Math.abs(panelY - targetCenterY) < 5;
        if (snapCenterY)
            panelY = targetCenterY;
    }

    private void drawDotGrid(GuiGraphics gfx, int top, int bottom, boolean active, int spx, int spy, int pw, int ph) {
        if (openProgress < 1f)
            return;
        int spacing = 24;
        if (!active) {
            for (int x = spacing / 2; x < this.width; x += spacing) {
                for (int y = top + spacing / 2; y < bottom; y += spacing) {
                    gfx.fill(x, y, x + 1, y + 1, COL_DOT_GRID);
                }
            }
        } else {
            for (int x = spacing / 2; x < this.width; x += spacing) {
                boolean nearX = Math.abs(x - spx) < 4 || Math.abs(x - (spx + pw)) < 4;
                for (int y = top + spacing / 2; y < bottom; y += spacing) {
                    boolean near = nearX || Math.abs(y - spy) < 4 || Math.abs(y - (spy + ph)) < 4;
                    gfx.fill(x, y, x + 1, y + 1, near ? COL_DOT_HIGHLIGHT : COL_DOT_GRID);
                }
            }
        }
    }

    private static void drawRoundedRect(GuiGraphics gfx, int x, int y, int w, int h, int col) {
        if (w <= 0 || h <= 0)
            return;
        if (w < 4 || h < 4) {
            gfx.fill(x, y, x + w, y + h, col);
            return;
        }
        gfx.fill(x + 2, y, x + w - 2, y + 1, col);
        gfx.fill(x + 1, y + 1, x + w - 1, y + 2, col);
        gfx.fill(x, y + 2, x + w, y + h - 2, col);
        gfx.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, col);
        gfx.fill(x + 2, y + h - 1, x + w - 2, y + h, col);
    }

    private static void drawRoundedBorder(GuiGraphics gfx, int x, int y, int w, int h, int col) {
        if (w < 4 || h < 4)
            return;
        gfx.fill(x + 2, y, x + w - 2, y + 1, col);
        gfx.fill(x + 1, y + 1, x + w - 1, y + 2, col);
        gfx.fill(x, y + 2, x + 1, y + h - 2, col);
        gfx.fill(x + w - 1, y + 2, x + w, y + h - 2, col);
        gfx.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, col);
        gfx.fill(x + 2, y + h - 1, x + w - 2, y + h, col);
    }

    private static void drawRect1px(GuiGraphics gfx, int x, int y, int w, int h, int col) {
        gfx.fill(x, y, x + w, y + 1, col);
        gfx.fill(x, y + h - 1, x + w, y + h, col);
        gfx.fill(x, y, x + 1, y + h, col);
        gfx.fill(x + w - 1, y, x + w, y + h, col);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (openProgress < 1f)
            return super.mouseClicked(mx, my, button);

        int px = panelX;
        int py = panelY;
        int pw = getPanelW();
        int ph = getPanelH();

        if (button == 0 && mx >= px && mx <= px + pw && my >= py && my <= py + ph) {
            pushUndo();
            dragging = true;
            dragOffsetX = (int) mx - panelX;
            dragOffsetY = (int) my - panelY;

            smoothPanelX = panelX;
            smoothPanelY = panelY;

            playClick();
            for (int i = 0; i < rippleAge.length; i++) {
                rippleX[i] = (int) mx;
                rippleY[i] = (int) my;
                rippleAge[i] = RIPPLE_MAX_AGE - i * 3;
            }
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (button == 0 && dragging) {
            dragging = false;
            updateSnapping();
            saveAnchor();
            smoothPanelX = panelX;
            smoothPanelY = panelY;

            if (!tutorialDone && this.minecraft.player != null) {
                setPlayerDoneTutorial(this.minecraft.player.getUUID().toString());
                tutorialDone = true;
            }
            return true;
        }
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (dragging && button == 0) {
            panelX = clampX((int) mx - dragOffsetX);
            panelY = clampY((int) my - dragOffsetY);
            updateSnapping();
            return true;
        }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean ctrlHeld = (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;
        boolean shiftHeld = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;
        if (keyCode == GLFW.GLFW_KEY_Z && ctrlHeld && !shiftHeld) {
            if (popUndo()) {
                playClick();
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        boolean isDirectional = (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_DOWN);

        if (isDirectional) {
            if (nudgeIdleTicks <= 0) {
                pushUndo();
            }
            nudgeIdleTicks = 2;

            int nudge = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0 ? 10 : 1;
            if (keyCode == GLFW.GLFW_KEY_LEFT)
                panelX = clampX(panelX - nudge);
            else if (keyCode == GLFW.GLFW_KEY_RIGHT)
                panelX = clampX(panelX + nudge);
            else if (keyCode == GLFW.GLFW_KEY_UP)
                panelY = clampY(panelY - nudge);
            else if (keyCode == GLFW.GLFW_KEY_DOWN)
                panelY = clampY(panelY + nudge);

            updateSnapping();
            saveAnchor();

            if (!tutorialDone && this.minecraft.player != null) {
                setPlayerDoneTutorial(this.minecraft.player.getUUID().toString());
                tutorialDone = true;
            }
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        if (delta != 0) {
            pushUndo();
            float newScale = delta > 0
                ? Math.min(2.0f, NotificationOverlay.getScale() + 0.1f)
                : Math.max(0.5f, NotificationOverlay.getScale() - 0.1f);
            changeScalePreservingCenter(newScale);
            return true;
        }
        return super.mouseScrolled(mx, my, delta);
    }

    private int clampX(int x) {
        return Math.max(0, Math.min(x, this.width - getPanelW()));
    }

    private int clampY(int y) {
        return Math.max(0, Math.min(y, this.height - FOOTER_H - getPanelH()));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static boolean hasPlayerDoneTutorial(String uuid) {
        try {
            if (Files.exists(TUTORIAL_FILE)) {
                return Files.readAllLines(TUTORIAL_FILE).contains(uuid);
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private static void setPlayerDoneTutorial(String uuid) {
        try {
            List<String> list = new ArrayList<>();
            if (Files.exists(TUTORIAL_FILE)) {
                list.addAll(Files.readAllLines(TUTORIAL_FILE));
            }
            if (!list.contains(uuid)) {
                list.add(uuid);
                Files.write(TUTORIAL_FILE, list);
            }
        } catch (Exception ignored) {
        }
    }
}
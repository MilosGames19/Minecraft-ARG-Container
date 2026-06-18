package net.mcreator.minecraftalphaargmod.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NotificationPositionScreen extends Screen {

	private static final int HEADER_H = 22;
	private static final int HOTBAR_H = 24;

	private static final int COL_VIGNETTE = 0xB8000000;
	private static final int COL_HOTBAR_BG = 0xDD0A0A0A;
	private static final int COL_HOTBAR_LABEL = 0xFF555555;
	private static final int COL_HOTBAR_LINE = 0xFF333333;

	private static final int COL_PANEL_BG_TOP = 0xD4101010;
	private static final int COL_PANEL_BG_BOT = 0xCC080808;
	private static final int COL_PANEL_GLOW = 0xFFFFAA00;
	private static final int COL_PANEL_IDLE = 0xFF383838;

	private static final int COL_HEADER_BG = 0xF0050505;
	private static final int COL_HEADER_LINE = 0xFF1E1E1E;
	private static final int COL_TITLE = 0xFFCCCCCC;
	private static final int COL_COORDS = 0xFF666666;
	private static final int COL_COORDS_ACTIVE = 0xFFFFAA00;

	private static final int COL_HINT_IDLE = 0xFF555555;
	private static final int COL_HINT_ACTIVE = 0xFFFFCC44;
	private static final int COL_SERVER_HEADER = 0xFFFF6600;
	private static final int COL_SAMPLE_TEXT = 0xFFEEEEEE;

	private int panelX, panelY;
	private boolean dragging = false;
	private int dragOffsetX, dragOffsetY;

	public NotificationPositionScreen() {
		super(Component.literal("Notification Position"));
	}

	private int getPanelW() {
		return (int) ((NotificationOverlay.BASE_INNER_W + NotificationOverlay.PAD_X * 2) * NotificationOverlay.getScale());
	}

	private int getPanelH() {
		int sampleBlockH = NotificationOverlay.LINE_HEIGHT_BASE + NotificationOverlay.LINE_HEIGHT_STEP;
		return (int) ((sampleBlockH + NotificationOverlay.PAD_Y * 2) * NotificationOverlay.getScale());
	}

	@Override
	protected void init() {
		restorePanelFromAnchor();

		int btnY = this.height - HOTBAR_H - 26;
		int cx = this.width / 2;

		addRenderableWidget(Button.builder(Component.literal("Reset Default"), btn -> {
			NotificationOverlay.setAnchor(20, 30);
			NotificationOverlay.setScale(1.0f);
			restorePanelFromAnchor();
		}).pos(cx - 156, btnY).size(104, 20).build());

		addRenderableWidget(Button.builder(Component.literal("Scale -"), btn -> {
			NotificationOverlay.setScale(NotificationOverlay.getScale() - 0.1f);
			restorePanelFromAnchor();
		}).pos(cx - 48, btnY).size(46, 20).build());

		addRenderableWidget(Button.builder(Component.literal("Scale +"), btn -> {
			NotificationOverlay.setScale(NotificationOverlay.getScale() + 0.1f);
			restorePanelFromAnchor();
		}).pos(cx + 2, btnY).size(46, 20).build());

		addRenderableWidget(Button.builder(Component.literal("Save & Close"), btn -> this.onClose()).pos(cx + 52, btnY).size(104, 20).build());
	}

	private void restorePanelFromAnchor() {
		int hotbarBaseline = this.height - NotificationOverlay.HOTBAR_HEIGHT;
		panelX = NotificationOverlay.getAnchorX();
		panelY = hotbarBaseline - NotificationOverlay.getAnchorY() - getPanelH();
		panelX = clampX(panelX);
		panelY = clampY(panelY);
	}

	@Override
	public void onClose() {
		saveAnchor();
		super.onClose();
	}

	private void saveAnchor() {
		int hotbarBaseline = this.height - NotificationOverlay.HOTBAR_HEIGHT;
		NotificationOverlay.setAnchor(panelX, hotbarBaseline - panelY - getPanelH());
	}

	@Override
	public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
		int W = this.width;
		int H = this.height;
		int hotbarTop = H - HOTBAR_H;
		int panelW = getPanelW();
		int panelH = getPanelH();
		float s = NotificationOverlay.getScale();
		boolean hovered = isHovered(mouseX, mouseY);
		boolean active = hovered || dragging;

		gfx.fill(0, HEADER_H, W, hotbarTop, COL_VIGNETTE);

		gfx.fill(0, hotbarTop, W, H, COL_HOTBAR_BG);
		gfx.fill(0, hotbarTop, W, hotbarTop + 1, COL_HOTBAR_LINE);
		drawCentredString(gfx, "\u25b2  HOTBAR", W / 2, hotbarTop + 7, COL_HOTBAR_LABEL);

		gfx.fill(0, 0, W, HEADER_H, COL_HEADER_BG);
		gfx.fill(0, HEADER_H - 1, W, HEADER_H, COL_HEADER_LINE);

		gfx.drawString(this.font, "NOTIFICATION POSITION", 8, 7, COL_TITLE, false);

		String infoStr = String.format("X %d  Y %d  |  %.1fx", NotificationOverlay.getAnchorX(), NotificationOverlay.getAnchorY(), NotificationOverlay.getScale());
		int infoColor = active ? COL_COORDS_ACTIVE : COL_COORDS;
		gfx.drawString(this.font, infoStr, W - this.font.width(infoStr) - 8, 7, infoColor, false);

		if (active) {
			gfx.fill(panelX - 1, panelY - 1, panelX + panelW + 1, panelY + panelH + 1, COL_PANEL_GLOW);
		}

		int localW = NotificationOverlay.BASE_INNER_W + NotificationOverlay.PAD_X * 2;
		int localH = NotificationOverlay.LINE_HEIGHT_BASE + NotificationOverlay.LINE_HEIGHT_STEP + NotificationOverlay.PAD_Y * 2;
		int px = NotificationOverlay.PAD_X;
		int py = NotificationOverlay.PAD_Y;

		gfx.pose().pushPose();
		gfx.pose().translate(panelX, panelY, 0);
		gfx.pose().scale(s, s, 1.0f);

		gfx.fillGradient(0, 0, localW, localH, COL_PANEL_BG_TOP, COL_PANEL_BG_BOT);
		drawRect1px(gfx, 0, 0, localW, localH, active ? COL_PANEL_GLOW : COL_PANEL_IDLE);
		if (active) {
			gfx.fill(1, 0, localW - 1, 1, COL_PANEL_GLOW);
		}
		gfx.drawString(this.font, "SERVER MESSAGE:", px, py, COL_SERVER_HEADER, false);
		gfx.drawString(this.font, "Sample notification text", px, py + NotificationOverlay.LINE_HEIGHT_BASE, COL_SAMPLE_TEXT, false);

		gfx.pose().popPose();

		String hint = dragging ? "Release to place" : (hovered ? "Drag to reposition" : "Click the preview to move it");
		int hintColor = active ? COL_HINT_ACTIVE : COL_HINT_IDLE;
		int scaledTextH = (int) (10 * s);
		int hintY = panelY + panelH + 5;
		if (hintY + scaledTextH > hotbarTop - 4)
			hintY = panelY - scaledTextH - 3;

		gfx.pose().pushPose();
		gfx.pose().translate(panelX, hintY, 0);
		gfx.pose().scale(s, s, 1.0f);
		gfx.drawString(this.font, hint, 0, 0, hintColor, false);
		gfx.pose().popPose();

		super.render(gfx, mouseX, mouseY, partialTick);
	}

	private static void drawRect1px(GuiGraphics gfx, int x, int y, int w, int h, int col) {
		gfx.fill(x, y, x + w, y + 1, col);
		gfx.fill(x, y + h - 1, x + w, y + h, col);
		gfx.fill(x, y, x + 1, y + h, col);
		gfx.fill(x + w - 1, y, x + w, y + h, col);
	}

	private void drawCentredString(GuiGraphics gfx, String text, int cx, int y, int color) {
		gfx.drawString(this.font, text, cx - this.font.width(text) / 2, y, color, false);
	}

	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		if (button == 0 && isHovered((int) mx, (int) my)) {
			dragging = true;
			dragOffsetX = (int) mx - panelX;
			dragOffsetY = (int) my - panelY;
			return true;
		}
		return super.mouseClicked(mx, my, button);
	}

	@Override
	public boolean mouseReleased(double mx, double my, int button) {
		if (button == 0 && dragging) {
			dragging = false;
			saveAnchor();
			return true;
		}
		return super.mouseReleased(mx, my, button);
	}

	@Override
	public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
		if (dragging && button == 0) {
			panelX = clampX((int) mx - dragOffsetX);
			panelY = clampY((int) my - dragOffsetY);
			return true;
		}
		return super.mouseDragged(mx, my, button, dx, dy);
	}

	private boolean isHovered(int mx, int my) {
		int pw = getPanelW();
		int ph = getPanelH();
		return mx >= panelX && mx <= panelX + pw && my >= panelY && my <= panelY + ph;
	}

	private int clampX(int x) {
		return Math.max(0, Math.min(x, this.width - getPanelW()));
	}

	private int clampY(int y) {
		return Math.max(HEADER_H, Math.min(y, this.height - NotificationOverlay.HOTBAR_HEIGHT - getPanelH()));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
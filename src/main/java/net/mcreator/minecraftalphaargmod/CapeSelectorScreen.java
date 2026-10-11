package net.mcreator.minecraftalphaargmod.capes.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.mcreator.minecraftalphaargmod.capes.CapeCategory;
import net.mcreator.minecraftalphaargmod.capes.network.CapeNetwork;
import net.mcreator.minecraftalphaargmod.capes.network.OpenCapeSelectorPacket;
import net.mcreator.minecraftalphaargmod.capes.network.SelectCapePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.PacketDistributor;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class CapeSelectorScreen extends Screen {
	private static final int SLOT_SIZE = 56;
	private static final int SLOT_PADDING = 8;
	private static final int SIDEBAR_WIDTH = 100;
	private static final int PREVIEW_WIDTH = 170;
	private static final int PANEL_MARGIN = 8;
	private static final int PANEL_TOP = 30;
	private static final int PANEL_BOTTOM_MARGIN = 12;
	private static final double DRAG_SENSITIVITY = 0.75;
	private static final int PANEL_FILL = 0x9F000000;
	private static final int PANEL_EDGE_LIGHT = 0x3AFFFFFF;
	private static final int PANEL_EDGE_DARK = 0x3A000000;

	private static final int SCROLL_STEP = 24;
	private static final int SCROLLBAR_WIDTH = 4;
	private static final int SCROLLBAR_INNER_PADDING = 2;
	private static final int SCROLLBAR_THUMB_MIN_HEIGHT = 20;
	private static final int SCROLLBAR_TRACK_COLOR = 0x40000000;
	private static final int SCROLLBAR_THUMB_COLOR = 0xFF808080;

	private static final int GRID_INNER_PADDING = 10;

	private final List<OpenCapeSelectorPacket.Entry> allCapes;
	private final boolean testMode;

	private String tentativeSelectedId;
	private CapeCategory filter;
	private boolean previewElytra;
	private final List<CapeSlotWidget> slotWidgets = new ArrayList<>();

	private int cachedVisibleCount = -1;
	private CapeCategory cachedVisibleCountFilter = null;
	// private String cachedSelectedName = null;
	private Component cachedSelectedName = null;
	private String cachedSelectedId = null;
	private String cachedSelectedNameForId = null;

	private int columns;

	private int sidebarPanelX0, sidebarPanelY0, sidebarPanelX1, sidebarPanelY1;
	private int gridPanelX0, gridPanelY0, gridPanelX1, gridPanelY1;
	private int previewPanelX0, previewPanelY0, previewPanelX1, previewPanelY1;
	private int sidebarFooterY;

	private int scrollOffset = 0;
	private int maxScroll = 0;
	private boolean draggingScrollbar = false;
	private double scrollbarDragStartY = 0.0;
	private int scrollbarDragStartOffset = 0;

	private Button elytraToggleButton;
	private Button resetRotationButton;

	private boolean draggingPreview;
	private double previewYaw = 0.0;

	public CapeSelectorScreen(OpenCapeSelectorPacket packet) {
		// super(Component.literal("Capes"));
		super(Component.translatable("gui.the_arg_container.capes.title"));
		this.allCapes = packet.getCapes();
		this.tentativeSelectedId = packet.getSelectedCapeId();
		this.testMode = packet.isTestMode();
		this.filter = null;
	}

	@Override
	protected void init() {
		sidebarPanelX0 = 8;
		sidebarPanelY0 = PANEL_TOP;
		sidebarPanelX1 = sidebarPanelX0 + SIDEBAR_WIDTH;
		sidebarPanelY1 = this.height - PANEL_BOTTOM_MARGIN;

		previewPanelX0 = this.width - PREVIEW_WIDTH - 8;
		previewPanelY0 = PANEL_TOP;
		previewPanelX1 = this.width - 8;
		previewPanelY1 = this.height - PANEL_BOTTOM_MARGIN;

		gridPanelX0 = sidebarPanelX1 + PANEL_MARGIN;
		gridPanelY0 = PANEL_TOP;
		gridPanelX1 = previewPanelX0 - PANEL_MARGIN;
		gridPanelY1 = this.height - PANEL_BOTTOM_MARGIN;

		int gridWidth = gridPanelX1 - gridPanelX0 - 20;
		columns = Math.max(1, gridWidth / (SLOT_SIZE + SLOT_PADDING));

		int filterY = sidebarPanelY0 + 10;
		int buttonHeight = 18;
		int buttonSpacing = 24;
		// addRenderableWidget(Button.builder(Component.literal("All"), b ->
		// setFilter(null)).bounds(sidebarPanelX0 + 10, filterY, SIDEBAR_WIDTH - 20,
		// buttonHeight).build());
		addRenderableWidget(Button.builder(Component.translatable("gui.the_arg_container.capes.filter.all"), b -> setFilter(null)).bounds(sidebarPanelX0 + 10, filterY, SIDEBAR_WIDTH - 20, buttonHeight).build());
		filterY += buttonSpacing;
		// addRenderableWidget(Button.builder(Component.literal("Dev"), b ->
		// setFilter(CapeCategory.DEV_DEFAULT)).bounds(sidebarPanelX0 + 10, filterY,
		// SIDEBAR_WIDTH - 20, buttonHeight).build());
		addRenderableWidget(Button.builder(Component.translatable("gui.the_arg_container.capes.filter.dev"), b -> setFilter(CapeCategory.DEV_DEFAULT)).bounds(sidebarPanelX0 + 10, filterY, SIDEBAR_WIDTH - 20, buttonHeight).build());
		filterY += buttonSpacing;
		// addRenderableWidget(Button.builder(Component.literal("Contributor"), b ->
		// setFilter(CapeCategory.CONTRIBUTOR_DEFAULT)).bounds(sidebarPanelX0 + 10,
		// filterY, SIDEBAR_WIDTH - 20, buttonHeight).build());
		addRenderableWidget(Button.builder(Component.translatable("gui.the_arg_container.capes.filter.contributor"), b -> setFilter(CapeCategory.CONTRIBUTOR_DEFAULT)).bounds(sidebarPanelX0 + 10, filterY, SIDEBAR_WIDTH - 20, buttonHeight).build());
		filterY += buttonSpacing;
		// addRenderableWidget(Button.builder(Component.literal("Reviewer"), b ->
		// setFilter(CapeCategory.REVIEWER_DEFAULT)).bounds(sidebarPanelX0 + 10,
		// filterY, SIDEBAR_WIDTH - 20, buttonHeight).build());
		addRenderableWidget(Button.builder(Component.translatable("gui.the_arg_container.capes.filter.reviewer"), b -> setFilter(CapeCategory.REVIEWER_DEFAULT)).bounds(sidebarPanelX0 + 10, filterY, SIDEBAR_WIDTH - 20, buttonHeight).build());
		filterY += buttonSpacing;
		// addRenderableWidget(Button.builder(Component.literal("Personal"), b ->
		// setFilter(CapeCategory.PERSONAL)).bounds(sidebarPanelX0 + 10, filterY,
		// SIDEBAR_WIDTH - 20, buttonHeight).build());
		addRenderableWidget(Button.builder(Component.translatable("gui.the_arg_container.capes.filter.personal"), b -> setFilter(CapeCategory.PERSONAL)).bounds(sidebarPanelX0 + 10, filterY, SIDEBAR_WIDTH - 20, buttonHeight).build());
		filterY += buttonSpacing;
		sidebarFooterY = filterY + 14;

		int previewButtonWidth = (PREVIEW_WIDTH - 30) / 2;
		elytraToggleButton = addRenderableWidget(Button.builder(elytraButtonLabel(), b -> toggleElytraPreview()).bounds(previewPanelX0 + 10, previewPanelY0 + 8, previewButtonWidth, 18).build());
		// resetRotationButton =
		// addRenderableWidget(Button.builder(Component.literal("Reset View"), b ->
		// resetPreviewRotation()).bounds(previewPanelX0 + 10 + previewButtonWidth +
		// 8, previewPanelY0 + 8, previewButtonWidth, 18).build());
		resetRotationButton = addRenderableWidget(
				Button.builder(Component.translatable("gui.the_arg_container.capes.reset_view"), b -> resetPreviewRotation()).bounds(previewPanelX0 + 10 + previewButtonWidth + 8, previewPanelY0 + 8, previewButtonWidth, 18).build());

		// addRenderableWidget(Button.builder(Component.literal("Equip"), b ->
		// confirmSelection()).bounds(previewPanelX0 + 10, this.height - 35,
		// PREVIEW_WIDTH - 20, 20).build());
		addRenderableWidget(Button.builder(Component.translatable("gui.the_arg_container.capes.equip"), b -> confirmSelection()).bounds(previewPanelX0 + 10, this.height - 35, PREVIEW_WIDTH - 20, 20).build());

		if (this.minecraft.player != null) {
			ClientCapeCache.setPreview(this.minecraft.player.getUUID(), resolveTexture(tentativeSelectedId));
		}

		rebuildGrid();
	}

	private void rebuildGrid() {
		slotWidgets.forEach(this::removeWidget);
		slotWidgets.clear();

		List<OpenCapeSelectorPacket.Entry> filtered = new ArrayList<>();
		filtered.add(null);
		for (OpenCapeSelectorPacket.Entry entry : allCapes) {
			if (filter == null || entry.getCategory() == filter) {
				filtered.add(entry);
			}
		}

		int rows = (int) Math.ceil(filtered.size() / (double) columns);
		int itemsInWidestRow = rows > 1 ? columns : filtered.size();
		int contentWidth = itemsInWidestRow * (SLOT_SIZE + SLOT_PADDING) - SLOT_PADDING;
		int contentHeight = rows * (SLOT_SIZE + SLOT_PADDING) - SLOT_PADDING;

		int panelInnerWidth = gridPanelX1 - gridPanelX0;
		int panelInnerHeight = gridPanelY1 - gridPanelY0;

		int scrollbarReserve = (contentHeight > panelInnerHeight) ? SCROLLBAR_WIDTH + 2 * SCROLLBAR_INNER_PADDING : 0;
		int availableWidth = panelInnerWidth - 2 * GRID_INNER_PADDING - scrollbarReserve;
		int fitColumns = Math.max(1, availableWidth / (SLOT_SIZE + SLOT_PADDING));
		if (fitColumns != columns) {
			columns = fitColumns;
			rows = (int) Math.ceil(filtered.size() / (double) columns);
			itemsInWidestRow = rows > 1 ? columns : filtered.size();
			contentWidth = itemsInWidestRow * (SLOT_SIZE + SLOT_PADDING) - SLOT_PADDING;
			contentHeight = rows * (SLOT_SIZE + SLOT_PADDING) - SLOT_PADDING;
		}

		int maxScroll = Math.max(0, contentHeight - panelInnerHeight + 2 * GRID_INNER_PADDING);
		this.maxScroll = maxScroll;
		this.scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

		int startX = gridPanelX0 + GRID_INNER_PADDING + Math.max(0, (availableWidth - contentWidth) / 2);
		int startY;
		if (contentHeight <= panelInnerHeight - 2 * GRID_INNER_PADDING) {
			startY = gridPanelY0 + Math.max(GRID_INNER_PADDING, (panelInnerHeight - contentHeight) / 2);
		} else {
			startY = gridPanelY0 + GRID_INNER_PADDING - scrollOffset;
		}

		int col = 0;
		int row = 0;
		for (OpenCapeSelectorPacket.Entry entry : filtered) {
			int x = startX + col * (SLOT_SIZE + SLOT_PADDING);
			int y = startY + row * (SLOT_SIZE + SLOT_PADDING);
			CapeSlotWidget widget = new CapeSlotWidget(x, y, SLOT_SIZE, entry, this);
			slotWidgets.add(widget);
			addRenderableWidget(widget);

			col++;
			if (col >= columns) {
				col = 0;
				row++;
			}
		}
	}

	private void repositionGridWidgets() {
		if (slotWidgets.isEmpty())
			return;

		int total = slotWidgets.size();
		int rows = (int) Math.ceil(total / (double) columns);
		int contentHeight = rows * (SLOT_SIZE + SLOT_PADDING) - SLOT_PADDING;
		int panelInnerHeight = gridPanelY1 - gridPanelY0;

		int startY;
		if (contentHeight <= panelInnerHeight - 2 * GRID_INNER_PADDING) {
			startY = gridPanelY0 + Math.max(GRID_INNER_PADDING, (panelInnerHeight - contentHeight) / 2);
		} else {
			startY = gridPanelY0 + GRID_INNER_PADDING - scrollOffset;
		}

		for (int i = 0; i < total; i++) {
			int row = i / columns;
			int y = startY + row * (SLOT_SIZE + SLOT_PADDING);
			slotWidgets.get(i).setY(y);
		}
	}

	private void setFilter(CapeCategory category) {
		this.filter = category;
		this.scrollOffset = 0;
		cachedVisibleCount = -1;
		cachedVisibleCountFilter = null;
		rebuildGrid();
	}

	private void toggleElytraPreview() {
		previewElytra = !previewElytra;
		elytraToggleButton.setMessage(elytraButtonLabel());
		if (this.minecraft.player != null) {
			// ClientCapeCache.setPreview(this.minecraft.player.getUUID(),
			//         previewElytra ? null : resolveTexture(tentativeSelectedId));
			ClientCapeCache.setPreview(this.minecraft.player.getUUID(), resolveTexture(tentativeSelectedId));
		}
	}

	private void resetPreviewRotation() {
		previewYaw = 0.0;
	}

	private Component elytraButtonLabel() {
		// return Component.literal("Elytra: " + (previewElytra ? "On" : "Off"));
		return previewElytra ? Component.translatable("gui.the_arg_container.capes.elytra.on") : Component.translatable("gui.the_arg_container.capes.elytra.off");
	}

	public boolean isTentativelySelected(OpenCapeSelectorPacket.Entry entry) {
		if (entry == null) {
			return tentativeSelectedId == null;
		}
		return entry.getId().equals(tentativeSelectedId);
	}

	public void selectTentative(OpenCapeSelectorPacket.Entry entry) {
		this.tentativeSelectedId = entry == null ? null : entry.getId();
		cachedSelectedName = null;
		cachedSelectedId = null;
		cachedSelectedNameForId = null;
		if (this.minecraft.player != null) {
			// ResourceLocation texture = previewElytra ? null
			//         : (entry == null ? null : entry.getTexture());
			ResourceLocation texture = entry == null ? null : entry.getTexture();
			ClientCapeCache.setPreview(this.minecraft.player.getUUID(), texture);
		}
	}

	private void confirmSelection() {
		CapeNetwork.CHANNEL.send(PacketDistributor.SERVER.noArg(), new SelectCapePacket(tentativeSelectedId));
		this.onClose();
	}

	private ResourceLocation resolveTexture(String capeId) {
		if (capeId == null)
			return null;
		for (OpenCapeSelectorPacket.Entry entry : allCapes) {
			if (entry.getId().equals(capeId)) {
				return entry.getTexture();
			}
		}
		return null;
	}

	private int getVisibleCapeCount() {
		if (cachedVisibleCountFilter == filter && cachedVisibleCount >= 0) {
			return cachedVisibleCount;
		}
		int count = 0;
		for (OpenCapeSelectorPacket.Entry entry : allCapes) {
			if (filter == null || entry.getCategory() == filter) {
				count++;
			}
		}
		cachedVisibleCount = count;
		cachedVisibleCountFilter = filter;
		return count;
	}

	// private String getSelectedDisplayName() {
	//         String key = tentativeSelectedId == null ? "" :
	//         tentativeSelectedId; if (cachedSelectedNameForId != null &&
	//         cachedSelectedNameForId.equals(key)) {
	//                 return cachedSelectedName;
	//         }
	//         String name = "None";
	//         for (OpenCapeSelectorPacket.Entry entry : allCapes) {
	//                 if (entry.getId().equals(tentativeSelectedId)) {
	//                         name = entry.getDisplayName();
	//                         break;
	//                 }
	//         }
	//         cachedSelectedName = name;
	//         cachedSelectedId = tentativeSelectedId;
	//         cachedSelectedNameForId = key;
	//         return name;
	// }
	private Component getSelectedDisplayName() {
		String key = tentativeSelectedId == null ? "" : tentativeSelectedId;
		if (cachedSelectedNameForId != null && cachedSelectedNameForId.equals(key)) {
			return cachedSelectedName;
		}
		Component name = Component.translatable("gui.the_arg_container.capes.none");
		for (OpenCapeSelectorPacket.Entry entry : allCapes) {
			if (entry.getId().equals(tentativeSelectedId)) {
				name = Component.translatable(entry.getDisplayName());
				break;
			}
		}
		cachedSelectedName = name;
		cachedSelectedId = tentativeSelectedId;
		cachedSelectedNameForId = key;
		return name;
	}

	private String getSelectedDisplayId() {
		String key = tentativeSelectedId == null ? "" : tentativeSelectedId;
		if (cachedSelectedNameForId != null && cachedSelectedNameForId.equals(key)) {
			return cachedSelectedId;
		}
		getSelectedDisplayName();
		return cachedSelectedId;
	}

	private boolean isInPreviewPanel(double mouseX, double mouseY) {
		return mouseX >= previewPanelX0 && mouseX < previewPanelX1 && mouseY >= previewPanelY0 && mouseY < previewPanelY1;
	}

	private boolean isInGridPanel(double mouseX, double mouseY) {
		return mouseX >= gridPanelX0 && mouseX < gridPanelX1 && mouseY >= gridPanelY0 && mouseY < gridPanelY1;
	}

	private int scrollbarTrackX() {
		return gridPanelX1 - SCROLLBAR_WIDTH - SCROLLBAR_INNER_PADDING;
	}

	private int scrollbarThumbHeight() {
		int panelInnerHeight = gridPanelY1 - gridPanelY0;
		int contentHeight = maxScroll + panelInnerHeight;
		int thumb = (int) ((long) panelInnerHeight * panelInnerHeight / Math.max(1, contentHeight));
		return Math.max(SCROLLBAR_THUMB_MIN_HEIGHT, Math.min(panelInnerHeight, thumb));
	}

	private int scrollbarThumbY() {
		int panelInnerHeight = gridPanelY1 - gridPanelY0;
		int thumbHeight = scrollbarThumbHeight();
		if (maxScroll <= 0)
			return gridPanelY0;
		return gridPanelY0 + (panelInnerHeight - thumbHeight) * scrollOffset / maxScroll;
	}

	private boolean isInScrollbar(double mouseX, double mouseY) {
		if (maxScroll <= 0)
			return false;
		int barX = scrollbarTrackX();
		return mouseX >= barX && mouseX < barX + SCROLLBAR_WIDTH && mouseY >= gridPanelY0 && mouseY < gridPanelY1;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && isInScrollbar(mouseX, mouseY)) {
			draggingScrollbar = true;
			scrollbarDragStartY = mouseY;
			scrollbarDragStartOffset = scrollOffset;
			return true;
		}

		boolean handled = false;
		for (net.minecraft.client.gui.components.events.GuiEventListener listener : this.children()) {
			if (listener instanceof CapeSlotWidget widget) {
				int widgetTop = widget.getY();
				int widgetBottom = widgetTop + widget.getHeight();
				if (widgetBottom <= gridPanelY0 || widgetTop >= gridPanelY1) {
					continue;
				}
			}
			if (listener.mouseClicked(mouseX, mouseY, button)) {
				this.setFocused(listener);
				if (button == 0) {
					this.setDragging(true);
				}
				handled = true;
				break;
			}
		}

		if (!handled && button == 0 && isInPreviewPanel(mouseX, mouseY)) {
			draggingPreview = true;
			return true;
		}
		return handled;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (draggingScrollbar && button == 0) {
			int panelInnerHeight = gridPanelY1 - gridPanelY0;
			int thumbHeight = scrollbarThumbHeight();
			int travel = panelInnerHeight - thumbHeight;
			if (travel > 0) {
				double delta = mouseY - scrollbarDragStartY;
				int newOffset = scrollbarDragStartOffset + (int) (delta * maxScroll / travel);
				scrollOffset = Math.max(0, Math.min(maxScroll, newOffset));
				repositionGridWidgets();
			}
			return true;
		}

		if (draggingPreview && button == 0) {
			previewYaw = (previewYaw - dragX * DRAG_SENSITIVITY) % 360.0;
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) {
			draggingPreview = false;
			draggingScrollbar = false;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (maxScroll > 0 && isInGridPanel(mouseX, mouseY)) {
			int newOffset = scrollOffset - (int) (amount * SCROLL_STEP);
			scrollOffset = Math.max(0, Math.min(maxScroll, newOffset));
			repositionGridWidgets();
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public void onClose() {
		ClientCapeCache.clearPreview();
		super.onClose();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		guiGraphics.fill(0, 0, this.width, this.height, 0xC0000000);
		// guiGraphics.drawString(this.font, "Capes", 10, 10, 0xFFFFFF);
		guiGraphics.drawString(this.font, Component.translatable("gui.the_arg_container.capes.title"), 10, 10, 0xFFFFFF);

		if (testMode) {
			// guiGraphics.drawString(this.font, "Test access", this.width - 90, 10,
			// 0xFFAA00);
			guiGraphics.drawString(this.font, Component.translatable("gui.the_arg_container.capes.test_access"), this.width - 90, 10, 0xFFAA00);
		}

		drawPanel(guiGraphics, sidebarPanelX0, sidebarPanelY0, sidebarPanelX1, sidebarPanelY1);
		drawPanel(guiGraphics, gridPanelX0, gridPanelY0, gridPanelX1, gridPanelY1);
		drawPanel(guiGraphics, previewPanelX0, previewPanelY0, previewPanelX1, previewPanelY1);

		int visibleCapes = getVisibleCapeCount();
		// String countLabel = visibleCapes + (visibleCapes == 1 ? " cape" : "
		// capes"); guiGraphics.drawCenteredString(this.font, countLabel,
		// sidebarPanelX0 + SIDEBAR_WIDTH / 2, sidebarFooterY, 0x808080);
		guiGraphics.drawCenteredString(this.font, Component.translatable("gui.the_arg_container.capes.count", visibleCapes), sidebarPanelX0 + SIDEBAR_WIDTH / 2, sidebarFooterY, 0x808080);

		if (this.minecraft.player != null) {
			int previewX = previewPanelX0 + (previewPanelX1 - previewPanelX0) / 2;
			int previewY = previewPanelY0 + (previewPanelY1 - previewPanelY0) * 2 / 3;
			int previewPanelHeight = previewPanelY1 - previewPanelY0;
			// renderPreviewPlayer(guiGraphics, previewX, previewY, 70);
			int scale = Math.max(30, Math.min(185, previewPanelHeight * 25 / 100));
			// Player preview size
			renderPreviewPlayer(guiGraphics, previewX, previewY, scale);
			// guiGraphics.drawCenteredString(this.font, "Drag to rotate", previewX,
			// previewPanelY1 - 63, 0x808080);
			guiGraphics.drawCenteredString(this.font, Component.translatable("gui.the_arg_container.capes.drag_to_rotate"), previewX, previewPanelY1 - 63, 0x808080);
		}

		for (net.minecraft.client.gui.components.events.GuiEventListener listener : this.children()) {
			if (listener instanceof CapeSlotWidget)
				continue;
			if (listener instanceof net.minecraft.client.gui.components.Renderable renderable) {
				renderable.render(guiGraphics, mouseX, mouseY, partialTick);
			}
		}

		guiGraphics.enableScissor(gridPanelX0, gridPanelY0, gridPanelX1, gridPanelY1);
		for (net.minecraft.client.gui.components.events.GuiEventListener listener : this.children()) {
			if (!(listener instanceof CapeSlotWidget))
				continue;
			if (listener instanceof net.minecraft.client.gui.components.Renderable renderable) {
				renderable.render(guiGraphics, mouseX, mouseY, partialTick);
			}
		}
		guiGraphics.disableScissor();

		if (maxScroll > 0) {
			int trackX = scrollbarTrackX();
			guiGraphics.fill(trackX, gridPanelY0, trackX + SCROLLBAR_WIDTH, gridPanelY1, SCROLLBAR_TRACK_COLOR);
			int thumbY = scrollbarThumbY();
			int thumbHeight = scrollbarThumbHeight();
			guiGraphics.fill(trackX, thumbY, trackX + SCROLLBAR_WIDTH, thumbY + thumbHeight, SCROLLBAR_THUMB_COLOR);
		}

		// String selectedName = getSelectedDisplayName();
		// String selectedId = getSelectedDisplayId();
		// int previewCenterX = previewPanelX0 + (previewPanelX1 - previewPanelX0) /
		// 2; guiGraphics.drawCenteredString(this.font, selectedName,
		// previewCenterX, this.height - 55, 0xFFFFFF);
		Component selectedName = getSelectedDisplayName();
		String selectedId = getSelectedDisplayId();
		int previewCenterX = previewPanelX0 + (previewPanelX1 - previewPanelX0) / 2;
		guiGraphics.drawCenteredString(this.font, selectedName, previewCenterX, this.height - 55, 0xFFFFFF);

		if (testMode && selectedId != null) {
			// guiGraphics.drawCenteredString(this.font, "id: " + selectedId,
			// previewCenterX, this.height - 44, 0xFFAA00);
			guiGraphics.drawCenteredString(this.font, Component.translatable("gui.the_arg_container.capes.id", selectedId), previewCenterX, this.height - 44, 0xFFAA00);
		}
	}

	private void drawPanel(GuiGraphics guiGraphics, int x0, int y0, int x1, int y1) {
		guiGraphics.fill(x0, y0, x1, y1, PANEL_FILL);
		guiGraphics.fill(x0, y0, x1, y0 + 1, PANEL_EDGE_LIGHT);
		guiGraphics.fill(x0, y0, x0 + 1, y1, PANEL_EDGE_LIGHT);
		guiGraphics.fill(x0, y1 - 1, x1, y1, PANEL_EDGE_DARK);
		guiGraphics.fill(x1 - 1, y0, x1, y1, PANEL_EDGE_DARK);
	}

	private void renderPreviewPlayer(GuiGraphics guiGraphics, int x, int y, int scale) {
		Player player = this.minecraft.player;
		if (player == null)
			return;

		// ItemStack savedChest = null;
		// if (previewElytra) {
		//     savedChest = player.getItemBySlot(EquipmentSlot.CHEST);
		//     player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.ELYTRA));
		// }
		ItemStack savedChest = player.getItemBySlot(EquipmentSlot.CHEST);
		boolean needSwap = previewElytra != savedChest.is(Items.ELYTRA);
		if (needSwap) {
			player.setItemSlot(EquipmentSlot.CHEST, previewElytra ? new ItemStack(Items.ELYTRA) : ItemStack.EMPTY);
		}

		try {
			renderMiniPlayer(guiGraphics, x, y, scale, 180.0F + (float) previewYaw, player);
		} finally {
			// if (savedChest != null) {
			//     player.setItemSlot(EquipmentSlot.CHEST, savedChest);
			// }
			if (needSwap) {
				player.setItemSlot(EquipmentSlot.CHEST, savedChest);
			}
		}
	}

	public static void renderMiniPlayer(GuiGraphics guiGraphics, int x, int y, int scale, float yaw, LivingEntity entity) {
		Quaternionf poseRotation = new Quaternionf().rotateZ((float) Math.PI);

		float yBodyRotSaved = entity.yBodyRot;
		float yRotSaved = entity.getYRot();
		float xRotSaved = entity.getXRot();
		float yHeadRotOSaved = entity.yHeadRotO;
		float yHeadRotSaved = entity.yHeadRot;

		entity.yBodyRot = yaw;
		entity.setYRot(yaw);
		entity.setXRot(0);
		entity.yHeadRot = entity.getYRot();
		entity.yHeadRotO = entity.getYRot();

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(x, y, 50.0);
		guiGraphics.pose().mulPoseMatrix(new Matrix4f().scaling(scale, scale, -scale));
		guiGraphics.pose().mulPose(poseRotation);

		Lighting.setupForEntityInInventory();

		EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		dispatcher.setRenderShadow(false);
		RenderSystem.runAsFancy(() -> {
			dispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, guiGraphics.pose(), guiGraphics.bufferSource(), 15728880);
		});
		guiGraphics.flush();
		dispatcher.setRenderShadow(true);

		guiGraphics.pose().popPose();
		Lighting.setupFor3DItems();

		entity.yBodyRot = yBodyRotSaved;
		entity.setYRot(yRotSaved);
		entity.setXRot(xRotSaved);
		entity.yHeadRotO = yHeadRotOSaved;
		entity.yHeadRot = yHeadRotSaved;
	}

	private static PlayerModel<Player> capeOnlyModel;

	private static PlayerModel<Player> getCapeOnlyModel() {
		if (capeOnlyModel == null) {
			capeOnlyModel = new PlayerModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER), false);
		}
		return capeOnlyModel;
	}

	public static void invalidateModelCache() {
		capeOnlyModel = null;
	}

	public static void renderCapeModel(GuiGraphics guiGraphics, int x, int y, int scale, ResourceLocation capeTexture) {
		PlayerModel<Player> model = getCapeOnlyModel();

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(x, y, 50.0);
		guiGraphics.pose().mulPoseMatrix(new Matrix4f().scaling(scale, scale, -scale));

		Quaternionf restingRotation = new Quaternionf().rotateZ((float) Math.PI);
		restingRotation.mul(new Quaternionf().rotateX((float) Math.toRadians(6.0)));
		guiGraphics.pose().mulPose(restingRotation);
		guiGraphics.pose().scale(-1.0F, -1.0F, 1.0F);

		Lighting.setupForEntityInInventory();

		MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entitySolid(capeTexture));
		model.renderCloak(guiGraphics.pose(), vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY);
		bufferSource.endBatch();

		guiGraphics.pose().popPose();
		Lighting.setupFor3DItems();
	}
}

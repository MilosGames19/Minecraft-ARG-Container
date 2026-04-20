package net.mcreator.minecraftalphaargmod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModItems;
import net.mcreator.minecraftalphaargmod.network.CraftSkyBuilderPacket;
import net.mcreator.minecraftalphaargmod.network.SkyBuilderNetwork;
import net.mcreator.minecraftalphaargmod.world.inventory.SkyBuilderGuiMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.SkyBuilderRecipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class SkyBuilderGuiScreen extends AbstractContainerScreen<SkyBuilderGuiMenu> {

	private static final HashMap<String, Object> guistate = SkyBuilderGuiMenu.guistate;

	private final Level world;
	private final int x, y, z;
	private final Player entity;

	private static final ResourceLocation TEXTURE = new ResourceLocation("the_arg_container:textures/screens/sky_builder_gui_v2.png");

	private static final int GRID_X = 52;
	private static final int GRID_Y = 15;
	private static final int GRID_COLS = 4;
	private static final int GRID_ROWS = 3;
	private static final int SLOT_SIZE = 18;

	private static final int SCROLL_X = 127;
	private static final int SCROLL_Y = 15;
	private static final int SCROLL_W = 8;
	private static final int SCROLL_H = GRID_ROWS * SLOT_SIZE;
	private static final int THUMB_H = 16;

	private static final int COL_TRACK_BG = 0xFF1A1A1A;
	private static final int COL_TRACK_BORDER = 0xFF000000;
	private static final int COL_TRACK_INNER = 0xFF2D2D2D;
	private static final int COL_THUMB_TOP = 0xFFAADDFF;
	private static final int COL_THUMB_MID = 0xFF5599CC;
	private static final int COL_THUMB_BOT = 0xFF336688;
	private static final int COL_THUMB_INACTIVE = 0xFF444444;
	private static final int COL_SELECTED = 0x9000AAFF;
	private static final int COL_CANNOT_AFFORD = 0xAA000000;
	private static final int COL_NO_SHARDS = 0xCC333333;

	private int scrollOffset = 0;
	private double scrollProgress = 0.0;
	private boolean draggingScroll = false;
	private int selectedRecipe = -1;

	public SkyBuilderGuiScreen(SkyBuilderGuiMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
		this.titleLabelX = 52;
		this.titleLabelY = 6;
		this.inventoryLabelX = 8;
		this.inventoryLabelY = 72;
	}

	private int getMaxScroll() {
		int totalRows = (int) Math.ceil((double) SkyBuilderRecipes.RECIPES.size() / GRID_COLS);
		return Math.max(0, totalRows - GRID_ROWS);
	}

	private boolean isScrollActive() {
		return getMaxScroll() > 0;
	}

	private void updateScrollFromMouseY(double mouseY) {
		if (!isScrollActive())
			return;
		double usable = SCROLL_H - THUMB_H;
		double relative = (mouseY - (this.topPos + SCROLL_Y + THUMB_H / 2.0)) / usable;
		scrollProgress = Math.max(0.0, Math.min(1.0, relative));
		scrollOffset = (int) Math.round(scrollProgress * getMaxScroll());
	}

	private int getThumbScreenY() {
		return this.topPos + SCROLL_Y + (int) (scrollProgress * (SCROLL_H - THUMB_H));
	}

	private boolean isOverScrollbar(double mx, double my) {
		return mx >= this.leftPos + SCROLL_X && mx <= this.leftPos + SCROLL_X + SCROLL_W && my >= this.topPos + SCROLL_Y && my <= this.topPos + SCROLL_Y + SCROLL_H;
	}

	private int getRecipeIndexAt(double mouseX, double mouseY) {
		for (int i = 0; i < GRID_COLS * GRID_ROWS; i++) {
			int recipeIdx = scrollOffset * GRID_COLS + i;
			if (recipeIdx >= SkyBuilderRecipes.RECIPES.size())
				break;
			int col = i % GRID_COLS;
			int row = i / GRID_COLS;
			int sx = this.leftPos + GRID_X + col * SLOT_SIZE;
			int sy = this.topPos + GRID_Y + row * SLOT_SIZE;
			if (mouseX >= sx && mouseX < sx + SLOT_SIZE && mouseY >= sy && mouseY < sy + SLOT_SIZE)
				return recipeIdx;
		}
		return -1;
	}

	private int getShardsInInputSlot() {
		ItemStack stack = this.menu.slots.get(0).getItem();
		return (stack.getItem() == TheArgContainerModItems.SKY_SHARD.get()) ? stack.getCount() : 0;
	}

	@Override
	public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(gg);
		super.render(gg, mouseX, mouseY, partialTicks);
		this.renderTooltip(gg, mouseX, mouseY);
		this.renderRecipeTooltip(gg, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics gg, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		gg.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, 176, 166, 176, 166);

		int currentShards = getShardsInInputSlot();
		boolean hasShards = currentShards > 0;

		for (int i = 0; i < GRID_COLS * GRID_ROWS; i++) {
			int recipeIdx = scrollOffset * GRID_COLS + i;
			if (recipeIdx >= SkyBuilderRecipes.RECIPES.size())
				break;

			int col = i % GRID_COLS;
			int row = i / GRID_COLS;
			int sx = this.leftPos + GRID_X + col * SLOT_SIZE;
			int sy = this.topPos + GRID_Y + row * SLOT_SIZE;

			if (!hasShards) {
				gg.fill(sx + 1, sy + 1, sx + SLOT_SIZE - 1, sy + SLOT_SIZE - 1, COL_NO_SHARDS);
				continue;
			}

			SkyBuilderRecipes.Recipe recipe = SkyBuilderRecipes.RECIPES.get(recipeIdx);

			if (recipeIdx == selectedRecipe)
				gg.fill(sx + 1, sy + 1, sx + SLOT_SIZE - 1, sy + SLOT_SIZE - 1, COL_SELECTED);

			gg.renderItem(recipe.getOutput(), sx + 1, sy + 1);

			if (currentShards < recipe.inputCount())
				gg.fill(sx + 1, sy + 1, sx + SLOT_SIZE - 1, sy + SLOT_SIZE - 1, COL_CANNOT_AFFORD);
		}

		renderScrollbar(gg);
		RenderSystem.disableBlend();
	}

	private void renderScrollbar(GuiGraphics gg) {
		int tx = this.leftPos + SCROLL_X;
		int ty = this.topPos + SCROLL_Y;
		int bx = tx + SCROLL_W;
		int by = ty + SCROLL_H;

		gg.fill(tx - 1, ty - 1, bx + 1, by + 1, COL_TRACK_BORDER);
		gg.fill(tx, ty, bx, by, COL_TRACK_BG);
		int cx = tx + SCROLL_W / 2;
		gg.fill(cx, ty + 1, cx + 1, by - 1, COL_TRACK_INNER);

		if (isScrollActive()) {
			int thumbY = getThumbScreenY();
			gg.fill(tx - 1, thumbY - 1, bx + 1, thumbY + THUMB_H + 1, COL_TRACK_BORDER);
			gg.fill(tx, thumbY, bx, thumbY + 3, COL_THUMB_TOP);
			gg.fill(tx, thumbY + 3, bx, thumbY + THUMB_H - 3, COL_THUMB_MID);
			gg.fill(tx, thumbY + THUMB_H - 3, bx, thumbY + THUMB_H, COL_THUMB_BOT);
			gg.fill(tx, thumbY, tx + 1, thumbY + THUMB_H, COL_THUMB_TOP);
		} else {
			gg.fill(tx, ty, bx, ty + THUMB_H, COL_THUMB_INACTIVE);
		}
	}

	private void renderRecipeTooltip(GuiGraphics gg, int mouseX, int mouseY) {
		int idx = getRecipeIndexAt(mouseX, mouseY);
		if (idx < 0)
			return;

		int currentShards = getShardsInInputSlot();
		List<Component> lines = new ArrayList<>();

		if (currentShards == 0) {
			lines.add(Component.literal("Insert Sky Shards to craft").withStyle(ChatFormatting.GRAY));
			gg.renderTooltip(this.font, lines, Optional.empty(), mouseX, mouseY);
			return;
		}

		SkyBuilderRecipes.Recipe recipe = SkyBuilderRecipes.RECIPES.get(idx);
		int need = recipe.inputCount();

		lines.add(recipe.getOutput().getHoverName().copy().withStyle(ChatFormatting.WHITE));
		lines.add(Component.literal(need + "x Sky Shard  =  1x " + recipe.getOutput().getHoverName().getString()).withStyle(ChatFormatting.GRAY));

		if (currentShards < need) {
			lines.add(Component.literal("Need " + (need - currentShards) + " more Sky Shard(s)").withStyle(ChatFormatting.RED));
		} else {
			int canCraft = currentShards / need;
			lines.add(Component.literal("Click to craft!").withStyle(ChatFormatting.GREEN));
			if (canCraft > 1)
				lines.add(Component.literal("Shift-click to craft all (" + canCraft + "x)").withStyle(ChatFormatting.AQUA));
		}

		gg.renderTooltip(this.font, lines, Optional.empty(), mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics gg, int mouseX, int mouseY) {
		gg.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
		gg.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
	}

	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		if (isScrollActive() && isOverScrollbar(mx, my)) {
			draggingScroll = true;
			updateScrollFromMouseY(my);
			return true;
		}

		int recipeIdx = getRecipeIndexAt(mx, my);
		if (recipeIdx >= 0 && getShardsInInputSlot() > 0) {
			selectedRecipe = recipeIdx;
			SkyBuilderNetwork.CHANNEL.sendToServer(new CraftSkyBuilderPacket(recipeIdx, hasShiftDown()));
			return true;
		}

		return super.mouseClicked(mx, my, button);
	}

	@Override
	public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
		if (draggingScroll) {
			updateScrollFromMouseY(my);
			return true;
		}
		return super.mouseDragged(mx, my, button, dx, dy);
	}

	@Override
	public boolean mouseReleased(double mx, double my, int button) {
		draggingScroll = false;
		return super.mouseReleased(mx, my, button);
	}

	@Override
	public boolean mouseScrolled(double mx, double my, double delta) {
		if (isScrollActive()) {
			int maxScroll = getMaxScroll();
			scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - delta));
			scrollProgress = (maxScroll > 0) ? (double) scrollOffset / maxScroll : 0.0;
			return true;
		}
		return super.mouseScrolled(mx, my, delta);
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	public void init() {
		super.init();
	}
}
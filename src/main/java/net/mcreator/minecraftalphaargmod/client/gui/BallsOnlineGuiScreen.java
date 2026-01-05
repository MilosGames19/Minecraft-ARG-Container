package net.mcreator.minecraftalphaargmod.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;

import net.mcreator.minecraftalphaargmod.world.inventory.BallsOnlineGuiMenu;
import net.mcreator.minecraftalphaargmod.init.TheArgContainerModScreens.WidgetScreen;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class BallsOnlineGuiScreen extends AbstractContainerScreen<BallsOnlineGuiMenu> implements WidgetScreen {
	private final static HashMap<String, Object> guistate = BallsOnlineGuiMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private final static HashMap<String, String> textstate = new HashMap<>();
	public static EditBox balls_online;
	Button button_empty;

	public BallsOnlineGuiScreen(BallsOnlineGuiMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("the_arg_container:textures/screens/balls_online_gui.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		balls_online.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		RenderSystem.disableBlend();
	}

	public HashMap<String, Object> getWidgets() {
		return guistate;
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		if (balls_online.isFocused())
			return balls_online.keyPressed(key, b, c);
		return super.keyPressed(key, b, c);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		balls_online.tick();
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		String balls_onlineValue = balls_online.getValue();
		super.resize(minecraft, width, height);
		balls_online.setValue(balls_onlineValue);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.the_arg_container.balls_online_gui.label_unfinished"), 50, 7, -3801088, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.the_arg_container.balls_online_gui.label_enter_website"), 51, 41, -16777216, false);
	}

	@Override
	public void init() {
		super.init();
		balls_online = new EditBox(this.font, this.leftPos + 28, this.topPos + 71, 118, 18, Component.translatable("gui.the_arg_container.balls_online_gui.balls_online"));
		balls_online.setMaxLength(32767);
		guistate.put("text:balls_online", balls_online);
		this.addWidget(this.balls_online);
		button_empty = Button.builder(Component.translatable("gui.the_arg_container.balls_online_gui.button_empty"), e -> {
		}).bounds(this.leftPos + 17, this.topPos + 134, 140, 20).build();
		guistate.put("button:button_empty", button_empty);
		this.addRenderableWidget(button_empty);
	}
}

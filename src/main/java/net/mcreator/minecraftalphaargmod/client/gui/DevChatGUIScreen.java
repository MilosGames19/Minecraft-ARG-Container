package net.mcreator.minecraftalphaargmod.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;

import net.mcreator.minecraftalphaargmod.world.inventory.DevChatGUIMenu;
import net.mcreator.minecraftalphaargmod.init.TheArgContainerModScreens.WidgetScreen;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class DevChatGUIScreen extends AbstractContainerScreen<DevChatGUIMenu> implements WidgetScreen {
	private final static HashMap<String, Object> guistate = DevChatGUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private final static HashMap<String, String> textstate = new HashMap<>();
	public static EditBox dev_chat;

	public DevChatGUIScreen(DevChatGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		dev_chat.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
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
		if (dev_chat.isFocused())
			return dev_chat.keyPressed(key, b, c);
		return super.keyPressed(key, b, c);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		dev_chat.tick();
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		String dev_chatValue = dev_chat.getValue();
		super.resize(minecraft, width, height);
		dev_chat.setValue(dev_chatValue);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.the_arg_container.dev_chat_gui.label_who_do_you_want_to_contact"), 18, 14, -1, false);
	}

	@Override
	public void init() {
		super.init();
		dev_chat = new EditBox(this.font, this.leftPos + 29, this.topPos + 74, 118, 18, Component.translatable("gui.the_arg_container.dev_chat_gui.dev_chat"));
		dev_chat.setMaxLength(32767);
		guistate.put("text:dev_chat", dev_chat);
		this.addWidget(this.dev_chat);
	}
}

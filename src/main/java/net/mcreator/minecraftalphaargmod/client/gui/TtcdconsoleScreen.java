package net.mcreator.minecraftalphaargmod.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.minecraftalphaargmod.world.inventory.TtcdconsoleMenu;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class TtcdconsoleScreen extends AbstractContainerScreen<TtcdconsoleMenu> {
	private final static HashMap<String, Object> guistate = TtcdconsoleMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private final static HashMap<String, String> textstate = new HashMap<>();
	public static EditBox Execute_protocol;
	Button button_launch;
	Button button_leave;

	public TtcdconsoleScreen(TtcdconsoleMenu container, Inventory inventory, Component text) {
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
		Execute_protocol.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		if (Execute_protocol.isFocused())
			return Execute_protocol.keyPressed(key, b, c);
		return super.keyPressed(key, b, c);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		Execute_protocol.tick();
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.the_arg_container.ttcdconsole.label_execute_protocol"), 42, -2, -1, false);
	}

	@Override
	public void init() {
		super.init();
		Execute_protocol = new EditBox(this.font, this.leftPos + 25, this.topPos + 17, 118, 18, Component.translatable("gui.the_arg_container.ttcdconsole.Execute_protocol"));
		Execute_protocol.setMaxLength(32767);
		guistate.put("text:Execute_protocol", Execute_protocol);
		this.addWidget(this.Execute_protocol);
		button_launch = Button.builder(Component.translatable("gui.the_arg_container.ttcdconsole.button_launch"), e -> {
		}).bounds(this.leftPos + 150, this.topPos + 16, 61, 20).build();
		guistate.put("button:button_launch", button_launch);
		this.addRenderableWidget(button_launch);
		button_leave = Button.builder(Component.translatable("gui.the_arg_container.ttcdconsole.button_leave"), e -> {
		}).bounds(this.leftPos + 150, this.topPos + 43, 51, 20).build();
		guistate.put("button:button_leave", button_leave);
		this.addRenderableWidget(button_leave);
	}
}

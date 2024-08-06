package net.mcreator.minecraftalphaargmod.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.client.player.Input;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.minecraftalphaargmod.world.inventory.TerminalGUIMenu;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class TerminalGUIScreen extends AbstractContainerScreen<TerminalGUIMenu> {
	private final static HashMap<String, Object> guistate = TerminalGUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private final static HashMap<String, String> textstate = new HashMap<>();
	public static EditBox Input;
	Button button_execute;

	public TerminalGUIScreen(TerminalGUIMenu container, Inventory inventory, Component text) {
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
		Input.render(guiGraphics, mouseX, mouseY, partialTicks);
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
		if (Input.isFocused())
			return Input.keyPressed(key, b, c);
		return super.keyPressed(key, b, c);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		Input.tick();
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.minecraft_alpha_arg_mod.terminal_gui.label_type_raw_command"), 46, 12, -1, false);
	}

	@Override
	public void init() {
		super.init();
		Input = new EditBox(this.font, this.leftPos + 28, this.topPos + 70, 118, 18, Component.translatable("gui.minecraft_alpha_arg_mod.terminal_gui.Input"));
		Input.setMaxLength(32767);
		guistate.put("text:Input", Input);
		this.addWidget(this.Input);
		button_execute = Button.builder(Component.translatable("gui.minecraft_alpha_arg_mod.terminal_gui.button_execute"), e -> {
		}).bounds(this.leftPos + 55, this.topPos + 131, 61, 20).build();
		guistate.put("button:button_execute", button_execute);
		this.addRenderableWidget(button_execute);
	}
}

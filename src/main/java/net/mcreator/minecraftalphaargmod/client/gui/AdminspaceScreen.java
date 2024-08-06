package net.mcreator.minecraftalphaargmod.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.minecraftalphaargmod.world.inventory.AdminspaceMenu;
import net.mcreator.minecraftalphaargmod.network.AdminspaceButtonMessage;
import net.mcreator.minecraftalphaargmod.MinecraftAlphaArgModMod;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class AdminspaceScreen extends AbstractContainerScreen<AdminspaceMenu> {
	private final static HashMap<String, Object> guistate = AdminspaceMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private final static HashMap<String, String> textstate = new HashMap<>();
	Button button_yes;
	Button button_no;

	public AdminspaceScreen(AdminspaceMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("minecraft_alpha_arg_mod:textures/screens/adminspace.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
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

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	public void containerTick() {
		super.containerTick();
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.minecraft_alpha_arg_mod.adminspace.label_adminspace"), 6, 7, -16777216, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecraft_alpha_arg_mod.adminspace.label_proceed"), 63, 51, -16777216, false);
	}

	@Override
	public void init() {
		super.init();
		button_yes = Button.builder(Component.translatable("gui.minecraft_alpha_arg_mod.adminspace.button_yes"), e -> {
			if (true) {
				MinecraftAlphaArgModMod.PACKET_HANDLER.sendToServer(new AdminspaceButtonMessage(0, x, y, z, textstate));
				AdminspaceButtonMessage.handleButtonAction(entity, 0, x, y, z, textstate);
			}
		}).bounds(this.leftPos + 63, this.topPos + 71, 40, 20).build();
		guistate.put("button:button_yes", button_yes);
		this.addRenderableWidget(button_yes);
		button_no = Button.builder(Component.translatable("gui.minecraft_alpha_arg_mod.adminspace.button_no"), e -> {
			if (true) {
				MinecraftAlphaArgModMod.PACKET_HANDLER.sendToServer(new AdminspaceButtonMessage(1, x, y, z, textstate));
				AdminspaceButtonMessage.handleButtonAction(entity, 1, x, y, z, textstate);
			}
		}).bounds(this.leftPos + 66, this.topPos + 97, 35, 20).build();
		guistate.put("button:button_no", button_no);
		this.addRenderableWidget(button_no);
	}
}

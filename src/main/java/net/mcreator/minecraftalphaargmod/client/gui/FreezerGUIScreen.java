package net.mcreator.minecraftalphaargmod.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.minecraftalphaargmod.world.inventory.FreezerGUIMenu;
import net.mcreator.minecraftalphaargmod.TheArgContainerMod;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class FreezerGUIScreen extends AbstractContainerScreen<FreezerGUIMenu> {
	private final static HashMap<String, Object> guistate = FreezerGUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private final static HashMap<String, String> textstate = new HashMap<>();

	public FreezerGUIScreen(FreezerGUIMenu container, Inventory inventory, Component text) {
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
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		guiGraphics.blit(new ResourceLocation("the_arg_container:textures/screens/freezer.png"), this.leftPos + 0, this.topPos + 0, 0, 0, 256, 256, 256, 256);

		RenderSystem.disableBlend();
	}

	public static HashMap<String, String> getTextboxValues() {
		return textstate;
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
		TheArgContainerMod.PACKET_HANDLER.sendToServer(new FreezerGUIMenu.FreezerGUIOtherMessage(0, x, y, z, textstate));
		FreezerGUIMenu.FreezerGUIOtherMessage.handleOtherAction(entity, 0, x, y, z, textstate);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.the_arg_container.freezer_gui.label_freezer"), 66, 6, -1, false);
	}

	@Override
	public void init() {
		super.init();
	}
}

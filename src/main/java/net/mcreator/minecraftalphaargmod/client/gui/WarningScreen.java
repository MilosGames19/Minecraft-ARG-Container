package net.mcreator.minecraftalphaargmod.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.minecraftalphaargmod.world.inventory.WarningMenu;
import net.mcreator.minecraftalphaargmod.network.WarningButtonMessage;
import net.mcreator.minecraftalphaargmod.init.TheArgContainerModScreens.WidgetScreen;
import net.mcreator.minecraftalphaargmod.TheArgContainerMod;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class WarningScreen extends AbstractContainerScreen<WarningMenu> implements WidgetScreen {
	private final static HashMap<String, Object> guistate = WarningMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private final static HashMap<String, String> textstate = new HashMap<>();
	Button button_i_understand;

	public WarningScreen(WarningMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("the_arg_container:textures/screens/warning.png");

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

	public HashMap<String, Object> getWidgets() {
		return guistate;
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
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.the_arg_container.warning.label_warning"), 11, 10, -16777216, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.the_arg_container.warning.label_this_mod_is_still_in_beta_and_u"), 11, 28, -16777216, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.the_arg_container.warning.label_and_updates_could_potentially_co"), 11, 37, -16777216, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.the_arg_container.warning.label_corrupt_the_world"), 11, 46, -65536, false);
	}

	@Override
	public void init() {
		super.init();
		button_i_understand = Button.builder(Component.translatable("gui.the_arg_container.warning.button_i_understand"), e -> {
			if (true) {
				TheArgContainerMod.PACKET_HANDLER.sendToServer(new WarningButtonMessage(0, x, y, z, textstate));
				WarningButtonMessage.handleButtonAction(entity, 0, x, y, z, textstate);
			}
		}).bounds(this.leftPos + 41, this.topPos + 132, 87, 20).build();
		guistate.put("button:button_i_understand", button_i_understand);
		this.addRenderableWidget(button_i_understand);
	}
}

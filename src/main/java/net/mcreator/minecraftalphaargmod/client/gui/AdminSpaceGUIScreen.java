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

import net.mcreator.minecraftalphaargmod.world.inventory.AdminSpaceGUIMenu;
import net.mcreator.minecraftalphaargmod.network.AdminSpaceGUIButtonMessage;
import net.mcreator.minecraftalphaargmod.MinecraftAlphaArgModMod;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class AdminSpaceGUIScreen extends AbstractContainerScreen<AdminSpaceGUIMenu> {
	private final static HashMap<String, Object> guistate = AdminSpaceGUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private final static HashMap<String, String> textstate = new HashMap<>();
	public static EditBox asc;
	Button button_execute;

	public AdminSpaceGUIScreen(AdminSpaceGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("minecraft_alpha_arg_mod:textures/screens/admin_space_gui.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		asc.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

		guiGraphics.blit(new ResourceLocation("minecraft_alpha_arg_mod:textures/screens/bogulied.png"), this.leftPos + 0, this.topPos + -10, 0, 0, 176, 176, 176, 176);

		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		if (asc.isFocused())
			return asc.keyPressed(key, b, c);
		return super.keyPressed(key, b, c);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		asc.tick();
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.minecraft_alpha_arg_mod.admin_space_gui.label_adminspace_console"), 4, -7, -16777216, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.minecraft_alpha_arg_mod.admin_space_gui.label_v2084"), 133, -7, -16777216, false);
	}

	@Override
	public void init() {
		super.init();
		asc = new EditBox(this.font, this.leftPos + 29, this.topPos + 17, 118, 18, Component.translatable("gui.minecraft_alpha_arg_mod.admin_space_gui.asc"));
		asc.setMaxLength(32767);
		guistate.put("text:asc", asc);
		this.addWidget(this.asc);
		button_execute = Button.builder(Component.translatable("gui.minecraft_alpha_arg_mod.admin_space_gui.button_execute"), e -> {
			if (true) {
				textstate.put("textin:asc", asc.getValue());
				MinecraftAlphaArgModMod.PACKET_HANDLER.sendToServer(new AdminSpaceGUIButtonMessage(0, x, y, z, textstate));
				AdminSpaceGUIButtonMessage.handleButtonAction(entity, 0, x, y, z, textstate);
			}
		}).bounds(this.leftPos + 55, this.topPos + 132, 61, 20).build();
		guistate.put("button:button_execute", button_execute);
		this.addRenderableWidget(button_execute);
	}
}

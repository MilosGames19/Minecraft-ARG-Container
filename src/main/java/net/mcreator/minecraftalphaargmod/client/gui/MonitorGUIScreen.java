package net.mcreator.minecraftalphaargmod.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.minecraftalphaargmod.world.inventory.MonitorGUIMenu;
import net.mcreator.minecraftalphaargmod.network.MonitorGUIButtonMessage;
import net.mcreator.minecraftalphaargmod.MinecraftAlphaArgModMod;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class MonitorGUIScreen extends AbstractContainerScreen<MonitorGUIMenu> {
	private final static HashMap<String, Object> guistate = MonitorGUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private final static HashMap<String, String> textstate = new HashMap<>();
	Button button_empty;
	Button button_empty1;
	Button button_empty2;
	Button button_ps;
	Button button_off;

	public MonitorGUIScreen(MonitorGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("minecraft_alpha_arg_mod:textures/screens/monitor_gui.png");

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

		guiGraphics.blit(new ResourceLocation("minecraft_alpha_arg_mod:textures/screens/bogulied.png"), this.leftPos + 0, this.topPos + -10, 0, 0, 176, 176, 176, 176);

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
		guiGraphics.drawString(this.font, Component.translatable("gui.minecraft_alpha_arg_mod.monitor_gui.label_monitor"), 3, -7, -12829636, false);
	}

	@Override
	public void init() {
		super.init();
		button_empty = Button.builder(Component.translatable("gui.minecraft_alpha_arg_mod.monitor_gui.button_empty"), e -> {
			if (true) {
				MinecraftAlphaArgModMod.PACKET_HANDLER.sendToServer(new MonitorGUIButtonMessage(0, x, y, z, textstate));
				MonitorGUIButtonMessage.handleButtonAction(entity, 0, x, y, z, textstate);
			}
		}).bounds(this.leftPos + 24, this.topPos + 25, 30, 20).build();
		guistate.put("button:button_empty", button_empty);
		this.addRenderableWidget(button_empty);
		button_empty1 = Button.builder(Component.translatable("gui.minecraft_alpha_arg_mod.monitor_gui.button_empty1"), e -> {
			if (true) {
				MinecraftAlphaArgModMod.PACKET_HANDLER.sendToServer(new MonitorGUIButtonMessage(1, x, y, z, textstate));
				MonitorGUIButtonMessage.handleButtonAction(entity, 1, x, y, z, textstate);
			}
		}).bounds(this.leftPos + 123, this.topPos + 25, 30, 20).build();
		guistate.put("button:button_empty1", button_empty1);
		this.addRenderableWidget(button_empty1);
		button_empty2 = Button.builder(Component.translatable("gui.minecraft_alpha_arg_mod.monitor_gui.button_empty2"), e -> {
			if (true) {
				MinecraftAlphaArgModMod.PACKET_HANDLER.sendToServer(new MonitorGUIButtonMessage(2, x, y, z, textstate));
				MonitorGUIButtonMessage.handleButtonAction(entity, 2, x, y, z, textstate);
			}
		}).bounds(this.leftPos + 24, this.topPos + 79, 30, 20).build();
		guistate.put("button:button_empty2", button_empty2);
		this.addRenderableWidget(button_empty2);
		button_ps = Button.builder(Component.translatable("gui.minecraft_alpha_arg_mod.monitor_gui.button_ps"), e -> {
			if (true) {
				MinecraftAlphaArgModMod.PACKET_HANDLER.sendToServer(new MonitorGUIButtonMessage(3, x, y, z, textstate));
				MonitorGUIButtonMessage.handleButtonAction(entity, 3, x, y, z, textstate);
			}
		}).bounds(this.leftPos + 123, this.topPos + 79, 30, 20).build();
		guistate.put("button:button_ps", button_ps);
		this.addRenderableWidget(button_ps);
		button_off = Button.builder(Component.translatable("gui.minecraft_alpha_arg_mod.monitor_gui.button_off"), e -> {
			if (true) {
				MinecraftAlphaArgModMod.PACKET_HANDLER.sendToServer(new MonitorGUIButtonMessage(4, x, y, z, textstate));
				MonitorGUIButtonMessage.handleButtonAction(entity, 4, x, y, z, textstate);
			}
		}).bounds(this.leftPos + 69, this.topPos + 133, 40, 20).build();
		guistate.put("button:button_off", button_off);
		this.addRenderableWidget(button_off);
	}
}

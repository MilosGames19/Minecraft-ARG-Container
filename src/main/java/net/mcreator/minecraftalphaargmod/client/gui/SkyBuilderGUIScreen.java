package net.mcreator.minecraftalphaargmod.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.minecraftalphaargmod.world.inventory.SkyBuilderGUIMenu;
import net.mcreator.minecraftalphaargmod.network.SkyBuilderGUIButtonMessage;
import net.mcreator.minecraftalphaargmod.TheArgContainerMod;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class SkyBuilderGUIScreen extends AbstractContainerScreen<SkyBuilderGUIMenu> {
	private final static HashMap<String, Object> guistate = SkyBuilderGUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private final static HashMap<String, String> textstate = new HashMap<>();
	ImageButton imagebutton_slot;
	ImageButton imagebutton_slot1;
	ImageButton imagebutton_slot2;
	ImageButton imagebutton_slot3;
	ImageButton imagebutton_slot4;
	ImageButton imagebutton_slot5;
	ImageButton imagebutton_slot6;
	ImageButton imagebutton_slot7;
	ImageButton imagebutton_slot8;
	ImageButton imagebutton_slot9;
	ImageButton imagebutton_slot10;
	ImageButton imagebutton_slot11;

	public SkyBuilderGUIScreen(SkyBuilderGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("the_arg_container:textures/screens/sky_builder_gui.png");

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
		TheArgContainerMod.PACKET_HANDLER.sendToServer(new SkyBuilderGUIMenu.SkyBuilderGUIOtherMessage(0, x, y, z, textstate));
		SkyBuilderGUIMenu.SkyBuilderGUIOtherMessage.handleOtherAction(entity, 0, x, y, z, textstate);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.the_arg_container.sky_builder_gui.label_inventory"), 7, 71, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.the_arg_container.sky_builder_gui.label_sky_builder"), 6, 4, -12829636, false);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_slot = new ImageButton(this.leftPos + 52, this.topPos + 15, 18, 18, 0, 0, 18, new ResourceLocation("the_arg_container:textures/screens/atlas/imagebutton_slot.png"), 18, 36, e -> {
			if (true) {
				TheArgContainerMod.PACKET_HANDLER.sendToServer(new SkyBuilderGUIButtonMessage(0, x, y, z, textstate));
				SkyBuilderGUIButtonMessage.handleButtonAction(entity, 0, x, y, z, textstate);
			}
		});
		guistate.put("button:imagebutton_slot", imagebutton_slot);
		this.addRenderableWidget(imagebutton_slot);
		imagebutton_slot1 = new ImageButton(this.leftPos + 70, this.topPos + 15, 18, 18, 0, 0, 18, new ResourceLocation("the_arg_container:textures/screens/atlas/imagebutton_slot1.png"), 18, 36, e -> {
			if (true) {
				TheArgContainerMod.PACKET_HANDLER.sendToServer(new SkyBuilderGUIButtonMessage(1, x, y, z, textstate));
				SkyBuilderGUIButtonMessage.handleButtonAction(entity, 1, x, y, z, textstate);
			}
		});
		guistate.put("button:imagebutton_slot1", imagebutton_slot1);
		this.addRenderableWidget(imagebutton_slot1);
		imagebutton_slot2 = new ImageButton(this.leftPos + 88, this.topPos + 15, 18, 18, 0, 0, 18, new ResourceLocation("the_arg_container:textures/screens/atlas/imagebutton_slot2.png"), 18, 36, e -> {
			if (true) {
				TheArgContainerMod.PACKET_HANDLER.sendToServer(new SkyBuilderGUIButtonMessage(2, x, y, z, textstate));
				SkyBuilderGUIButtonMessage.handleButtonAction(entity, 2, x, y, z, textstate);
			}
		});
		guistate.put("button:imagebutton_slot2", imagebutton_slot2);
		this.addRenderableWidget(imagebutton_slot2);
		imagebutton_slot3 = new ImageButton(this.leftPos + 106, this.topPos + 15, 18, 18, 0, 0, 18, new ResourceLocation("the_arg_container:textures/screens/atlas/imagebutton_slot3.png"), 18, 36, e -> {
			if (true) {
				TheArgContainerMod.PACKET_HANDLER.sendToServer(new SkyBuilderGUIButtonMessage(3, x, y, z, textstate));
				SkyBuilderGUIButtonMessage.handleButtonAction(entity, 3, x, y, z, textstate);
			}
		});
		guistate.put("button:imagebutton_slot3", imagebutton_slot3);
		this.addRenderableWidget(imagebutton_slot3);
		imagebutton_slot4 = new ImageButton(this.leftPos + 52, this.topPos + 33, 18, 18, 0, 0, 18, new ResourceLocation("the_arg_container:textures/screens/atlas/imagebutton_slot4.png"), 18, 36, e -> {
			if (true) {
				TheArgContainerMod.PACKET_HANDLER.sendToServer(new SkyBuilderGUIButtonMessage(4, x, y, z, textstate));
				SkyBuilderGUIButtonMessage.handleButtonAction(entity, 4, x, y, z, textstate);
			}
		});
		guistate.put("button:imagebutton_slot4", imagebutton_slot4);
		this.addRenderableWidget(imagebutton_slot4);
		imagebutton_slot5 = new ImageButton(this.leftPos + 70, this.topPos + 33, 18, 18, 0, 0, 18, new ResourceLocation("the_arg_container:textures/screens/atlas/imagebutton_slot5.png"), 18, 36, e -> {
			if (true) {
				TheArgContainerMod.PACKET_HANDLER.sendToServer(new SkyBuilderGUIButtonMessage(5, x, y, z, textstate));
				SkyBuilderGUIButtonMessage.handleButtonAction(entity, 5, x, y, z, textstate);
			}
		});
		guistate.put("button:imagebutton_slot5", imagebutton_slot5);
		this.addRenderableWidget(imagebutton_slot5);
		imagebutton_slot6 = new ImageButton(this.leftPos + 88, this.topPos + 33, 18, 18, 0, 0, 18, new ResourceLocation("the_arg_container:textures/screens/atlas/imagebutton_slot6.png"), 18, 36, e -> {
		});
		guistate.put("button:imagebutton_slot6", imagebutton_slot6);
		this.addRenderableWidget(imagebutton_slot6);
		imagebutton_slot7 = new ImageButton(this.leftPos + 106, this.topPos + 33, 18, 18, 0, 0, 18, new ResourceLocation("the_arg_container:textures/screens/atlas/imagebutton_slot7.png"), 18, 36, e -> {
		});
		guistate.put("button:imagebutton_slot7", imagebutton_slot7);
		this.addRenderableWidget(imagebutton_slot7);
		imagebutton_slot8 = new ImageButton(this.leftPos + 52, this.topPos + 51, 18, 18, 0, 0, 18, new ResourceLocation("the_arg_container:textures/screens/atlas/imagebutton_slot8.png"), 18, 36, e -> {
		});
		guistate.put("button:imagebutton_slot8", imagebutton_slot8);
		this.addRenderableWidget(imagebutton_slot8);
		imagebutton_slot9 = new ImageButton(this.leftPos + 70, this.topPos + 51, 18, 18, 0, 0, 18, new ResourceLocation("the_arg_container:textures/screens/atlas/imagebutton_slot9.png"), 18, 36, e -> {
		});
		guistate.put("button:imagebutton_slot9", imagebutton_slot9);
		this.addRenderableWidget(imagebutton_slot9);
		imagebutton_slot10 = new ImageButton(this.leftPos + 88, this.topPos + 51, 18, 18, 0, 0, 18, new ResourceLocation("the_arg_container:textures/screens/atlas/imagebutton_slot10.png"), 18, 36, e -> {
		});
		guistate.put("button:imagebutton_slot10", imagebutton_slot10);
		this.addRenderableWidget(imagebutton_slot10);
		imagebutton_slot11 = new ImageButton(this.leftPos + 106, this.topPos + 51, 18, 18, 0, 0, 18, new ResourceLocation("the_arg_container:textures/screens/atlas/imagebutton_slot11.png"), 18, 36, e -> {
		});
		guistate.put("button:imagebutton_slot11", imagebutton_slot11);
		this.addRenderableWidget(imagebutton_slot11);
	}
}

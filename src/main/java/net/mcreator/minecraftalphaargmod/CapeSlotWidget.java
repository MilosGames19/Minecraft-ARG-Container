package net.mcreator.minecraftalphaargmod.capes.client;

import net.mcreator.minecraftalphaargmod.capes.network.OpenCapeSelectorPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class CapeSlotWidget extends AbstractWidget {

	private final OpenCapeSelectorPacket.Entry entry;
	private final CapeSelectorScreen screen;

	public CapeSlotWidget(int x, int y, int size, OpenCapeSelectorPacket.Entry entry, CapeSelectorScreen screen) {
		super(x, y, size, size, entry == null ? Component.translatable("gui.the_arg_container.capes.none") : Component.translatable(entry.getDisplayName()));
		// super(x, y, size, size, entry == null ? Component.literal("None") : Component.literal(entry.getDisplayName()));
		this.entry = entry;
		this.screen = screen;
	}

	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		int x0 = getX();
		int y0 = getY();
		int x1 = x0 + width;
		int y1 = y0 + height;

		boolean selected = screen.isTentativelySelected(entry);

		guiGraphics.fill(x0, y0, x1, y1, 0xFF1A1A1A);

		if (entry != null) {
			int scale = (int) (width * 0.9);
			int centerX = x0 + width / 2;
			int centerY = y0 + height / 2 - scale / 2;

			guiGraphics.enableScissor(x0, y0, x1, y1);
			CapeSelectorScreen.renderCapeModel(guiGraphics, centerX, centerY, scale, entry.getTexture());
			guiGraphics.disableScissor();
		} else {
			// guiGraphics.drawCenteredString(screen.getMinecraft().font, "None", x0 + width / 2, y0 + height / 2 - 4, 0xA0A0A0);
			guiGraphics.drawCenteredString(screen.getMinecraft().font, Component.translatable("gui.the_arg_container.capes.none"), x0 + width / 2, y0 + height / 2 - 4, 0xA0A0A0);
		}

		int borderColor = selected ? 0xFF55FF55 : (isHovered ? 0xFFFFFFFF : 0xFF3A3A3A);
		guiGraphics.fill(x0, y0, x1, y0 + 1, borderColor);
		guiGraphics.fill(x0, y1 - 1, x1, y1, borderColor);
		guiGraphics.fill(x0, y0, x0 + 1, y1, borderColor);
		guiGraphics.fill(x1 - 1, y0, x1, y1, borderColor);
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		screen.selectTentative(entry);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
		narrationElementOutput.add(NarratedElementType.TITLE, this.getMessage());
	}

	public OpenCapeSelectorPacket.Entry getEntry() {
		return entry;
	}
}
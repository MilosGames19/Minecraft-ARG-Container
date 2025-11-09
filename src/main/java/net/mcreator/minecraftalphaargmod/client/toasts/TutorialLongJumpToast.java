package net.mcreator.minecraftalphaargmod.client.toasts;

import net.minecraft.world.level.entity.Visibility;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.GuiGraphics;

import com.mojang.blaze3d.systems.RenderSystem;

public class TutorialLongJumpToast implements Toast {
	@Override
	public Visibility render(GuiGraphics guiGraphics, ToastComponent component, long lastChanged) {
		guiGraphics.blit(TEXTURE, 0, 0, 0, 32, this.width(), this.height());
		guiGraphics.drawString(component.getMinecraft().font, Component.translatable("toasts.the_arg_container.tutorial_long_jump.title"), 5, 7, -11534256, false);
		guiGraphics.drawString(component.getMinecraft().font, Component.translatable("toasts.the_arg_container.tutorial_long_jump.description"), 5, 18, -16777216, false);
		RenderSystem.enableBlend();
		if (lastChanged <= 10000)
			return Visibility.SHOW;
		else
			return Visibility.HIDE;
	}
}


package net.mcreator.minecraftalphaargmod.client.screens;

import org.checkerframework.checker.units.qual.h;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.Minecraft;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class VersionOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(ScreenEvent.Render.Post event) {
		if (event.getScreen() instanceof TitleScreen) {
			int w = event.getScreen().width;
			int h = event.getScreen().height;
			Level world = null;
			double x = 0;
			double y = 0;
			double z = 0;
			Player entity = Minecraft.getInstance().player;
			if (entity != null) {
				world = entity.level();
				x = entity.getX();
				y = entity.getY();
				z = entity.getZ();
			}
			if (true) {
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.the_arg_container.version.label_the_arg_container_version_beta"), 1, 2, -1, false);
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.the_arg_container.version.label_beta_041"), 16, 12, -1, false);
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.the_arg_container.version.label_preview"), 16, 22, -1, false);
			}
		}
	}
}

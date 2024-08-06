
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.minecraftalphaargmod.init;

import org.lwjgl.glfw.GLFW;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

import net.mcreator.minecraftalphaargmod.network.SmallDashMessage;
import net.mcreator.minecraftalphaargmod.network.DashMessage;
import net.mcreator.minecraftalphaargmod.MinecraftAlphaArgModMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class MinecraftAlphaArgModModKeyMappings {
	public static final KeyMapping DASH = new KeyMapping("key.minecraft_alpha_arg_mod.dash", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.misc") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				MinecraftAlphaArgModMod.PACKET_HANDLER.sendToServer(new DashMessage(0, 0));
				DashMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};
	public static final KeyMapping SMALL_DASH = new KeyMapping("key.minecraft_alpha_arg_mod.small_dash", GLFW.GLFW_KEY_SPACE, "key.categories.misc") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				MinecraftAlphaArgModMod.PACKET_HANDLER.sendToServer(new SmallDashMessage(0, 0));
				SmallDashMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(DASH);
		event.register(SMALL_DASH);
	}

	@Mod.EventBusSubscriber({Dist.CLIENT})
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (Minecraft.getInstance().screen == null) {
				DASH.consumeClick();
				SMALL_DASH.consumeClick();
			}
		}
	}
}


/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.minecraftalphaargmod.init;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.gui.screens.MenuScreens;

import net.mcreator.minecraftalphaargmod.client.gui.TerminalGUIScreen;
import net.mcreator.minecraftalphaargmod.client.gui.ServerGuIScreen;
import net.mcreator.minecraftalphaargmod.client.gui.SafeGUIScreen;
import net.mcreator.minecraftalphaargmod.client.gui.MonitorGUIScreen;
import net.mcreator.minecraftalphaargmod.client.gui.KeyScreen;
import net.mcreator.minecraftalphaargmod.client.gui.Key1Screen;
import net.mcreator.minecraftalphaargmod.client.gui.GuiScreen;
import net.mcreator.minecraftalphaargmod.client.gui.FreezerGUIScreen;
import net.mcreator.minecraftalphaargmod.client.gui.EtGUIScreen;
import net.mcreator.minecraftalphaargmod.client.gui.EssenceclonerGUIScreen;
import net.mcreator.minecraftalphaargmod.client.gui.BlueChestGUIScreen;
import net.mcreator.minecraftalphaargmod.client.gui.AdminspaceScreen;
import net.mcreator.minecraftalphaargmod.client.gui.AdminSpaceGUIScreen;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MinecraftAlphaArgModModScreens {
	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(MinecraftAlphaArgModModMenus.SAFE_GUI.get(), SafeGUIScreen::new);
			MenuScreens.register(MinecraftAlphaArgModModMenus.FREEZER_GUI.get(), FreezerGUIScreen::new);
			MenuScreens.register(MinecraftAlphaArgModModMenus.ET_GUI.get(), EtGUIScreen::new);
			MenuScreens.register(MinecraftAlphaArgModModMenus.ESSENCECLONER_GUI.get(), EssenceclonerGUIScreen::new);
			MenuScreens.register(MinecraftAlphaArgModModMenus.GUI.get(), GuiScreen::new);
			MenuScreens.register(MinecraftAlphaArgModModMenus.TERMINAL_GUI.get(), TerminalGUIScreen::new);
			MenuScreens.register(MinecraftAlphaArgModModMenus.SERVER_GU_I.get(), ServerGuIScreen::new);
			MenuScreens.register(MinecraftAlphaArgModModMenus.KEY.get(), KeyScreen::new);
			MenuScreens.register(MinecraftAlphaArgModModMenus.ADMINSPACE.get(), AdminspaceScreen::new);
			MenuScreens.register(MinecraftAlphaArgModModMenus.ADMIN_SPACE_GUI.get(), AdminSpaceGUIScreen::new);
			MenuScreens.register(MinecraftAlphaArgModModMenus.MONITOR_GUI.get(), MonitorGUIScreen::new);
			MenuScreens.register(MinecraftAlphaArgModModMenus.BLUE_CHEST_GUI.get(), BlueChestGUIScreen::new);
			MenuScreens.register(MinecraftAlphaArgModModMenus.KEY_1.get(), Key1Screen::new);
		});
	}
}

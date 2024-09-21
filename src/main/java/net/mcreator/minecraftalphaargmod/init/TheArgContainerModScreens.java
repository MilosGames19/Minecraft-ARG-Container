
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
public class TheArgContainerModScreens {
	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(TheArgContainerModMenus.SAFE_GUI.get(), SafeGUIScreen::new);
			MenuScreens.register(TheArgContainerModMenus.FREEZER_GUI.get(), FreezerGUIScreen::new);
			MenuScreens.register(TheArgContainerModMenus.ET_GUI.get(), EtGUIScreen::new);
			MenuScreens.register(TheArgContainerModMenus.ESSENCECLONER_GUI.get(), EssenceclonerGUIScreen::new);
			MenuScreens.register(TheArgContainerModMenus.GUI.get(), GuiScreen::new);
			MenuScreens.register(TheArgContainerModMenus.TERMINAL_GUI.get(), TerminalGUIScreen::new);
			MenuScreens.register(TheArgContainerModMenus.SERVER_GU_I.get(), ServerGuIScreen::new);
			MenuScreens.register(TheArgContainerModMenus.KEY.get(), KeyScreen::new);
			MenuScreens.register(TheArgContainerModMenus.ADMINSPACE.get(), AdminspaceScreen::new);
			MenuScreens.register(TheArgContainerModMenus.ADMIN_SPACE_GUI.get(), AdminSpaceGUIScreen::new);
			MenuScreens.register(TheArgContainerModMenus.MONITOR_GUI.get(), MonitorGUIScreen::new);
			MenuScreens.register(TheArgContainerModMenus.BLUE_CHEST_GUI.get(), BlueChestGUIScreen::new);
			MenuScreens.register(TheArgContainerModMenus.KEY_1.get(), Key1Screen::new);
		});
	}
}

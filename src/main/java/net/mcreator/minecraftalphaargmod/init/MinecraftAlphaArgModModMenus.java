
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.minecraftalphaargmod.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

import net.mcreator.minecraftalphaargmod.world.inventory.TerminalGUIMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.ServerGuIMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.SafeGUIMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.MonitorGUIMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.KeyMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.GuiMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.FreezerGUIMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.EtGUIMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.EssenceclonerGUIMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.BlueChestGUIMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.AdminspaceMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.AdminSpaceGUIMenu;
import net.mcreator.minecraftalphaargmod.MinecraftAlphaArgModMod;

public class MinecraftAlphaArgModModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MinecraftAlphaArgModMod.MODID);
	public static final RegistryObject<MenuType<SafeGUIMenu>> SAFE_GUI = REGISTRY.register("safe_gui", () -> IForgeMenuType.create(SafeGUIMenu::new));
	public static final RegistryObject<MenuType<FreezerGUIMenu>> FREEZER_GUI = REGISTRY.register("freezer_gui", () -> IForgeMenuType.create(FreezerGUIMenu::new));
	public static final RegistryObject<MenuType<EtGUIMenu>> ET_GUI = REGISTRY.register("et_gui", () -> IForgeMenuType.create(EtGUIMenu::new));
	public static final RegistryObject<MenuType<EssenceclonerGUIMenu>> ESSENCECLONER_GUI = REGISTRY.register("essencecloner_gui", () -> IForgeMenuType.create(EssenceclonerGUIMenu::new));
	public static final RegistryObject<MenuType<GuiMenu>> GUI = REGISTRY.register("gui", () -> IForgeMenuType.create(GuiMenu::new));
	public static final RegistryObject<MenuType<TerminalGUIMenu>> TERMINAL_GUI = REGISTRY.register("terminal_gui", () -> IForgeMenuType.create(TerminalGUIMenu::new));
	public static final RegistryObject<MenuType<ServerGuIMenu>> SERVER_GU_I = REGISTRY.register("server_gu_i", () -> IForgeMenuType.create(ServerGuIMenu::new));
	public static final RegistryObject<MenuType<KeyMenu>> KEY = REGISTRY.register("key", () -> IForgeMenuType.create(KeyMenu::new));
	public static final RegistryObject<MenuType<AdminspaceMenu>> ADMINSPACE = REGISTRY.register("adminspace", () -> IForgeMenuType.create(AdminspaceMenu::new));
	public static final RegistryObject<MenuType<AdminSpaceGUIMenu>> ADMIN_SPACE_GUI = REGISTRY.register("admin_space_gui", () -> IForgeMenuType.create(AdminSpaceGUIMenu::new));
	public static final RegistryObject<MenuType<MonitorGUIMenu>> MONITOR_GUI = REGISTRY.register("monitor_gui", () -> IForgeMenuType.create(MonitorGUIMenu::new));
	public static final RegistryObject<MenuType<BlueChestGUIMenu>> BLUE_CHEST_GUI = REGISTRY.register("blue_chest_gui", () -> IForgeMenuType.create(BlueChestGUIMenu::new));
}

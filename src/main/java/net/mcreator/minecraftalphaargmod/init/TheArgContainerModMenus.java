
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.minecraftalphaargmod.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;

import net.mcreator.minecraftalphaargmod.world.inventory.WarningMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.TtcdconsoleMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.TerminalGUIMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.SkyBuilderGUIMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.ServerGuIMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.SafeGUIMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.MonitorGUIMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.KeyMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.Key1Menu;
import net.mcreator.minecraftalphaargmod.world.inventory.GuiMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.GeneratorGuiMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.FreezerGUIMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.EtGUIMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.EssenceclonerGUIMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.DevChatGUIMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.BlueChestGUIMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.BallsOnlineGuiMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.AuthenticatorGUiMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.AdminspaceMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.AdminSpaceGUIMenu;
import net.mcreator.minecraftalphaargmod.TheArgContainerMod;

import javax.annotation.Nullable;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TheArgContainerModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, TheArgContainerMod.MODID);
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
	public static final RegistryObject<MenuType<Key1Menu>> KEY_1 = REGISTRY.register("key_1", () -> IForgeMenuType.create(Key1Menu::new));
	public static final RegistryObject<MenuType<WarningMenu>> WARNING = REGISTRY.register("warning", () -> IForgeMenuType.create(WarningMenu::new));
	public static final RegistryObject<MenuType<DevChatGUIMenu>> DEV_CHAT_GUI = REGISTRY.register("dev_chat_gui", () -> IForgeMenuType.create(DevChatGUIMenu::new));
	public static final RegistryObject<MenuType<BallsOnlineGuiMenu>> BALLS_ONLINE_GUI = REGISTRY.register("balls_online_gui", () -> IForgeMenuType.create(BallsOnlineGuiMenu::new));
	public static final RegistryObject<MenuType<TtcdconsoleMenu>> TTCDCONSOLE = REGISTRY.register("ttcdconsole", () -> IForgeMenuType.create(TtcdconsoleMenu::new));
	public static final RegistryObject<MenuType<GeneratorGuiMenu>> GENERATOR_GUI = REGISTRY.register("generator_gui", () -> IForgeMenuType.create(GeneratorGuiMenu::new));
	public static final RegistryObject<MenuType<SkyBuilderGUIMenu>> SKY_BUILDER_GUI = REGISTRY.register("sky_builder_gui", () -> IForgeMenuType.create(SkyBuilderGUIMenu::new));
	public static final RegistryObject<MenuType<AuthenticatorGUiMenu>> AUTHENTICATOR_G_UI = REGISTRY.register("authenticator_g_ui", () -> IForgeMenuType.create(AuthenticatorGUiMenu::new));

	public static void setText(String boxname, String value, @Nullable ServerPlayer player) {
		if (player != null) {
			TheArgContainerMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new GuiSyncMessage(boxname, value));
		} else {
			TheArgContainerMod.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new GuiSyncMessage(boxname, value));
		}
	}

	public static class GuiSyncMessage {
		private final String textboxid;
		private final String data;

		public GuiSyncMessage(FriendlyByteBuf buffer) {
			this.textboxid = buffer.readComponent().getString();
			this.data = buffer.readComponent().getString();
		}

		public GuiSyncMessage(String textboxid, String data) {
			this.textboxid = textboxid;
			this.data = data;
		}

		public static void buffer(GuiSyncMessage message, FriendlyByteBuf buffer) {
			buffer.writeComponent(Component.literal(message.textboxid));
			buffer.writeComponent(Component.literal(message.data));
		}

		public static void handleData(GuiSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			context.enqueueWork(() -> {
				if (!context.getDirection().getReceptionSide().isServer()) {
					TheArgContainerModScreens.handleTextBoxMessage(message);
				}
			});
			context.setPacketHandled(true);
		}

		String editbox() {
			return this.textboxid;
		}

		String value() {
			return this.data;
		}
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		TheArgContainerMod.addNetworkMessage(GuiSyncMessage.class, GuiSyncMessage::buffer, GuiSyncMessage::new, GuiSyncMessage::handleData);
	}
}

package net.mcreator.minecraftalphaargmod;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.Minecraft;

import net.mcreator.minecraftalphaargmod.world.features.StructureFeature;
import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModVillagerProfessions;
import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModTabs;
import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModSounds;
import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModPaintings;
import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModMenus;
import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModItems;
import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModFeatures;
import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModEntities;
import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModBlocks;
import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModBlockEntities;

import java.util.function.Supplier;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.AbstractMap;

import java.lang.reflect.Field;

import java.io.StringWriter;
import java.io.PrintWriter;

@Mod("minecraft_alpha_arg_mod")
public class MinecraftAlphaArgModMod {
	public static final Logger LOGGER = LogManager.getLogger(MinecraftAlphaArgModMod.class);
	public static final String MODID = "minecraft_alpha_arg_mod";

	public MinecraftAlphaArgModMod() {
		// Start of user code block mod constructor
		// End of user code block mod constructor
		MinecraftForge.EVENT_BUS.register(this);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		MinecraftAlphaArgModModSounds.REGISTRY.register(bus);
		MinecraftAlphaArgModModBlocks.REGISTRY.register(bus);
		MinecraftAlphaArgModModBlockEntities.REGISTRY.register(bus);
		MinecraftAlphaArgModModItems.REGISTRY.register(bus);
		MinecraftAlphaArgModModEntities.REGISTRY.register(bus);

		MinecraftAlphaArgModModTabs.REGISTRY.register(bus);
		MinecraftAlphaArgModModFeatures.REGISTRY.register(bus);
		StructureFeature.REGISTRY.register(bus);

		MinecraftAlphaArgModModPaintings.REGISTRY.register(bus);

		MinecraftAlphaArgModModVillagerProfessions.PROFESSIONS.register(bus);
		MinecraftAlphaArgModModMenus.REGISTRY.register(bus);
		// Start of user code block mod init
		// End of user code block mod init
	}

	// Start of user code block mod methods
	// End of user code block mod methods
	public static class TextboxSetMessage {
		private final String textboxid;
		private final String data;

		public TextboxSetMessage(FriendlyByteBuf buffer) {
			this.textboxid = buffer.readUtf();
			this.data = buffer.readUtf();
		}

		public TextboxSetMessage(String textboxid, String data) {
			this.textboxid = textboxid;
			this.data = data;
		}

		public static void buffer(TextboxSetMessage message, FriendlyByteBuf buffer) {
			buffer.writeUtf(message.textboxid);
			buffer.writeUtf(message.data);
		}

		public static void handler(TextboxSetMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			context.enqueueWork(() -> {
				if (!context.getDirection().getReceptionSide().isServer() && message.data != null) {
					Screen currentScreen = Minecraft.getInstance().screen;
					Map<String, EditBox> textFieldsMap = new HashMap<>();
					if (currentScreen != null) {
						Field[] fields = currentScreen.getClass().getDeclaredFields();
						for (Field field : fields) {
							if (EditBox.class.isAssignableFrom(field.getType())) {
								try {
									field.setAccessible(true);
									EditBox textField = (EditBox) field.get(currentScreen);
									if (textField != null) {
										textFieldsMap.put(field.getName(), textField);
									}
								} catch (IllegalAccessException ex) {
									StringWriter sw = new StringWriter();
									PrintWriter pw = new PrintWriter(sw);
									ex.printStackTrace(pw);
									String exceptionAsString = sw.toString();
									MinecraftAlphaArgModMod.LOGGER.error(exceptionAsString);
								}
							}
						}
					}
					if (textFieldsMap.get(message.textboxid) != null) {
						textFieldsMap.get(message.textboxid).setValue(message.data);
					}
				}
			});
			context.setPacketHandled(true);
		}
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class initer {
		@SubscribeEvent
		public static void init(FMLCommonSetupEvent event) {
			MinecraftAlphaArgModMod.addNetworkMessage(TextboxSetMessage.class, TextboxSetMessage::buffer, TextboxSetMessage::new, TextboxSetMessage::handler);
		}
	}

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	private static int messageID = 0;

	public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
		PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
		messageID++;
	}

	private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
			workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
			workQueue.forEach(work -> {
				work.setValue(work.getValue() - 1);
				if (work.getValue() == 0)
					actions.add(work);
			});
			actions.forEach(e -> e.getKey().run());
			workQueue.removeAll(actions);
		}
	}
}

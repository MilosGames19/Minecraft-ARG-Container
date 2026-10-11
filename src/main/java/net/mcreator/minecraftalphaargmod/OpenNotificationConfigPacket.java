package net.mcreator.minecraftalphaargmod.network;

import net.mcreator.minecraftalphaargmod.NotificationPositionCommand;
import net.mcreator.minecraftalphaargmod.TheArgContainerMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class OpenNotificationConfigPacket {

	public OpenNotificationConfigPacket() {
	}

	public OpenNotificationConfigPacket(FriendlyByteBuf buffer) {
	}

	public static void buffer(OpenNotificationConfigPacket message, FriendlyByteBuf buffer) {
	}

	public static void handler(OpenNotificationConfigPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> NotificationPositionCommand.openNotificationConfig());
		});
		context.setPacketHandled(true);
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		TheArgContainerMod.addNetworkMessage(OpenNotificationConfigPacket.class, OpenNotificationConfigPacket::buffer, OpenNotificationConfigPacket::new, OpenNotificationConfigPacket::handler);
	}
}
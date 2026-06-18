package net.mcreator.minecraftalphaargmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerNotificationPacket {

	public final String text;
	public final int color;
	public final boolean fromServer;

	public ServerNotificationPacket(String text, int color, boolean fromServer) {
		this.text = text;
		this.color = color;
		this.fromServer = fromServer;
	}

	public static void encode(ServerNotificationPacket msg, FriendlyByteBuf buf) {
		buf.writeUtf(msg.text);
		buf.writeInt(msg.color);
		buf.writeBoolean(msg.fromServer);
	}

	public static ServerNotificationPacket decode(FriendlyByteBuf buf) {
		return new ServerNotificationPacket(buf.readUtf(), buf.readInt(), buf.readBoolean());
	}

	public static void handle(ServerNotificationPacket msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> net.mcreator.minecraftalphaargmod.client.NotificationOverlay.push(msg.text, msg.color, msg.fromServer));
		ctx.get().setPacketHandled(true);
	}
}
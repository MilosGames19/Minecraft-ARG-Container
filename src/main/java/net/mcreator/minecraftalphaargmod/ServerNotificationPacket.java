package net.mcreator.minecraftalphaargmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ServerNotificationPacket {

    @Nullable public final String title;
    public final String text;
    public final int titleColor;
    public final int messageColor;
    public final boolean fromServer;

    public ServerNotificationPacket(@Nullable String title, String text, int titleColor, int messageColor, boolean fromServer) {
        this.title = (title != null && !title.isEmpty()) ? title : null;
        this.text = text;
        this.titleColor = titleColor;
        this.messageColor = messageColor;
        this.fromServer = fromServer;
    }

    public static void encode(ServerNotificationPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.title != null);
        if (msg.title != null) {
            buf.writeUtf(msg.title);
        }
        buf.writeUtf(msg.text);
        buf.writeInt(msg.titleColor);
        buf.writeInt(msg.messageColor);
        buf.writeBoolean(msg.fromServer);
    }

    public static ServerNotificationPacket decode(FriendlyByteBuf buf) {
        String title = buf.readBoolean() ? buf.readUtf() : null;
        String text = buf.readUtf();
        int titleColor = buf.readInt();
        int messageColor = buf.readInt();
        boolean fromServer = buf.readBoolean();
        return new ServerNotificationPacket(title, text, titleColor, messageColor, fromServer);
    }

    public static void handle(ServerNotificationPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
            net.mcreator.minecraftalphaargmod.client.NotificationOverlay.push(
                msg.title, msg.text, msg.titleColor, msg.messageColor, msg.fromServer));
        ctx.get().setPacketHandled(true);
    }
}
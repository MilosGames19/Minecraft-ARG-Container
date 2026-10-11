package net.mcreator.minecraftalphaargmod.capes.network;

import net.mcreator.minecraftalphaargmod.capes.CapeRegistry;
import net.mcreator.minecraftalphaargmod.capes.PlayerCapeData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SelectCapePacket {

    private final String capeId;

    public SelectCapePacket(String capeId) {
        this.capeId = capeId;
    }

    public static void encode(SelectCapePacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.capeId != null);
        if (packet.capeId != null) {
            buf.writeUtf(packet.capeId);
        }
    }

    public static SelectCapePacket decode(FriendlyByteBuf buf) {
        String capeId = buf.readBoolean() ? buf.readUtf() : null;
        return new SelectCapePacket(capeId);
    }

    public static void handle(SelectCapePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) return;

            if (!CapeRegistry.hasAnyRole(sender)) {
                return;
            }

            if (packet.capeId != null && !CapeRegistry.isSelectable(sender, packet.capeId)) {
                return;
            }

            PlayerCapeData.setSelectedCape(sender, packet.capeId);
            CapeNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sender), CapeUpdatePacket.of(sender, packet.capeId));
        });
        ctx.get().setPacketHandled(true);
    }
}
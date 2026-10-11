package net.mcreator.minecraftalphaargmod.capes.network;

import net.mcreator.minecraftalphaargmod.capes.client.ClientCapeCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CapeUpdatePacket {

    private final UUID playerUuid;
    private final String capeId;

    public CapeUpdatePacket(UUID playerUuid, String capeId) {
        this.playerUuid = playerUuid;
        this.capeId = capeId;
    }

    public static CapeUpdatePacket of(ServerPlayer player, String capeId) {
        return new CapeUpdatePacket(player.getUUID(), capeId);
    }

    public static void encode(CapeUpdatePacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.playerUuid);
        buf.writeBoolean(packet.capeId != null);
        if (packet.capeId != null) {
            buf.writeUtf(packet.capeId);
        }
    }

    public static CapeUpdatePacket decode(FriendlyByteBuf buf) {
        UUID playerUuid = buf.readUUID();
        String capeId = buf.readBoolean() ? buf.readUtf() : null;
        return new CapeUpdatePacket(playerUuid, capeId);
    }

    public static void handle(CapeUpdatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientCapeCache.updateCape(packet.playerUuid, packet.capeId)));
        ctx.get().setPacketHandled(true);
    }
}
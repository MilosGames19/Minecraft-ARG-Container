package net.mcreator.minecraftalphaargmod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncAdminsPacket {

    private final Set<UUID> adminUUIDs;

    public SyncAdminsPacket(Set<UUID> adminUUIDs) {
        this.adminUUIDs = adminUUIDs;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(adminUUIDs.size());
        for (UUID uuid : adminUUIDs) {
            buf.writeUUID(uuid);
        }
    }

    public static SyncAdminsPacket decode(FriendlyByteBuf buf) {
        int count = buf.readInt();
        Set<UUID> uuids = new HashSet<>();
        for (int i = 0; i < count; i++) {
            uuids.add(buf.readUUID());
        }
        return new SyncAdminsPacket(uuids);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            AdminCache.setAdmins(adminUUIDs);
        });
        ctx.get().setPacketHandled(true);
    }
}
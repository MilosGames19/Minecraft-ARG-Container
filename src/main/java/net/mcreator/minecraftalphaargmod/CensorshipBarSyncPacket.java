package net.mcreator.minecraftalphaargmod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Sent from the server (command handler) to the executing player's client,
 * so the client-only CensorshipBarRenderer can be updated safely.
 *
 * ADD/REMOVE carry a UUID. CLEAR carries no UUID (pass null).
 */
public class CensorshipBarSyncPacket {

    public enum Action { ADD, REMOVE, CLEAR }

    private final Action action;
    private final UUID targetUUID; // null for CLEAR

    public CensorshipBarSyncPacket(Action action, UUID targetUUID) {
        this.action = action;
        this.targetUUID = targetUUID;
    }

    // ── Serialization ──────────────────────────────────────────────────────────

    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(action);
        buf.writeBoolean(targetUUID != null);
        if (targetUUID != null) {
            buf.writeUUID(targetUUID);
        }
    }

    public static CensorshipBarSyncPacket decode(FriendlyByteBuf buf) {
        Action action = buf.readEnum(Action.class);
        UUID uuid = buf.readBoolean() ? buf.readUUID() : null;
        return new CensorshipBarSyncPacket(action, uuid);
    }

    // ── Handler (runs on client thread via enqueueWork) ────────────────────────

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() ->
            // DistExecutor guards against accidental class-loading on a dedicated server
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                switch (action) {
                    case ADD    -> CensorshipBarRenderer.addTargetEntity(targetUUID);
                    case REMOVE -> CensorshipBarRenderer.removeTargetEntity(targetUUID);
                    case CLEAR  -> CensorshipBarRenderer.clearTargets();
                }
            })
        );
        ctx.setPacketHandled(true);
    }
}
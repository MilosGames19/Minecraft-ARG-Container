package net.mcreator.minecraftalphaargmod;

import org.apache.logging.log4j.core.appender.rolling.action.Action;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;
import java.util.UUID;

public class CensorshipBarSyncPacket {
	public enum Action {
		ADD, REMOVE, CLEAR
	}

	private final Action action;
	private final UUID targetUUID;

	public CensorshipBarSyncPacket(Action action, UUID targetUUID) {
		this.action = action;
		this.targetUUID = targetUUID;
	}

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

	public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			switch (action) {
				case ADD -> CensorshipBarRenderer.addTargetEntity(targetUUID);
				case REMOVE -> CensorshipBarRenderer.removeTargetEntity(targetUUID);
				case CLEAR -> CensorshipBarRenderer.clearTargets();
			}
		}));
		ctx.setPacketHandled(true);
	}
}

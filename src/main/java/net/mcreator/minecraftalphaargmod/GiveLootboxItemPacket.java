package net.mcreator.minecraftalphaargmod.network;

import net.minecraftforge.network.NetworkEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class GiveLootboxItemPacket {
	private final ItemStack stack;

	public GiveLootboxItemPacket(ItemStack stack) {
		this.stack = stack;
	}

	public static void encode(GiveLootboxItemPacket msg, FriendlyByteBuf buf) {
		buf.writeItem(msg.stack);
	}

	public static GiveLootboxItemPacket decode(FriendlyByteBuf buf) {
		return new GiveLootboxItemPacket(buf.readItem());
	}

	public static void handle(GiveLootboxItemPacket msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();
			if (player == null)
				return;
			boolean added = player.addItem(msg.stack.copy());
			if (!added) {
				player.drop(msg.stack.copy(), false);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}

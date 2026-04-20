package net.mcreator.minecraftalphaargmod;

import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkDirection;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class CensorshipBarNetwork {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation("the_arg_container", "censorship"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	private static int nextId = 0;

	public static void register() {
		CHANNEL.registerMessage(nextId++, CensorshipBarSyncPacket.class, CensorshipBarSyncPacket::encode, CensorshipBarSyncPacket::decode, CensorshipBarSyncPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}

	public static void sendToPlayer(ServerPlayer player, CensorshipBarSyncPacket packet) {
		CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
	}
}

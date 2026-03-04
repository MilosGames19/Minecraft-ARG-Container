package net.mcreator.minecraftalphaargmod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ArgNetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    // Null until onCommonSetup() runs — do NOT access before mod setup completes.
    private static SimpleChannel channel;

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        channel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation("the_arg_container", "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );

        channel.registerMessage(
                0,
                SyncAdminsPacket.class,
                SyncAdminsPacket::encode,
                SyncAdminsPacket::decode,
                SyncAdminsPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
    }

    // Sends the current admin list to a specific player
    public static void sendAdminsToPlayer(ServerPlayer player) {
        Set<UUID> adminUUIDs = collectAdmins(player);
        channel.send(PacketDistributor.PLAYER.with(() -> player), new SyncAdminsPacket(adminUUIDs));
    }

    // Broadcasts the updated admin list to ALL online players (used when op status changes)
    public static void broadcastAdmins(ServerPlayer anyOnlinePlayer) {
        for (ServerPlayer player : anyOnlinePlayer.getServer().getPlayerList().getPlayers()) {
            Set<UUID> adminUUIDs = collectAdmins(player);
            channel.send(PacketDistributor.PLAYER.with(() -> player), new SyncAdminsPacket(adminUUIDs));
        }
    }

    // Collects all admin UUIDs from the server
    private static Set<UUID> collectAdmins(ServerPlayer player) {
        Set<UUID> adminUUIDs = new HashSet<>();
        for (ServerPlayer online : player.getServer().getPlayerList().getPlayers()) {
            if (online.hasPermissions(4)) {
                adminUUIDs.add(online.getUUID());
            }
        }
        return adminUUIDs;
    }
}
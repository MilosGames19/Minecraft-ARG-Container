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

import java.util.Optional;


@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.MOD)
public class MinesweeperNetwork {

    private static final String PROTOCOL = "ms1";
    private static SimpleChannel channel;

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        channel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation("the_arg_container", "minesweeper_sync"),
                () -> PROTOCOL,
                PROTOCOL::equals,
                PROTOCOL::equals
        );

        channel.registerMessage(
                0,
                MinesweeperSyncPacket.class,
                MinesweeperSyncPacket::encode,
                MinesweeperSyncPacket::decode,
                MinesweeperSyncPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
    }

    public static void sendToPlayer(ServerPlayer player, MinesweeperSyncPacket packet) {
        if (channel == null) {
            throw new IllegalStateException(
                    "MinesweeperNetwork channel is null — onCommonSetup() has not run yet.");
        }
        channel.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
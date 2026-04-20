package net.mcreator.minecraftalphaargmod.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class SkyBuilderNetwork {

    private SkyBuilderNetwork() {}

    private static final String PROTOCOL = "1";

    public static SimpleChannel CHANNEL;

    public static void register() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation("the_arg_container", "sky_builder"),
                () -> PROTOCOL,
                PROTOCOL::equals,
                PROTOCOL::equals
        );

        int id = 0;
        CHANNEL.registerMessage(
                id++,
                CraftSkyBuilderPacket.class,
                CraftSkyBuilderPacket::encode,
                CraftSkyBuilderPacket::decode,
                CraftSkyBuilderPacket::handle
        );
    }
}
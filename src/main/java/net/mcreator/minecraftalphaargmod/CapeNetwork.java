package net.mcreator.minecraftalphaargmod.capes.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class CapeNetwork {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("the_arg_container", "capes"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;
    private static boolean registered = false;

    private static int nextId() {
        return packetId++;
    }

    public static void register() {
        if (registered) {
            throw new IllegalStateException("CapeNetwork.register() called twice");
        }
        registered = true;
        CHANNEL.registerMessage(nextId(), OpenCapeSelectorPacket.class, OpenCapeSelectorPacket::encode, OpenCapeSelectorPacket::decode, OpenCapeSelectorPacket::handle);
        CHANNEL.registerMessage(nextId(), SelectCapePacket.class, SelectCapePacket::encode, SelectCapePacket::decode, SelectCapePacket::handle);
        CHANNEL.registerMessage(nextId(), CapeUpdatePacket.class, CapeUpdatePacket::encode, CapeUpdatePacket::decode, CapeUpdatePacket::handle);
    }
}
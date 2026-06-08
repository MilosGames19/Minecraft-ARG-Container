package net.mcreator.minecraftalphaargmod.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;
import java.util.Optional;

public final class CustomBookNetwork {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("the_arg_container", "custom_book_net"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    private CustomBookNetwork() {}

    public static void register() {
        CHANNEL.registerMessage(0,
                CustomBookSavePacket.class,
                CustomBookSavePacket::encode,
                CustomBookSavePacket::decode,
                CustomBookSavePacket::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    /** Save without signing */
    public static void sendSave(int slot, List<String> pages) {
        CHANNEL.sendToServer(new CustomBookSavePacket(slot, pages, Optional.empty()));
    }

    /** Save and sign — converts item to written_document on server */
    public static void sendSign(int slot, List<String> pages, String title) {
        CHANNEL.sendToServer(new CustomBookSavePacket(slot, pages, Optional.of(title.trim())));
    }
}
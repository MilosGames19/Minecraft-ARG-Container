package net.mcreator.minecraftalphaargmod.network;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.mcreator.minecraftalphaargmod.item.WritableDocumentItem;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CustomBookSavePacket {

    static final int MAX_PAGES = 50;
    static final int MAX_PAGE_CHARS = 1024;
    static final int MAX_TITLE_CHARS = 32;
    private static final String NBT_PAGES = "pages";
    private static final String NBT_TITLE = "title";
    private static final String NBT_AUTHOR = "author";

    private final int slot;
    private final List<String> pages;
    @Nullable
    private final String title;

    public CustomBookSavePacket(int slot, List<String> pages, @Nullable String title) {
        this.slot = slot;
        this.pages = List.copyOf(pages);
        this.title = title;
    }

    public static void encode(CustomBookSavePacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.slot);
        buf.writeVarInt(pkt.pages.size());
        for (String page : pkt.pages) {
            buf.writeUtf(page, MAX_PAGE_CHARS);
        }
        buf.writeBoolean(pkt.title != null);
        if (pkt.title != null) {
            buf.writeUtf(pkt.title, MAX_TITLE_CHARS);
        }
    }

    public static CustomBookSavePacket decode(FriendlyByteBuf buf) {
        int slot = buf.readVarInt();
        int size = buf.readVarInt();
        if (size < 0 || size > MAX_PAGES) {
            throw new IllegalStateException("CustomBookSavePacket: invalid page count " + size);
        }
        List<String> pages = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            pages.add(buf.readUtf(MAX_PAGE_CHARS));
        }
        String title = buf.readBoolean() ? buf.readUtf(MAX_TITLE_CHARS) : null;
        return new CustomBookSavePacket(slot, pages, title);
    }

    public static void handle(CustomBookSavePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null)
                return;
            if (pkt.slot < 0 || pkt.slot > 40)
                return;

            ItemStack stack = player.getInventory().getItem(pkt.slot);
            if (!(stack.getItem() instanceof WritableDocumentItem)) {
                net.mcreator.minecraftalphaargmod.TheArgContainerMod.LOGGER.warn("CustomBookSavePacket: slot " + pkt.slot + " is not a writable document");
                return;
            }

            ListTag list = new ListTag();
            for (String page : pkt.pages)
                list.add(StringTag.valueOf(page));

            if (pkt.title != null) {
                var writtenItem = ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation("the_arg_container", "written_document"));
                if (writtenItem == null)
                    return;

                ItemStack signed = new ItemStack(writtenItem);
                signed.getOrCreateTag().put(NBT_PAGES, list);
                signed.getOrCreateTag().putString(NBT_TITLE, pkt.title.trim());
                signed.getOrCreateTag().putString(NBT_AUTHOR, player.getGameProfile().getName());
                player.getInventory().setItem(pkt.slot, signed);
            } else {
                stack.getOrCreateTag().put(NBT_PAGES, list);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public int getSlot() {
        return slot;
    }

    public List<String> getPages() {
        return pages;
    }

    @Nullable
    public String getTitle() {
        return title;
    }
}

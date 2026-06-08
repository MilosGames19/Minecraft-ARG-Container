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

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class CustomBookSavePacket {

    static final int MAX_PAGES      = 50;
    static final int MAX_PAGE_CHARS = 1024;
    static final int MAX_TITLE_CHARS = 32;
    private static final String NBT_PAGES  = "pages";
    private static final String NBT_TITLE  = "title";
    private static final String NBT_AUTHOR = "author";

    private final int             slot;
    private final List<String>    pages;
    private final Optional<String> title; // present = player is signing

    public CustomBookSavePacket(int slot, List<String> pages, Optional<String> title) {
        this.slot  = slot;
        this.pages = List.copyOf(pages);
        this.title = title;
    }

    public static void encode(CustomBookSavePacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.slot);
        buf.writeCollection(pkt.pages, (b, page) -> b.writeUtf(page, MAX_PAGE_CHARS));
        buf.writeOptional(pkt.title, (b, t) -> b.writeUtf(t, MAX_TITLE_CHARS));
    }

    public static CustomBookSavePacket decode(FriendlyByteBuf buf) {
        int slot = buf.readVarInt();
        List<String> pages = buf.readList(b -> b.readUtf(MAX_PAGE_CHARS));
        Optional<String> title = buf.readOptional(b -> b.readUtf(MAX_TITLE_CHARS));
        return new CustomBookSavePacket(slot, pages, title);
    }

    public static void handle(CustomBookSavePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (pkt.slot < 0 || pkt.slot > 40) return;
            if (pkt.pages.size() > MAX_PAGES) return;
            for (String page : pkt.pages)
                if (page.length() > MAX_PAGE_CHARS) return;

            ItemStack stack = player.getInventory().getItem(pkt.slot);
            if (!(stack.getItem() instanceof WritableDocumentItem)) return;

            // Build pages tag
            ListTag list = new ListTag();
            for (String page : pkt.pages) list.add(StringTag.valueOf(page));

            if (pkt.title.isPresent()) {
                // Signing — convert WritableDocument → WrittenDocument
                var writtenItem = ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation("the_arg_container", "written_document"));
                if (writtenItem == null) return;

                ItemStack signed = new ItemStack(writtenItem);
                signed.getOrCreateTag().put(NBT_PAGES, list);
                signed.getOrCreateTag().putString(NBT_TITLE, pkt.title.get().trim());
                signed.getOrCreateTag().putString(NBT_AUTHOR,
                        player.getGameProfile().getName());
                player.getInventory().setItem(pkt.slot, signed);
            } else {
                // Just saving edits
                stack.getOrCreateTag().put(NBT_PAGES, list);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public int             getSlot()  { return slot; }
    public List<String>    getPages() { return pages; }
    public Optional<String> getTitle() { return title; }
}
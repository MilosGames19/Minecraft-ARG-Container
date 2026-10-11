package net.mcreator.minecraftalphaargmod.client.book;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class BookDefinition {

    public static final ResourceLocation VANILLA_BOOK_TEXTURE = new ResourceLocation("minecraft", "textures/gui/book.png");

    private final Component title;
    private final ResourceLocation texture;
    private final boolean writable;
    private final int maxPages;

    public BookDefinition(Component title, ResourceLocation texture, boolean writable, int maxPages) {
        this.title = title;
        this.texture = texture;
        this.writable = writable;
        this.maxPages = maxPages;
    }

    public Component getTitle() {
        return title;
    }

    public ResourceLocation getTexture() {
        return texture != null ? texture : VANILLA_BOOK_TEXTURE;
    }

    public boolean isCustomTexture() {
        return texture != null;
    }

    public boolean isWritable() {
        return writable;
    }

    public int getMaxPages() {
        return maxPages;
    }
}

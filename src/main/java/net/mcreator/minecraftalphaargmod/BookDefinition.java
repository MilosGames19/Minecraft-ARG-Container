package net.mcreator.minecraftalphaargmod.client.book;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Immutable descriptor for a registered custom book item.
 * Build with {@link #builder(Component)}.
 */
public final class BookDefinition {

    /** Vanilla book texture — used when no custom texture is specified. */
    public static final ResourceLocation VANILLA_BOOK_TEXTURE =
            new ResourceLocation("minecraft", "textures/gui/book.png");

    private final Component title;
    private final ResourceLocation texture; // null = vanilla fallback
    private final boolean writable;
    private final int maxPages;

    private BookDefinition(Builder b) {
        this.title    = b.title;
        this.texture  = b.texture;
        this.writable = b.writable;
        this.maxPages = b.maxPages;
    }

    public Component      getTitle()         { return title; }
    public ResourceLocation getTexture()     { return texture != null ? texture : VANILLA_BOOK_TEXTURE; }
    public boolean        isCustomTexture()  { return texture != null; }
    public boolean        isWritable()       { return writable; }
    public int            getMaxPages()      { return maxPages; }

    public static Builder builder(Component title) { return new Builder(title); }

    public static final class Builder {
        private final Component title;
        private ResourceLocation texture = null;
        private boolean writable = false;
        private int maxPages = 50;

        private Builder(Component title) { this.title = title; }

        /** Supply a custom 256×256 GUI texture sheet. Omit to use the vanilla book. */
        public Builder texture(ResourceLocation loc) { this.texture = loc; return this; }

        /** Allow the player to write/edit pages. */
        public Builder writable() { this.writable = true; return this; }

        /** Cap the number of pages (default 50). */
        public Builder maxPages(int max) { this.maxPages = max; return this; }

        public BookDefinition build() { return new BookDefinition(this); }
    }
}
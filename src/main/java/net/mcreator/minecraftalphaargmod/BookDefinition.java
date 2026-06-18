package net.mcreator.minecraftalphaargmod.client.book;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class BookDefinition {

	public static final ResourceLocation VANILLA_BOOK_TEXTURE = new ResourceLocation("minecraft", "textures/gui/book.png");

	private final Component title;
	private final ResourceLocation texture;
	private final boolean writable;
	private final int maxPages;

	private BookDefinition(Builder b) {
		this.title = b.title;
		this.texture = b.texture;
		this.writable = b.writable;
		this.maxPages = b.maxPages;
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

	public static Builder builder(Component title) {
		return new Builder(title);
	}

	public static final class Builder {
		private final Component title;
		private ResourceLocation texture = null;
		private boolean writable = false;
		private int maxPages = 50;

		private Builder(Component title) {
			this.title = title;
		}

		public Builder texture(ResourceLocation loc) {
			this.texture = loc;
			return this;
		}

		public Builder writable() {
			this.writable = true;
			return this;
		}

		public Builder maxPages(int max) {
			this.maxPages = max;
			return this;
		}

		public BookDefinition build() {
			return new BookDefinition(this);
		}
	}
}
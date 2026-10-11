package net.mcreator.minecraftalphaargmod.client.book;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public final class BookNavButton extends Button {

    private static final int BUTTON_W = 23;
    private static final int BUTTON_H = 13;
    private static final int HOVER_TEX_X = 23;
    private static final int NEXT_TEX_Y = 192;
    private static final int PREV_TEX_Y = 205;

    private final ResourceLocation texture;
    private final boolean isNext;

    public BookNavButton(int x, int y, boolean isNext, ResourceLocation texture, OnPress action) {
        super(x, y, BUTTON_W, BUTTON_H, CommonComponents.EMPTY, action, Button.DEFAULT_NARRATION);
        this.texture = texture;
        this.isNext = isNext;
    }

    @Override
    public void renderWidget(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        int texX = this.isHoveredOrFocused() ? HOVER_TEX_X : 0;
        int texY = this.isNext ? NEXT_TEX_Y : PREV_TEX_Y;
        gfx.blit(this.texture, this.getX(), this.getY(), texX, texY, this.width, this.height);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
    }
}

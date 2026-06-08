package net.mcreator.minecraftalphaargmod.mixins;

import net.mcreator.minecraftalphaargmod.client.console.AlphaCommandManager;
import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class NoCommandScreenMixin extends Screen {

    @Shadow CommandSuggestions commandSuggestions;
    @Shadow protected EditBox input;

    protected NoCommandScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        if (!McconfigConfiguration.ALPHAVER_CONSOLE.get()) return;

        commandSuggestions = new CommandSuggestions(
                this.minecraft, (ChatScreen)(Object)this, this.input, this.font,
                false, false, 1, 10, true, -805306368) {
            @Override public void updateCommandInfo() {}
            @Override public void render(GuiGraphics g, int mx, int my) {}
            @Override public boolean keyPressed(int key, int scan, int mod) { return false; }
            @Override public boolean mouseScrolled(double delta) { return false; }
            @Override public boolean mouseClicked(double mx, double my, int btn) { return false; }
        };
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (McconfigConfiguration.ALPHAVER_CONSOLE.get() && keyCode == GLFW.GLFW_KEY_TAB) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "handleChatInput", at = @At("HEAD"), cancellable = true)
    private void onHandleChatInput(String message, boolean addToHistory, CallbackInfoReturnable<Boolean> cir) {
        String normalized = ((ChatScreen)(Object)this).normalizeChatMessage(message);
        if (normalized.isEmpty()) {
            cir.setReturnValue(true);
            return;
        }

        if (AlphaCommandManager.isBlacklisted(normalized)) {
            String chatText = normalized.startsWith("/") ? normalized.substring(1) : normalized;
            if (addToHistory) this.minecraft.gui.getChat().addRecentChat(chatText);
            this.minecraft.player.connection.sendChat(chatText);
            cir.setReturnValue(this.minecraft.screen == (ChatScreen)(Object)this);
            return;
        }

        if (McconfigConfiguration.ALPHAVER_CONSOLE.get() && normalized.startsWith("/")) {
            if (addToHistory) this.minecraft.gui.getChat().addRecentChat(normalized);
            this.minecraft.player.connection.sendChat(normalized);
            cir.setReturnValue(this.minecraft.screen == (ChatScreen)(Object)this);
        }
    }
}
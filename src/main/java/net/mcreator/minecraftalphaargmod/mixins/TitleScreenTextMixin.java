package net.mcreator.minecraftalphaargmod.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.components.SplashRenderer;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class TitleScreenTextMixin {

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void arg_titleKeyShuffle(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (keyCode != GLFW.GLFW_KEY_R) return;

        Screen self = (Screen) (Object) this;
        if (!(self instanceof TitleScreen)) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;

        SplashRenderer newSplash = mc.getSplashManager().getSplash();
        ((TitleScreenSplashAccessorMixin) self).arg_setSplash(newSplash);
        cir.setReturnValue(true);
    }
}

// Internal testing only. Please remove this before the release.
// -Glitchy
  	
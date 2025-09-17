package net.mcreator.minecraftalphaargmod.mixins;

import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TitleScreen.class)
public interface TitleScreenSplashAccessorMixin {
    @Accessor("splash")
    void arg_setSplash(SplashRenderer splash);
}

// Internal testing only. Please remove this before the release.
// -Glitchy
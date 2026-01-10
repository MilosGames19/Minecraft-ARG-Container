//Thanks to EBALIA for showing me how to use mixin register.
package net.mcreator.minecraftalphaargmod;

import java.util.List;

public class MixinRegister {
    public List<String> mixins;

    public MixinRegister() {
        this.mixins = List.of(
            "net.mcreator.minecraftalphaargmod.mixin.ChatTypeBoundMixin",
            "net.mcreator.minecraftalphaargmod.mixin.TitleScreenTextMixin",
            "net.mcreator.minecraftalphaargmod.mixin.TitleScreenSplashAccessorMixin"
        );
    }
}
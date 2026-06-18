//Thanks to EBALIA for showing me how to use mixin register.
package net.mcreator.minecraftalphaargmod;

import java.util.List;

public class MixinRegister {

    public List<String> mixins;

    public MixinRegister() {
        this.mixins = List.of(
            "net.mcreator.minecraftalphaargmod.mixins.ChatTypeBoundMixin",
            "net.mcreator.minecraftalphaargmod.mixins.TitleScreenTextMixin",
            "net.mcreator.minecraftalphaargmod.mixins.TitleScreenSplashAccessorMixin",
            "net.mcreator.minecraftalphaargmod.mixins.NoCommandScreenMixin",
            "net.mcreator.minecraftalphaargmod.mixins.CommandSuggestionFilterMixin",
            "net.mcreator.minecraftalphaargmod.mixins.AbstractButtonRenderMixin",
            "net.mcreator.minecraftalphaargmod.mixins.AbstractSliderButtonRenderMixin"
            //"net.mcreator.minecraftalphaargmod.mixins.DatapackFailureScreenMixin"
            
        );
    }
}
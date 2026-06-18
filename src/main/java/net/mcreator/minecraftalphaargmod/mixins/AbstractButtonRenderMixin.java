package net.mcreator.minecraftalphaargmod.mixins;

import net.mcreator.minecraftalphaargmod.animation.ButtonFlyInTracker;
import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(AbstractButton.class)
public class AbstractButtonRenderMixin {

    private static final ThreadLocal<Integer> activeOffset = ThreadLocal.withInitial(() -> 0);

    private static final List<Class<? extends Screen>> EXCLUDED_SCREENS = List.of(
        //Glitchy, add excluded screen classes here:
        net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen.class,
        net.mcreator.minecraftalphaargmod.PaletteScreen.class,
        net.mcreator.minecraftalphaargmod.client.ModConfigScreen.class,
        net.minecraftforge.client.gui.ModListScreen.class
    );

    @Inject(
        method = "renderWidget(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
        at = @At("HEAD")
    )
    private void flyIn_head(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!McconfigConfiguration.ALPHAVER_BUTTON_ANIMATION.get())  {
            activeOffset.set(0);
            return;
        }

        Screen currentScreen = Minecraft.getInstance().screen;
        if (currentScreen != null && EXCLUDED_SCREENS.contains(currentScreen.getClass())) {
            activeOffset.set(0);
            return;
        }

        int screenHeight = currentScreen != null
            ? currentScreen.height
            : Minecraft.getInstance().getWindow().getGuiScaledHeight();

        int offset = ButtonFlyInTracker.getYOffset((AbstractButton) (Object) this, screenHeight);
        activeOffset.set(offset);

        if (offset != 0) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, (float) offset, 0.0F);
        }
    }

    @Inject(
        method = "renderWidget(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
        at = @At("TAIL")
    )
    private void flyIn_tail(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (activeOffset.get() != 0) {
            guiGraphics.pose().popPose();
            activeOffset.set(0);
        }
    }
}
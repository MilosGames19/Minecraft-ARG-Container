package net.mcreator.minecraftalphaargmod.client.panorama;

import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class PanoramaEventHandler {

    private PanoramaEventHandler() {}

    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof TitleScreen ts) {
            CustomPanoramaManager.onTitleScreenOpen(ts);
        }
    }

    @SubscribeEvent
    public static void onKeyPress(ScreenEvent.KeyPressed.Post event) {
        if (!(event.getScreen() instanceof TitleScreen ts)) return;
        if (!net.minecraft.client.gui.screens.Screen.hasControlDown()) return;
        int key = event.getKeyCode();
        if (key == GLFW.GLFW_KEY_RIGHT) {
            CustomPanoramaManager.cycle(ts, +1);
        } else if (key == GLFW.GLFW_KEY_LEFT) {
            CustomPanoramaManager.cycle(ts, -1);
        }
    }
}
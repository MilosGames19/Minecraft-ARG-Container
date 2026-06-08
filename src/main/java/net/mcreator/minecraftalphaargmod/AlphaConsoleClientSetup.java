package net.mcreator.minecraftalphaargmod.client.console;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AlphaConsoleClientSetup {

    public static final KeyMapping OPEN_CONSOLE_KEY = new KeyMapping(
            "key.the_arg_container.open_console",
            GLFW.GLFW_KEY_GRAVE_ACCENT,
            "key.categories.the_arg_container"
    );

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_CONSOLE_KEY);
    }

}
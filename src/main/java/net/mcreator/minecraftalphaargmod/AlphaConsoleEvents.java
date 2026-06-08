package net.mcreator.minecraftalphaargmod.client.console;

import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class AlphaConsoleEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!McconfigConfiguration.ALPHAVER_CONSOLE.get()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (AlphaConsoleClientSetup.OPEN_CONSOLE_KEY.consumeClick()) {
            if (!(mc.screen instanceof AlphaConsoleScreen)) {
                AlphaCommandManager.INSTANCE.clearInput();
                mc.setScreen(new AlphaConsoleScreen());
            }
        }
    }
}
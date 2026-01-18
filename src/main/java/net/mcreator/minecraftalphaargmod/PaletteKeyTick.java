package net.mcreator.minecraftalphaargmod;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PaletteKeyTick {
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (PaletteKeyHandler.PALETTE_KEY.consumeClick()) {
                PaletteScreen.GUI_open();
            }
        }
    }
}
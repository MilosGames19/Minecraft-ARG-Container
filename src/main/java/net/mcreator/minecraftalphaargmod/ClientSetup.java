package net.mcreator.minecraftalphaargmod;  
  
import net.mcreator.minecraftalphaargmod.client.ModConfigScreen;  
import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;  
import net.minecraftforge.api.distmarker.Dist;  
import net.minecraftforge.client.ConfigScreenHandler;  
import net.minecraftforge.eventbus.api.SubscribeEvent;  
import net.minecraftforge.fml.ModLoadingContext;  
import net.minecraftforge.fml.common.Mod;  
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;  
  
@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)  
public class ClientSetup {  
  
    @SubscribeEvent  
    public static void onClientSetup(FMLClientSetupEvent event) {  
        ModLoadingContext.get().registerExtensionPoint(  
            ConfigScreenHandler.ConfigScreenFactory.class,  
            () -> new ConfigScreenHandler.ConfigScreenFactory(  
                (mc, parent) -> new ModConfigScreen(  
                    parent,  
                    McconfigConfiguration.SPEC,  
                    "the_arg_container"  
                )  
            )  
        );  
    }  
  
    public static void openConfigScreen() {  
        var minecraft = net.minecraft.client.Minecraft.getInstance();  
        minecraft.setScreen(  
            new ModConfigScreen(  
                minecraft.screen,  
                McconfigConfiguration.SPEC,  
                "the_arg_container"  
            )  
        );  
    }  
}
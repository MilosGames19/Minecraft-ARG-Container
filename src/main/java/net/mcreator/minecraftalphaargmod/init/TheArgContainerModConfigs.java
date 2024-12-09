package net.mcreator.minecraftalphaargmod.init;

import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;
import net.mcreator.minecraftalphaargmod.TheArgContainerMod;

@Mod.EventBusSubscriber(modid = TheArgContainerMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TheArgContainerModConfigs {
	@SubscribeEvent
	public static void register(FMLConstructModEvent event) {
		event.enqueueWork(() -> {
			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, McconfigConfiguration.SPEC, "the_arg_container.toml");
		});
	}
}

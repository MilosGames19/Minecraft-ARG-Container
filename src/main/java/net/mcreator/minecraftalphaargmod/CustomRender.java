
package net.mcreator.minecraftalphaargmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.mcreator.minecraftalphaargmod.TheArgContainerMod;
import net.minecraftforge.client.event.EntityRenderersEvent;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModBlockEntities;
import net.mcreator.minecraftalphaargmod.*;
import org.mozilla.javascript.Context;
import net.minecraft.world.level.biome.TheEndBiomeSource;

@Mod.EventBusSubscriber(modid = TheArgContainerMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CustomRender {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

	//Block register
	//																Name of the blocks 
	//                                                        (remember to turn on block entity)
	event.registerBlockEntityRenderer(TheArgContainerModBlockEntities.HUB_SKY.get(), context -> new HubSkyRender());
	event.registerBlockEntityRenderer(TheArgContainerModBlockEntities.AUTHENTICATOR.get(), context -> new AuthenticatorBeamRender());
	event.registerBlockEntityRenderer(TheArgContainerModBlockEntities.THE_ULTIMATE_TRUTH.get(), context -> new TheUltimateTruthRender());
	event.registerBlockEntityRenderer(TheArgContainerModBlockEntities.THE_ULTIMATE_TRUTH_BRICKS.get(), context -> new TheUltimateTruthBricksRender());

  }
}
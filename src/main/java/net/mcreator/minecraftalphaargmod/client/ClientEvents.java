package net.mcreator.minecraftalphaargmod.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.mcreator.minecraftalphaargmod.client.renderer.RenderWireframeBlockBlockEntityRenderer;
import net.mcreator.minecraftalphaargmod.init.TheArgContainerModBlockEntities;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
            TheArgContainerModBlockEntities.RENDER_WIREFRAME_BLOCK.get(),
            RenderWireframeBlockBlockEntityRenderer::new
        );
    }
}
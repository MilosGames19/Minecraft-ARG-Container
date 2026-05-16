// Thank you, Nebula and Noodlegamer76, for making this possible.
// If you want to use this code, please credit Nebula and Noodlegamer76. Without them, this wouldn't be possible.
package net.mcreator.minecraftalphaargmod;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class FakeSkyRegistry {

    private static final List<FakeSkyFactory> factories = new ArrayList<>();

    private static final FakeSkyFactory key_portal = register("the_arg_container", "textures/preparations_sky.png");
    private static final FakeSkyFactory shattered_day = register("the_arg_container", "textures/overworldshatteredskybox.png");
    private static final FakeSkyFactory shattered_night = register("the_arg_container", "textures/shatteredskybox.png");
    private static final FakeSkyFactory debug_sky = register("the_arg_container", "textures/debug_skybox.png");
    private static final FakeSkyFactory hub_sky = register("the_arg_container", "textures/hub_sky_v2.png");

    public static FakeSkyFactory getKeyPortalFactory() { return key_portal; }
    public static FakeSkyFactory getShatteredDayFactory() { return shattered_day; }
    public static FakeSkyFactory getShatteredNightFactory() { return shattered_night; }
    public static FakeSkyFactory getDebugSkyFactory() { return debug_sky; }
    public static FakeSkyFactory getHubSkyFactory() { return hub_sky; }

    public static FakeSkyFactory register(String modId, String texturePath) {
        FakeSkyFactory factory = new FakeSkyFactory(new ResourceLocation(modId, texturePath));
        factories.add(factory);
        return factory;
    }

    @SubscribeEvent
    public static void onRenderSky(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;
        for (FakeSkyFactory factory : factories) {
            factory.bake(event);
        }
    }

    @SubscribeEvent
    public static void onRenderBlockEntities(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;
        for (FakeSkyFactory factory : factories) {
            factory.renderToMain(event);
        }
    }
}
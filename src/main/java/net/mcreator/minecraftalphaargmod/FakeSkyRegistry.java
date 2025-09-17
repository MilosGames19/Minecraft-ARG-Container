// Thank you, Nebula, for making this possible.
// If you want to use this code, please credit Nebula. Without him, this wouldn't be possible.
package net.mcreator.minecraftalphaargmod;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.RenderType;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class FakeSkyRegistry {

    private static final List<FakeSkyFactory> factories = new ArrayList<>();

    private static final FakeSkyFactory key_portal = register("the_arg_container", "textures/preparations_sky.png");
	private static final FakeSkyFactory shattered_day = register("the_arg_container", "textures/overworldshatteredskybox.png");
	private static final FakeSkyFactory shattered_night = register("the_arg_container", "textures/shatteredskybox.png");
	private static final FakeSkyFactory debug_sky = register("the_arg_container", "textures/debug_skybox.png");
	
    public static RenderType getKeyPortal() {
        return key_portal.get();
    }

    public static RenderType getShatteredDay() {
    	return shattered_day.get();	
    }

	public static RenderType getShatteredNight() {
    	return shattered_night.get();	
    }

	public static RenderType getDebugSky() {
		return debug_sky.get();
	}

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
}


package net.mcreator.minecraftalphaargmod.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.minecraftalphaargmod.entity.ObserverMobEntity;
import net.mcreator.minecraftalphaargmod.client.model.ModelObserver;

public class ObserverMobRenderer extends MobRenderer<ObserverMobEntity, ModelObserver<ObserverMobEntity>> {
	public ObserverMobRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelObserver<ObserverMobEntity>(context.bakeLayer(ModelObserver.LAYER_LOCATION)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(ObserverMobEntity entity) {
		return new ResourceLocation("the_arg_container:textures/entities/observer.png");
	}
}

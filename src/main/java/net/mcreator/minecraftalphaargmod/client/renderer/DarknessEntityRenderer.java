
package net.mcreator.minecraftalphaargmod.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.minecraftalphaargmod.entity.DarknessEntityEntity;
import net.mcreator.minecraftalphaargmod.client.model.Modelmother;

public class DarknessEntityRenderer extends MobRenderer<DarknessEntityEntity, Modelmother<DarknessEntityEntity>> {
	public DarknessEntityRenderer(EntityRendererProvider.Context context) {
		super(context, new Modelmother<DarknessEntityEntity>(context.bakeLayer(Modelmother.LAYER_LOCATION)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(DarknessEntityEntity entity) {
		return new ResourceLocation("the_arg_container:textures/entities/noised.png");
	}
}

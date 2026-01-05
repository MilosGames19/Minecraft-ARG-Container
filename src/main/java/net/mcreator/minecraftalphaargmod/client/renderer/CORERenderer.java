
package net.mcreator.minecraftalphaargmod.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.minecraftalphaargmod.entity.COREEntity;
import net.mcreator.minecraftalphaargmod.client.model.Modelcore;

public class CORERenderer extends MobRenderer<COREEntity, Modelcore<COREEntity>> {
	public CORERenderer(EntityRendererProvider.Context context) {
		super(context, new Modelcore<COREEntity>(context.bakeLayer(Modelcore.LAYER_LOCATION)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(COREEntity entity) {
		return new ResourceLocation("the_arg_container:textures/entities/core.png");
	}
}

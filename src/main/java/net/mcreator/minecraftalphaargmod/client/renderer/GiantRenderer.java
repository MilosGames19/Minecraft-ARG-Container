
package net.mcreator.minecraftalphaargmod.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.minecraftalphaargmod.entity.GiantEntity;
import net.mcreator.minecraftalphaargmod.client.model.ModelZombieModel;

import com.mojang.blaze3d.vertex.PoseStack;

public class GiantRenderer extends MobRenderer<GiantEntity, ModelZombieModel<GiantEntity>> {
	public GiantRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelZombieModel(context.bakeLayer(ModelZombieModel.LAYER_LOCATION)), 0f);
	}

	@Override
	protected void scale(GiantEntity entity, PoseStack poseStack, float f) {
		poseStack.scale(6f, 6f, 6f);
	}

	@Override
	public ResourceLocation getTextureLocation(GiantEntity entity) {
		return new ResourceLocation("the_arg_container:textures/entities/giant.png");
	}
}

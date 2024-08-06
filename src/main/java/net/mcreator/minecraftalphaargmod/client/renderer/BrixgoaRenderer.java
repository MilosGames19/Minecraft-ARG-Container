
package net.mcreator.minecraftalphaargmod.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.minecraftalphaargmod.entity.BrixgoaEntity;
import net.mcreator.minecraftalphaargmod.client.model.ModelBrixgoa;

import com.mojang.blaze3d.vertex.PoseStack;

public class BrixgoaRenderer extends MobRenderer<BrixgoaEntity, ModelBrixgoa<BrixgoaEntity>> {
	public BrixgoaRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelBrixgoa(context.bakeLayer(ModelBrixgoa.LAYER_LOCATION)), 0.5f);
	}

	@Override
	protected void scale(BrixgoaEntity entity, PoseStack poseStack, float f) {
		poseStack.scale(2f, 2f, 2f);
	}

	@Override
	public ResourceLocation getTextureLocation(BrixgoaEntity entity) {
		return new ResourceLocation("minecraft_alpha_arg_mod:textures/entities/brixgoa.png");
	}
}

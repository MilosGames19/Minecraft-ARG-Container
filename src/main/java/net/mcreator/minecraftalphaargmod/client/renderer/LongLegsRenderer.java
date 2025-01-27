
package net.mcreator.minecraftalphaargmod.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.minecraftalphaargmod.entity.LongLegsEntity;
import net.mcreator.minecraftalphaargmod.client.model.ModelCustomModel;

import com.mojang.blaze3d.vertex.PoseStack;

public class LongLegsRenderer extends MobRenderer<LongLegsEntity, ModelCustomModel<LongLegsEntity>> {
	public LongLegsRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelCustomModel(context.bakeLayer(ModelCustomModel.LAYER_LOCATION)), 0.5f);
	}

	@Override
	protected void scale(LongLegsEntity entity, PoseStack poseStack, float f) {
		poseStack.scale(3.58f, 3.58f, 3.58f);
	}

	@Override
	public ResourceLocation getTextureLocation(LongLegsEntity entity) {
		return new ResourceLocation("the_arg_container:textures/entities/texture.png");
	}
}

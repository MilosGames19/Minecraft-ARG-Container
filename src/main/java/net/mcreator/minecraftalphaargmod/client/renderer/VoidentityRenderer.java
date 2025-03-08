
package net.mcreator.minecraftalphaargmod.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.minecraftalphaargmod.entity.VoidentityEntity;
import net.mcreator.minecraftalphaargmod.client.model.ModelVoid_entity;

import com.mojang.blaze3d.vertex.PoseStack;

public class VoidentityRenderer extends MobRenderer<VoidentityEntity, ModelVoid_entity<VoidentityEntity>> {
	public VoidentityRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelVoid_entity(context.bakeLayer(ModelVoid_entity.LAYER_LOCATION)), 0f);
	}

	@Override
	protected void scale(VoidentityEntity entity, PoseStack poseStack, float f) {
		poseStack.scale(1.3f, 1.3f, 1.3f);
	}

	@Override
	public ResourceLocation getTextureLocation(VoidentityEntity entity) {
		return new ResourceLocation("the_arg_container:textures/entities/void_entity_v2.png");
	}
}

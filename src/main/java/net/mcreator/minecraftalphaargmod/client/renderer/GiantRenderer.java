
package net.mcreator.minecraftalphaargmod.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.HumanoidModel;

import net.mcreator.minecraftalphaargmod.entity.GiantEntity;

import com.mojang.blaze3d.vertex.PoseStack;

public class GiantRenderer extends HumanoidMobRenderer<GiantEntity, HumanoidModel<GiantEntity>> {
	public GiantRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER)), 3f);
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
	}

	@Override
	protected void scale(GiantEntity entity, PoseStack poseStack, float f) {
		poseStack.scale(12f, 12f, 12f);
	}

	@Override
	public ResourceLocation getTextureLocation(GiantEntity entity) {
		return new ResourceLocation("the_arg_container:textures/entities/giant.png");
	}
}

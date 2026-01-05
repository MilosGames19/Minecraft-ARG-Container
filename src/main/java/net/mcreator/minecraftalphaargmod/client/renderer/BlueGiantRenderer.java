
package net.mcreator.minecraftalphaargmod.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.HumanoidModel;

import net.mcreator.minecraftalphaargmod.entity.BlueGiantEntity;

import com.mojang.blaze3d.vertex.PoseStack;

public class BlueGiantRenderer extends HumanoidMobRenderer<BlueGiantEntity, HumanoidModel<BlueGiantEntity>> {
	public BlueGiantRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<BlueGiantEntity>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
	}

	@Override
	protected void scale(BlueGiantEntity entity, PoseStack poseStack, float f) {
		poseStack.scale(6f, 6f, 6f);
	}

	@Override
	public ResourceLocation getTextureLocation(BlueGiantEntity entity) {
		return new ResourceLocation("the_arg_container:textures/entities/download.png");
	}
}

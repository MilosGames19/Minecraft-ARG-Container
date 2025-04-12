
package net.mcreator.minecraftalphaargmod.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.minecraftalphaargmod.entity.model.VoidEntityGeckolibModel;
import net.mcreator.minecraftalphaargmod.entity.VoidEntityGeckolibEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class VoidEntityGeckolibRenderer extends GeoEntityRenderer<VoidEntityGeckolibEntity> {
	public VoidEntityGeckolibRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new VoidEntityGeckolibModel());
		this.shadowRadius = 0f;
	}

	@Override
	public RenderType getRenderType(VoidEntityGeckolibEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, VoidEntityGeckolibEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 1.5f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(VoidEntityGeckolibEntity entityLivingBaseIn) {
		return 0.0F;
	}
}

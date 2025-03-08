package net.mcreator.minecraftalphaargmod.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.minecraftalphaargmod.entity.VoidEntityGeckolibEntity;

public class VoidEntityGeckolibModel extends GeoModel<VoidEntityGeckolibEntity> {
	@Override
	public ResourceLocation getAnimationResource(VoidEntityGeckolibEntity entity) {
		return new ResourceLocation("the_arg_container", "animations/geckolib_void_entity.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(VoidEntityGeckolibEntity entity) {
		return new ResourceLocation("the_arg_container", "geo/geckolib_void_entity.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(VoidEntityGeckolibEntity entity) {
		return new ResourceLocation("the_arg_container", "textures/entities/" + entity.getTexture() + ".png");
	}

}

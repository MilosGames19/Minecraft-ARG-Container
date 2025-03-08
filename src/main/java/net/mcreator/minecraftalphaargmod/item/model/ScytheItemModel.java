package net.mcreator.minecraftalphaargmod.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.minecraftalphaargmod.item.ScytheItem;

public class ScytheItemModel extends GeoModel<ScytheItem> {
	@Override
	public ResourceLocation getAnimationResource(ScytheItem animatable) {
		return new ResourceLocation("the_arg_container", "animations/scythe.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ScytheItem animatable) {
		return new ResourceLocation("the_arg_container", "geo/scythe.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ScytheItem animatable) {
		return new ResourceLocation("the_arg_container", "textures/item/stiched_texture.png");
	}
}

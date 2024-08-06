
package net.mcreator.minecraftalphaargmod.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.minecraftalphaargmod.entity.DBGEntity;
import net.mcreator.minecraftalphaargmod.client.model.ModelDBG;

public class DBGRenderer extends MobRenderer<DBGEntity, ModelDBG<DBGEntity>> {
	public DBGRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelDBG(context.bakeLayer(ModelDBG.LAYER_LOCATION)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(DBGEntity entity) {
		return new ResourceLocation("minecraft_alpha_arg_mod:textures/entities/dbg.png");
	}
}

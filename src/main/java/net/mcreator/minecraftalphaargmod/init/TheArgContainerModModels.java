
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.minecraftalphaargmod.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.mcreator.minecraftalphaargmod.client.model.Modelspearprojectile;
import net.mcreator.minecraftalphaargmod.client.model.ModelObserver;
import net.mcreator.minecraftalphaargmod.client.model.ModelGiant;
import net.mcreator.minecraftalphaargmod.client.model.ModelEssenceProjectile;
import net.mcreator.minecraftalphaargmod.client.model.ModelDBG;
import net.mcreator.minecraftalphaargmod.client.model.ModelCustomModel;
import net.mcreator.minecraftalphaargmod.client.model.ModelBrixgoa;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class TheArgContainerModModels {
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ModelDBG.LAYER_LOCATION, ModelDBG::createBodyLayer);
		event.registerLayerDefinition(ModelGiant.LAYER_LOCATION, ModelGiant::createBodyLayer);
		event.registerLayerDefinition(ModelCustomModel.LAYER_LOCATION, ModelCustomModel::createBodyLayer);
		event.registerLayerDefinition(Modelspearprojectile.LAYER_LOCATION, Modelspearprojectile::createBodyLayer);
		event.registerLayerDefinition(ModelEssenceProjectile.LAYER_LOCATION, ModelEssenceProjectile::createBodyLayer);
		event.registerLayerDefinition(ModelBrixgoa.LAYER_LOCATION, ModelBrixgoa::createBodyLayer);
		event.registerLayerDefinition(ModelObserver.LAYER_LOCATION, ModelObserver::createBodyLayer);
	}
}

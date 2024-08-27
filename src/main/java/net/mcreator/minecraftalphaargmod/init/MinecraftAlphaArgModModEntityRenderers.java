
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.minecraftalphaargmod.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.mcreator.minecraftalphaargmod.client.renderer.StevenRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.SpearProjectileRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.SoulEntityRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.RecruiterRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.ObserverMobRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.GiantRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.ExplosiveEssenceRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.EssenceProjectileRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.EntityRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.DBGRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.BrixgoaRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.BlueGiantRenderer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MinecraftAlphaArgModModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(MinecraftAlphaArgModModEntities.OBSERVER_MOB.get(), ObserverMobRenderer::new);
		event.registerEntityRenderer(MinecraftAlphaArgModModEntities.RECRUITER.get(), RecruiterRenderer::new);
		event.registerEntityRenderer(MinecraftAlphaArgModModEntities.GIANT.get(), GiantRenderer::new);
		event.registerEntityRenderer(MinecraftAlphaArgModModEntities.SOUL_ENTITY.get(), SoulEntityRenderer::new);
		event.registerEntityRenderer(MinecraftAlphaArgModModEntities.ENTITY.get(), EntityRenderer::new);
		event.registerEntityRenderer(MinecraftAlphaArgModModEntities.SPEAR_PROJECTILE.get(), SpearProjectileRenderer::new);
		event.registerEntityRenderer(MinecraftAlphaArgModModEntities.ESSENCE_PROJECTILE.get(), EssenceProjectileRenderer::new);
		event.registerEntityRenderer(MinecraftAlphaArgModModEntities.EXPLOSIVE_ESSENCE.get(), ExplosiveEssenceRenderer::new);
		event.registerEntityRenderer(MinecraftAlphaArgModModEntities.DBG.get(), DBGRenderer::new);
		event.registerEntityRenderer(MinecraftAlphaArgModModEntities.BLUE_GIANT.get(), BlueGiantRenderer::new);
		event.registerEntityRenderer(MinecraftAlphaArgModModEntities.BRIXGOA.get(), BrixgoaRenderer::new);
		event.registerEntityRenderer(MinecraftAlphaArgModModEntities.STEVEN.get(), StevenRenderer::new);
	}
}

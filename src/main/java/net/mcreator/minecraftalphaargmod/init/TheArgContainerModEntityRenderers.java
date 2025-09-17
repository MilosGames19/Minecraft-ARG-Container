
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.minecraftalphaargmod.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;

import net.mcreator.minecraftalphaargmod.client.renderer.VoidEntityGeckolibRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.User0CloneRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.StevenRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.SpearProjectileRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.SoulEntityRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.RecruiterV2Renderer;
import net.mcreator.minecraftalphaargmod.client.renderer.RecruiterRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.PigRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.ObserverMobRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.LongLegsRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.GiantRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.ExplosiveEssenceRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.EvilUser0Renderer;
import net.mcreator.minecraftalphaargmod.client.renderer.EssenceProjectileRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.EntityRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.DarknessEntityRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.DBGRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.CORERenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.BrixgoaRenderer;
import net.mcreator.minecraftalphaargmod.client.renderer.BlueGiantRenderer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TheArgContainerModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(TheArgContainerModEntities.OBSERVER_MOB.get(), ObserverMobRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.RECRUITER.get(), RecruiterRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.GIANT.get(), GiantRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.SOUL_ENTITY.get(), SoulEntityRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.ENTITY.get(), EntityRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.SPEAR_PROJECTILE.get(), SpearProjectileRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.ESSENCE_PROJECTILE.get(), EssenceProjectileRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.EXPLOSIVE_ESSENCE.get(), ExplosiveEssenceRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.DBG.get(), DBGRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.BLUE_GIANT.get(), BlueGiantRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.BRIXGOA.get(), BrixgoaRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.STEVEN.get(), StevenRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.USER_0_CLONE.get(), User0CloneRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.EVIL_USER_0.get(), EvilUser0Renderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.RANGER_BULLET_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.PIG.get(), PigRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.PARTI_BULLET.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.LONG_LEGS.get(), LongLegsRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.VOID_ENTITY_GECKOLIB.get(), VoidEntityGeckolibRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.CORE.get(), CORERenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.DARKNESS_ENTITY.get(), DarknessEntityRenderer::new);
		event.registerEntityRenderer(TheArgContainerModEntities.RECRUITER_V_2.get(), RecruiterV2Renderer::new);
	}
}

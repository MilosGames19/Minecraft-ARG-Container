
package net.mcreator.minecraftalphaargmod.client.screens;

import org.checkerframework.checker.units.qual.h;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Minecraft;

import net.mcreator.minecraftalphaargmod.procedures.DashOverlayDisplayOverlayIngameProcedure;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class DashOverlayOverlay {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
		Level world = null;
		double x = 0;
		double y = 0;
		double z = 0;
		Player entity = Minecraft.getInstance().player;
		if (entity != null) {
			world = entity.level();
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
		}
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		if (DashOverlayDisplayOverlayIngameProcedure.execute()) {
			event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile000.png"), w / 2 + -51, h / 2 + 41, 0, 0, 102, 7, 102, 7);

			event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile003.png"), w / 2 + -51, h / 2 + 41, 0, 0, 102, 7, 102, 7);

			event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile006.png"), w / 2 + -51, h / 2 + 41, 0, 0, 102, 7, 102, 7);

			event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile009.png"), w / 2 + -51, h / 2 + 41, 0, 0, 102, 7, 102, 7);

			event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile012.png"), w / 2 + -51, h / 2 + 41, 0, 0, 102, 7, 102, 7);

			event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile015.png"), w / 2 + -51, h / 2 + 41, 0, 0, 102, 7, 102, 7);

			event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile018.png"), w / 2 + -51, h / 2 + 41, 0, 0, 102, 7, 102, 7);

			event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile021.png"), w / 2 + -51, h / 2 + 41, 0, 0, 102, 7, 102, 7);

			event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile024.png"), w / 2 + -51, h / 2 + 41, 0, 0, 102, 7, 102, 7);

			event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile027.png"), w / 2 + -51, h / 2 + 41, 0, 0, 102, 7, 102, 7);

			event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile029.png"), w / 2 + -51, h / 2 + 41, 0, 0, 102, 7, 102, 7);

		}
		RenderSystem.depthMask(true);
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}

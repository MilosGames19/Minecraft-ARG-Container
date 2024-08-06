
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

import net.mcreator.minecraftalphaargmod.procedures.BanTextDisplayOverlayIngameProcedure;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class BarOverlay {
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
		if (BanTextDisplayOverlayIngameProcedure.execute(entity)) {
			event.getGuiGraphics().blit(new ResourceLocation("minecraft_alpha_arg_mod:textures/screens/progress_0.png"), w / 2 + -63, h - 70, 0, 0, 127, 16, 127, 16);

			event.getGuiGraphics().blit(new ResourceLocation("minecraft_alpha_arg_mod:textures/screens/progress_1.png"), w / 2 + -63, h - 70, 0, 0, 127, 16, 127, 16);

			event.getGuiGraphics().blit(new ResourceLocation("minecraft_alpha_arg_mod:textures/screens/progress_2.png"), w / 2 + -63, h - 70, 0, 0, 127, 16, 127, 16);

			event.getGuiGraphics().blit(new ResourceLocation("minecraft_alpha_arg_mod:textures/screens/progress_3.png"), w / 2 + -63, h - 70, 0, 0, 127, 16, 127, 16);

			event.getGuiGraphics().blit(new ResourceLocation("minecraft_alpha_arg_mod:textures/screens/progress_4.png"), w / 2 + -63, h - 70, 0, 0, 127, 16, 127, 16);

			event.getGuiGraphics().blit(new ResourceLocation("minecraft_alpha_arg_mod:textures/screens/progress_5.png"), w / 2 + -63, h - 70, 0, 0, 127, 16, 127, 16);

			event.getGuiGraphics().blit(new ResourceLocation("minecraft_alpha_arg_mod:textures/screens/progress_6.png"), w / 2 + -63, h - 70, 0, 0, 127, 16, 127, 16);

			event.getGuiGraphics().blit(new ResourceLocation("minecraft_alpha_arg_mod:textures/screens/progress_7.png"), w / 2 + -63, h - 70, 0, 0, 127, 16, 127, 16);

			event.getGuiGraphics().blit(new ResourceLocation("minecraft_alpha_arg_mod:textures/screens/progress_8.png"), w / 2 + -63, h - 70, 0, 0, 127, 16, 127, 16);

			event.getGuiGraphics().blit(new ResourceLocation("minecraft_alpha_arg_mod:textures/screens/progress_9.png"), w / 2 + -63, h - 70, 0, 0, 127, 16, 127, 16);

			event.getGuiGraphics().blit(new ResourceLocation("minecraft_alpha_arg_mod:textures/screens/progress_10.png"), w / 2 + -63, h - 70, 0, 0, 127, 16, 127, 16);

		}
		RenderSystem.depthMask(true);
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}

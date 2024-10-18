
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
import net.mcreator.minecraftalphaargmod.procedures.Dash9Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash8Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash7Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash6Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash5Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash4Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash3Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash30Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash2Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash29Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash28Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash27Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash26Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash25Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash24Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash23Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash22Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash21Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash20Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash1Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash19Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash18Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash17Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash16Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash15Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash14Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash13Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash12Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash11Procedure;
import net.mcreator.minecraftalphaargmod.procedures.Dash10Procedure;

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
		if (DashOverlayDisplayOverlayIngameProcedure.execute(entity)) {
			if (Dash1Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile000.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash2Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile001.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash3Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile002.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash4Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile003.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash5Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile004.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash6Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile005.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash7Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile006.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash8Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile007.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash9Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile008.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash10Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile009.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash11Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile010.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash12Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile011.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash13Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile012.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash14Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile013.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash15Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile014.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash16Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile015.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash17Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile016.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash18Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile017.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash19Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile018.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash20Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile019.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash21Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile020.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash22Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile021.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash23Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile022.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash24Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile023.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash25Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile024.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash26Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile025.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash27Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile026.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash28Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile027.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash29Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile028.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
			if (Dash30Procedure.execute(entity)) {
				event.getGuiGraphics().blit(new ResourceLocation("the_arg_container:textures/screens/tile029.png"), w / 2 + -45, h / 2 + 64, 0, 0, 102, 7, 102, 7);
			}
		}
		RenderSystem.depthMask(true);
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}

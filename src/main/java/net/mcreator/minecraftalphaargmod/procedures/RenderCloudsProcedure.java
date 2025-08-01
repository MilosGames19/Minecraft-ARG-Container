package net.mcreator.minecraftalphaargmod.procedures;

import org.lwjgl.opengl.GL11;

import org.joml.Matrix4f;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.client.DimensionSpecialEffectsManager;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.CloudStatus;

import javax.annotation.Nullable;

import java.util.function.Predicate;
import java.util.Set;

import java.lang.reflect.Field;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RenderCloudsProcedure {
	private static int ticks = 0;
	private static float partialTick = 0.0F;
	private static PoseStack poseStack = null;
	private static Matrix4f projectionMatrix = null;
	private static VertexBuffer cloudBuffer = null;
	private static CloudStatus cloudStatus = null;
	private static double x = 0.0D;
	private static double y = 0.0D;
	private static double z = 0.0D;
	private static float width = 12.0F;
	private static float height = 4.0F;
	private static final Predicate<Object[]> PREDICATE = params -> {
		ticks = (Integer) params[1];
		partialTick = (Float) params[2];
		poseStack = (PoseStack) params[3];
		projectionMatrix = (Matrix4f) params[7];
		Minecraft minecraft = Minecraft.getInstance();
		Entity entity = minecraft.gameRenderer.getMainCamera().getEntity();
		if (entity != null) {
			ClientLevel level = minecraft.level;
			Vec3 pos = entity.getPosition(partialTick);
			execute(null, level, level.dimension(), partialTick);
			return true;
		}
		return false;
	};

	private static void buildClouds(CloudStatus cloudStatus, double x, double y, double z) {
		if (cloudBuffer == null || RenderCloudsProcedure.cloudStatus != cloudStatus || RenderCloudsProcedure.x != x || RenderCloudsProcedure.y != y || RenderCloudsProcedure.z != z) {
			RenderCloudsProcedure.cloudStatus = cloudStatus;
			RenderCloudsProcedure.x = x;
			RenderCloudsProcedure.y = y;
			RenderCloudsProcedure.z = z;
			Minecraft minecraft = Minecraft.getInstance();
			RenderSystem.bindTexture(RenderSystem.getShaderTexture(0));
			float du = 1.0F / GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
			float dv = 1.0F / GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
			float dx = Mth.floor(x) * du;
			float dz = Mth.floor(z) * dv;
			float cloudY = (float) Math.floor(y / height) * height;
			BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
			bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
			if (cloudStatus == CloudStatus.FANCY) {
				for (int i = -3; i <= 4; ++i) {
					for (int j = -3; j <= 4; ++j) {
						float cloudX = i * 8.0F;
						float cloudZ = j * 8.0F;
						if (cloudY > -height - 1) {
							bufferBuilder.vertex(cloudX, cloudY, cloudZ + 8.0F).uv(cloudX * du + dx, (cloudZ + 8.0F) * dv + dz).color(0.7F, 0.7F, 0.7F, 1.0F).normal(0.0F, -1.0F, 0.0F).endVertex();
							bufferBuilder.vertex(cloudX + 8.0F, cloudY, cloudZ + 8.0F).uv((cloudX + 8.0F) * du + dx, (cloudZ + 8.0F) * dv + dz).color(0.7F, 0.7F, 0.7F, 1.0F).normal(0.0F, -1.0F, 0.0F).endVertex();
							bufferBuilder.vertex(cloudX + 8.0F, cloudY, cloudZ).uv((cloudX + 8.0F) * du + dx, cloudZ * dv + dz).color(0.7F, 0.7F, 0.7F, 1.0F).normal(0.0F, -1.0F, 0.0F).endVertex();
							bufferBuilder.vertex(cloudX, cloudY, cloudZ).uv(cloudX * du + dx, cloudZ * dv + dz).color(0.7F, 0.7F, 0.7F, 1.0F).normal(0.0F, -1.0F, 0.0F).endVertex();
						}
						if (cloudY <= height + 1) {
							bufferBuilder.vertex(cloudX, cloudY + height - 9.765625E-4F, cloudZ + 8.0F).uv(cloudX * du + dx, (cloudZ + 8.0F) * dv + dz).color(1.0F, 1.0F, 1.0F, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
							bufferBuilder.vertex(cloudX + 8.0F, cloudY + height - 9.765625E-4F, cloudZ + 8.0F).uv((cloudX + 8.0F) * du + dx, (cloudZ + 8.0F) * dv + dz).color(1.0F, 1.0F, 1.0F, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
							bufferBuilder.vertex(cloudX + 8.0F, cloudY + height - 9.765625E-4F, cloudZ).uv((cloudX + 8.0F) * du + dx, cloudZ * dv + dz).color(1.0F, 1.0F, 1.0F, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
							bufferBuilder.vertex(cloudX, cloudY + height - 9.765625E-4F, cloudZ).uv(cloudX * du + dx, cloudZ * dv + dz).color(1.0F, 1.0F, 1.0F, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
						}
						if (i > -1) {
							for (int k = 0; k < 8; ++k) {
								bufferBuilder.vertex(cloudX + k, cloudY, cloudZ + 8.0F).uv((cloudX + k + 0.5F) * du + dx, (cloudZ + 8.0F) * dv + dz).color(0.9F, 0.9F, 0.9F, 1.0F).normal(-1.0F, 0.0F, 0.0F).endVertex();
								bufferBuilder.vertex(cloudX + k, cloudY + height, cloudZ + 8.0F).uv((cloudX + k + 0.5F) * du + dx, (cloudZ + 8.0F) * dv + dz).color(0.9F, 0.9F, 0.9F, 1.0F).normal(-1.0F, 0.0F, 0.0F).endVertex();
								bufferBuilder.vertex(cloudX + k, cloudY + height, cloudZ).uv((cloudX + k + 0.5F) * du + dx, cloudZ * dv + dz).color(0.9F, 0.9F, 0.9F, 1.0F).normal(-1.0F, 0.0F, 0.0F).endVertex();
								bufferBuilder.vertex(cloudX + k, cloudY, cloudZ).uv((cloudX + k + 0.5F) * du + dx, cloudZ * dv + dz).color(0.9F, 0.9F, 0.9F, 1.0F).normal(-1.0F, 0.0F, 0.0F).endVertex();
							}
						}
						if (i <= 1) {
							for (int k = 0; k < 8; ++k) {
								bufferBuilder.vertex(cloudX + k + 1.0F - 9.765625E-4F, cloudY, cloudZ + 8.0F).uv((cloudX + k + 0.5F) * du + dx, (cloudZ + 8.0F) * dv + dz).color(0.9F, 0.9F, 0.9F, 1.0F).normal(1.0F, 0.0F, 0.0F).endVertex();
								bufferBuilder.vertex(cloudX + k + 1.0F - 9.765625E-4F, cloudY + height, cloudZ + 8.0F).uv((cloudX + k + 0.5F) * du + dx, (cloudZ + 8.0F) * dv + dz).color(0.9F, 0.9F, 0.9F, 1.0F).normal(1.0F, 0.0F, 0.0F).endVertex();
								bufferBuilder.vertex(cloudX + k + 1.0F - 9.765625E-4F, cloudY + height, cloudZ).uv((cloudX + k + 0.5F) * du + dx, cloudZ * dv + dz).color(0.9F, 0.9F, 0.9F, 1.0F).normal(1.0F, 0.0F, 0.0F).endVertex();
								bufferBuilder.vertex(cloudX + k + 1.0F - 9.765625E-4F, cloudY, cloudZ).uv((cloudX + k + 0.5F) * du + dx, cloudZ * dv + dz).color(0.9F, 0.9F, 0.9F, 1.0F).normal(1.0F, 0.0F, 0.0F).endVertex();
							}
						}
						if (j > -1) {
							for (int k = 0; k < 8; ++k) {
								bufferBuilder.vertex(cloudX, cloudY + height, cloudZ + k).uv(cloudX * du + dx, (cloudZ + k + 0.5F) * dv + dz).color(0.8F, 0.8F, 0.8F, 1.0F).normal(0.0F, 0.0F, -1.0F).endVertex();
								bufferBuilder.vertex(cloudX + 8.0F, cloudY + height, cloudZ + k).uv((cloudX + 8.0F) * du + dx, (cloudZ + k + 0.5F) * dv + dz).color(0.8F, 0.8F, 0.8F, 1.0F).normal(0.0F, 0.0F, -1.0F).endVertex();
								bufferBuilder.vertex(cloudX + 8.0F, cloudY, cloudZ + k).uv((cloudX + 8.0F) * du + dx, (cloudZ + k + 0.5F) * dv + dz).color(0.8F, 0.8F, 0.8F, 1.0F).normal(0.0F, 0.0F, -1.0F).endVertex();
								bufferBuilder.vertex(cloudX, cloudY, cloudZ + k).uv(cloudX * du + dx, (cloudZ + k + 0.5F) * dv + dz).color(0.8F, 0.8F, 0.8F, 1.0F).normal(0.0F, 0.0F, -1.0F).endVertex();
							}
						}
						if (j <= 1) {
							for (int k = 0; k < 8; ++k) {
								bufferBuilder.vertex(cloudX, cloudY + height, cloudZ + k + 1.0F - 9.765625E-4F).uv(cloudX * du + dx, (cloudZ + k + 0.5F) * dv + dz).color(0.8F, 0.8F, 0.8F, 1.0F).normal(0.0F, 0.0F, 1.0F).endVertex();
								bufferBuilder.vertex(cloudX + 8.0F, cloudY + height, cloudZ + k + 1.0F - 9.765625E-4F).uv((cloudX + 8.0F) * du + dx, (cloudZ + k + 0.5F) * dv + dz).color(0.8F, 0.8F, 0.8F, 1.0F).normal(0.0F, 0.0F, 1.0F).endVertex();
								bufferBuilder.vertex(cloudX + 8.0F, cloudY, cloudZ + k + 1.0F - 9.765625E-4F).uv((cloudX + 8.0F) * du + dx, (cloudZ + k + 0.5F) * dv + dz).color(0.8F, 0.8F, 0.8F, 1.0F).normal(0.0F, 0.0F, 1.0F).endVertex();
								bufferBuilder.vertex(cloudX, cloudY, cloudZ + k + 1.0F - 9.765625E-4F).uv(cloudX * du + dx, (cloudZ + k + 0.5F) * dv + dz).color(0.8F, 0.8F, 0.8F, 1.0F).normal(0.0F, 0.0F, 1.0F).endVertex();
							}
						}
					}
				}
			} else if (cloudStatus == CloudStatus.FAST) {
				for (int i = -32; i < 32; i += 32) {
					for (int j = -32; j < 32; j += 32) {
						bufferBuilder.vertex(i, cloudY, j + 32).uv(i * du + dx, (j + 32) * dv + dz).color(1.0F, 1.0F, 1.0F, 1.0F).normal(0.0F, -1.0F, 0.0F).endVertex();
						bufferBuilder.vertex(i + 32, cloudY, j + 32).uv((i + 32) * du + dx, (j + 32) * dv + dz).color(1.0F, 1.0F, 1.0F, 1.0F).normal(0.0F, -1.0F, 0.0F).endVertex();
						bufferBuilder.vertex(i + 32, cloudY, j).uv((i + 32) * du + dx, j * dv + dz).color(1.0F, 1.0F, 1.0F, 1.0F).normal(0.0F, -1.0F, 0.0F).endVertex();
						bufferBuilder.vertex(i, cloudY, j).uv(i * du + dx, j * dv + dz).color(1.0F, 1.0F, 1.0F, 1.0F).normal(0.0F, -1.0F, 0.0F).endVertex();
					}
				}
			}
			if (cloudBuffer != null)
				cloudBuffer.close();
			cloudBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
			cloudBuffer.bind();
			cloudBuffer.upload(bufferBuilder.end());
		} else {
			cloudBuffer.bind();
		}
	}

	public static void renderClouds(CloudStatus cloudStatus, double altitude, double vx, double vz, int color) {
		if (cloudStatus == CloudStatus.OFF)
			return;
		RenderSystem.bindTexture(RenderSystem.getShaderTexture(0));
		int tw = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH) << 3;
		int th = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT) << 3;
		if (tw > 0 && th > 0) {
			Minecraft minecraft = Minecraft.getInstance();
			Vec3 pos = minecraft.gameRenderer.getMainCamera().getPosition();
			double factor = (ticks + partialTick) * 0.03D;
			double x = (pos.x() + factor * -vx) / width;
			double y = altitude + 0.33D - pos.y();
			double z = (pos.z() + factor * -vz) / width + 0.33D;
			x -= Mth.floor(x / tw) * tw;
			z -= Mth.floor(z / th) * th;
			float dx = (float) (x - Mth.floor(x));
			float dy = (float) (y / height - Mth.floor(y / height)) * height;
			float dz = (float) (z - Mth.floor(z));
			buildClouds(cloudStatus, x, y, z);
			poseStack.pushPose();
			poseStack.scale(width, 1.0F, width);
			poseStack.translate(-dx, dy, -dz);
			Matrix4f matrix4f = poseStack.last().pose();
			ShaderInstance shaderInstance = GameRenderer.getPositionTexColorNormalShader();
			RenderSystem.setShaderColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, (color >>> 24) / 255.0F);
			if (cloudStatus == CloudStatus.FANCY) {
				RenderSystem.colorMask(false, false, false, false);
				cloudBuffer.drawWithShader(matrix4f, projectionMatrix, shaderInstance);
				RenderSystem.colorMask(true, true, true, true);
				cloudBuffer.drawWithShader(matrix4f, projectionMatrix, shaderInstance);
			} else if (cloudStatus == CloudStatus.FAST) {
				RenderSystem.colorMask(true, true, true, true);
				cloudBuffer.drawWithShader(matrix4f, projectionMatrix, shaderInstance);
			}
			VertexBuffer.unbind();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			poseStack.popPose();
		}
	}

	@SubscribeEvent
	public static void cloudsSetup(FMLClientSetupEvent event) {
		try {
			Field field = DimensionSpecialEffectsManager.class.getDeclaredField("EFFECTS");
			field.setAccessible(true);
			for (DimensionSpecialEffects dimensionSpecialEffects : ((com.google.common.collect.ImmutableMap<ResourceLocation, DimensionSpecialEffects>) field.get(null)).values()) {
				Class<?> effects = dimensionSpecialEffects.getClass();
				((Set<Predicate<Object[]>>) effects.getField("CUSTOM_CLOUDS").get(null)).add(PREDICATE);
			}
		} catch (Exception e) {
		}
	}

	public static void execute(LevelAccessor world, ResourceKey<Level> dimension, double partialTick) {
		execute(null, world, dimension, partialTick);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, ResourceKey<Level> dimension, double partialTick) {
		if (dimension == null)
			return;
		if (dimension == Level.OVERWORLD) {
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.setShaderTexture(0, new ResourceLocation(("minecraft" + ":textures/" + "environment/clouds" + ".png")));
			renderClouds(Minecraft.getInstance().options.getCloudsType(), 192, -1, 0, (new Object() {
				public int get(LevelAccessor levelAccessor, float partialTick) {
					if (levelAccessor instanceof ClientLevel) {
						Vec3 color = ((ClientLevel) levelAccessor).getCloudColor(partialTick);
						return 204 << 24 | (int) (color.x() * 255.0D) << 16 | (int) (color.y() * 255.0D) << 8 | (int) (color.z() * 255.0D);
					}
					return 0;
				}
			}).get(world, (float) partialTick));
		}
	}
}

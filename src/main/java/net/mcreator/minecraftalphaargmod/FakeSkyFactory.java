// Thank you, Nebula and Noodlegamer76, for making this possible.
// If you want to use this code, please credit Nebula and Noodlegamer76. Without them, this wouldn't be possible.
package net.mcreator.minecraftalphaargmod;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class FakeSkyFactory {

	private final ResourceLocation texture;
	private TextureTarget skyRenderTarget;
	private final List<Matrix4f> pendingMatrices = new ArrayList<>();
	private TextureTarget compositeTarget;
	private VertexBuffer cubeBuffer;
	private VertexBuffer screenQuadBuffer;

	public FakeSkyFactory(ResourceLocation skyTexture) {
		this.texture = skyTexture;
	}


	public void queueMatrix(Matrix4f matrix) {
		pendingMatrices.add(new Matrix4f(matrix));
	}

	public void bake(RenderLevelStageEvent event) {
		Minecraft mc = Minecraft.getInstance();
		Window window = mc.getWindow();
		int width = window.getWidth();
		int height = window.getHeight();

		if (width <= 0 || height <= 0) return;

		if (skyRenderTarget == null || skyRenderTarget.width != width || skyRenderTarget.height != height) {
			if (skyRenderTarget != null) skyRenderTarget.destroyBuffers();
			skyRenderTarget = new TextureTarget(width, height, true, Minecraft.ON_OSX);
		}

		skyRenderTarget.bindWrite(true);
		RenderSystem.clearColor(0, 0, 0, 1);
		RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);

		RenderSystem.setShaderTexture(0, this.texture);
		renderSkybox(0f, 0f, 0f, 0xFFFFFFFF, true, event.getPoseStack(), event.getProjectionMatrix());

		mc.getMainRenderTarget().bindWrite(false);
	}

	public void renderToMain(RenderLevelStageEvent event) {
		if (pendingMatrices.isEmpty()) return;

		ShaderInstance fakeSkyShader = ShardsHandler.GetFakeSky();
		if (fakeSkyShader == null || skyRenderTarget == null) {
			pendingMatrices.clear();
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		Window window = mc.getWindow();
		int width = window.getWidth();
		int height = window.getHeight();
		if (width <= 0 || height <= 0) { pendingMatrices.clear(); return; }

		if (compositeTarget == null || compositeTarget.width != width || compositeTarget.height != height) {
			if (compositeTarget != null) compositeTarget.destroyBuffers();
			compositeTarget = new TextureTarget(width, height, true, Minecraft.ON_OSX);
		}

		if (cubeBuffer == null) {
			Matrix4f identity = new Matrix4f();
			BufferBuilder bb = Tesselator.getInstance().getBuilder();
			bb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
			emitCubeFaces(bb, identity);
			cubeBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
			cubeBuffer.bind();
			cubeBuffer.upload(bb.end());
			VertexBuffer.unbind();
		}

		if (screenQuadBuffer == null) {
			Matrix4f identity = new Matrix4f();
			BufferBuilder bb = Tesselator.getInstance().getBuilder();
			bb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			bb.vertex(identity, 0f, 0f, 0f).uv(0f, 0f).endVertex();
			bb.vertex(identity, 1f, 0f, 0f).uv(1f, 0f).endVertex();
			bb.vertex(identity, 1f, 1f, 0f).uv(1f, 1f).endVertex();
			bb.vertex(identity, 0f, 1f, 0f).uv(0f, 1f).endVertex();
			screenQuadBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
			screenQuadBuffer.bind();
			screenQuadBuffer.upload(bb.end());
			VertexBuffer.unbind();
		}

		RenderTarget mainFB = mc.getMainRenderTarget();

		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, mainFB.frameBufferId);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, compositeTarget.frameBufferId);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height,
				GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);

		compositeTarget.bindWrite(false);
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.setShaderTexture(0, skyRenderTarget.getColorTextureId());

		cubeBuffer.bind();
		for (Matrix4f matrix : pendingMatrices) {
			cubeBuffer.drawWithShader(matrix, event.getProjectionMatrix(), fakeSkyShader);
		}
		VertexBuffer.unbind();

		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, compositeTarget.frameBufferId);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, mainFB.frameBufferId);
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height,
				GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);

		mainFB.bindWrite(false);
		RenderSystem.disableDepthTest();
		RenderSystem.setShaderTexture(0, compositeTarget.getColorTextureId());
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

		Matrix4f ortho = new Matrix4f().setOrtho(0f, 1f, 0f, 1f, -1f, 1f);
		Matrix4f identity = new Matrix4f();

		ShaderInstance posTex = GameRenderer.getPositionTexShader();
		if (posTex != null) {
			screenQuadBuffer.bind();
			screenQuadBuffer.drawWithShader(identity, ortho, posTex);
			VertexBuffer.unbind();
		}

		RenderSystem.enableDepthTest();
		pendingMatrices.clear();
	}

	private static void emitCubeFaces(BufferBuilder bb, Matrix4f m) {
		// SOUTH  (f=0,g=1,h=0,i=1,j=1,k=1,l=1,m=1) → (0,0,1)(1,0,1)(1,1,1)(0,1,1)
		bb.vertex(m, 0f, 0f, 1f).endVertex();
		bb.vertex(m, 1f, 0f, 1f).endVertex();
		bb.vertex(m, 1f, 1f, 1f).endVertex();
		bb.vertex(m, 0f, 1f, 1f).endVertex();
		// NORTH  (f=0,g=1,h=1,i=0,j=0,k=0,l=0,m=0) → (0,1,0)(1,1,0)(1,0,0)(0,0,0)
		bb.vertex(m, 0f, 1f, 0f).endVertex();
		bb.vertex(m, 1f, 1f, 0f).endVertex();
		bb.vertex(m, 1f, 0f, 0f).endVertex();
		bb.vertex(m, 0f, 0f, 0f).endVertex();
		// EAST   (f=1,g=1,h=1,i=0,j=0,k=1,l=1,m=0) → (1,1,0)(1,1,1)(1,0,1)(1,0,0)
		bb.vertex(m, 1f, 1f, 0f).endVertex();
		bb.vertex(m, 1f, 1f, 1f).endVertex();
		bb.vertex(m, 1f, 0f, 1f).endVertex();
		bb.vertex(m, 1f, 0f, 0f).endVertex();
		// WEST   (f=0,g=0,h=0,i=1,j=0,k=1,l=1,m=0) → (0,0,0)(0,0,1)(0,1,1)(0,1,0)
		bb.vertex(m, 0f, 0f, 0f).endVertex();
		bb.vertex(m, 0f, 0f, 1f).endVertex();
		bb.vertex(m, 0f, 1f, 1f).endVertex();
		bb.vertex(m, 0f, 1f, 0f).endVertex();
		// DOWN   (f=0,g=1,h=0,i=0,j=0,k=0,l=1,m=1) → (0,0,0)(1,0,0)(1,0,1)(0,0,1)
		bb.vertex(m, 0f, 0f, 0f).endVertex();
		bb.vertex(m, 1f, 0f, 0f).endVertex();
		bb.vertex(m, 1f, 0f, 1f).endVertex();
		bb.vertex(m, 0f, 0f, 1f).endVertex();
		// UP     (f=0,g=1,h=1,i=1,j=1,k=1,l=0,m=0) → (0,1,1)(1,1,1)(1,1,0)(0,1,0)
		bb.vertex(m, 0f, 1f, 1f).endVertex();
		bb.vertex(m, 1f, 1f, 1f).endVertex();
		bb.vertex(m, 1f, 1f, 0f).endVertex();
		bb.vertex(m, 0f, 1f, 0f).endVertex();
	}

	public void renderSkybox(float yaw, float pitch, float roll, int color, boolean constant, PoseStack poseStack, Matrix4f projectionMatrix) {
		Minecraft minecraft = Minecraft.getInstance();
		if (this.skyboxBuffer == null) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
			bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			for (int i = 0; i < 6; ++i) {
				switch (i) {
					case 0:
						bufferBuilder.vertex(-0.5F, -0.5F, -0.5F).uv(0.0F, 0.0F).endVertex();
						bufferBuilder.vertex(-0.5F, -0.5F, 0.5F).uv(0.0F, 0.5F).endVertex();
						bufferBuilder.vertex(0.5F, -0.5F, 0.5F).uv(1.0F / 3.0F, 0.5F).endVertex();
						bufferBuilder.vertex(0.5F, -0.5F, -0.5F).uv(1.0F / 3.0F, 0.0F).endVertex();
						break;
					case 1:
						bufferBuilder.vertex(-0.5F, 0.5F, 0.5F).uv(1.0F / 3.0F, 0.0F).endVertex();
						bufferBuilder.vertex(-0.5F, 0.5F, -0.5F).uv(1.0F / 3.0F, 0.5F).endVertex();
						bufferBuilder.vertex(0.5F, 0.5F, -0.5F).uv(2.0F / 3.0F, 0.5F).endVertex();
						bufferBuilder.vertex(0.5F, 0.5F, 0.5F).uv(2.0F / 3.0F, 0.0F).endVertex();
						break;
					case 2:
						bufferBuilder.vertex(0.5F, 0.5F, 0.5F).uv(2.0F / 3.0F, 0.0F).endVertex();
						bufferBuilder.vertex(0.5F, -0.5F, 0.5F).uv(2.0F / 3.0F, 0.5F).endVertex();
						bufferBuilder.vertex(-0.5F, -0.5F, 0.5F).uv(1.0F, 0.5F).endVertex();
						bufferBuilder.vertex(-0.5F, 0.5F, 0.5F).uv(1.0F, 0.0F).endVertex();
						break;
					case 3:
						bufferBuilder.vertex(-0.5F, 0.5F, 0.5F).uv(0.0F, 0.5F).endVertex();
						bufferBuilder.vertex(-0.5F, -0.5F, 0.5F).uv(0.0F, 1.0F).endVertex();
						bufferBuilder.vertex(-0.5F, -0.5F, -0.5F).uv(1.0F / 3.0F, 1.0F).endVertex();
						bufferBuilder.vertex(-0.5F, 0.5F, -0.5F).uv(1.0F / 3.0F, 0.5F).endVertex();
						break;
					case 4:
						bufferBuilder.vertex(-0.5F, 0.5F, -0.5F).uv(1.0F / 3.0F, 0.5F).endVertex();
						bufferBuilder.vertex(-0.5F, -0.5F, -0.5F).uv(1.0F / 3.0F, 1.0F).endVertex();
						bufferBuilder.vertex(0.5F, -0.5F, -0.5F).uv(2.0F / 3.0F, 1.0F).endVertex();
						bufferBuilder.vertex(0.5F, 0.5F, -0.5F).uv(2.0F / 3.0F, 0.5F).endVertex();
						break;
					case 5:
						bufferBuilder.vertex(0.5F, 0.5F, -0.5F).uv(2.0F / 3.0F, 0.5F).endVertex();
						bufferBuilder.vertex(0.5F, -0.5F, -0.5F).uv(2.0F / 3.0F, 1.0F).endVertex();
						bufferBuilder.vertex(0.5F, -0.5F, 0.5F).uv(1.0F, 1.0F).endVertex();
						bufferBuilder.vertex(0.5F, 0.5F, 0.5F).uv(1.0F, 0.5F).endVertex();
						break;
				}
			}
			this.skyboxBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
			this.skyboxBuffer.bind();
			this.skyboxBuffer.upload(bufferBuilder.end());
		} else {
			this.skyboxBuffer.bind();
		}
		float size = Minecraft.getInstance().options.getEffectiveRenderDistance() << 6;
		poseStack.pushPose();
		poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
		poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
		poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll));
		poseStack.scale(size, size, size);
		RenderSystem.setShaderColor(
				(color >> 16 & 255) / 255.0F,
				(color >> 8 & 255) / 255.0F,
				(color & 255) / 255.0F,
				(color >>> 24) / 255.0F);
		this.skyboxBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, GameRenderer.getPositionTexShader());
		VertexBuffer.unbind();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
	}

	private VertexBuffer skyboxBuffer;
}
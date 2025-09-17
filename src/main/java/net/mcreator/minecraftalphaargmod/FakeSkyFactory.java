// Thank you, Nebula, for making this possible.
// If you want to use this code, please credit Nebula. Without him, this wouldn't be possible.
package net.mcreator.minecraftalphaargmod;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderStateShard;
import org.lwjgl.opengl.GL11;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Quaternionf;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class FakeSkyFactory {

    @Nullable
    private RenderType sky;
    private final ResourceLocation texture;
    private TextureTarget skyRenderTarget;
    private VertexBuffer skyboxBuffer;

    public FakeSkyFactory(ResourceLocation skyTexture) {
        this.texture = skyTexture;
        makeRenderType();
    }

	private void makeRenderType() {
	    this.sky = RenderType.create(
	        this.texture.getNamespace() + "_fake_sky", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256, false, false,
	        RenderType.CompositeState.builder()
	            .setShaderState(new RenderStateShard.ShaderStateShard(ShardsHandler::GetFakeSky))
	            .setTextureState(new RenderStateShard.EmptyTextureStateShard(
	                () -> {
	                    if (skyRenderTarget != null)
	                        RenderSystem.setShaderTexture(0, skyRenderTarget.getColorTextureId());
	                },
	                () -> RenderSystem.setShaderTexture(0, 0)
	            ))
	            .createCompositeState(false)
	    );
	}

	public void bake(RenderLevelStageEvent event) {
	    Minecraft mc = Minecraft.getInstance();
	    Window window = mc.getWindow();
	    int width = window.getWidth();
	    int height = window.getHeight();
	
	    if (width <= 0 || height <= 0) return;
	
	    if (this.skyRenderTarget == null || this.skyRenderTarget.width != width || this.skyRenderTarget.height != height) {
	        if (this.skyRenderTarget != null) {
	            this.skyRenderTarget.destroyBuffers();
	        }
	        this.skyRenderTarget = new TextureTarget(width, height, true, Minecraft.ON_OSX);
	    }
	
	    this.skyRenderTarget.bindWrite(true);
	    RenderSystem.clearColor(0, 0, 0, 1);
	    RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
	
		RenderSystem.setShaderTexture(0, this.texture);
	    renderSkybox(0f, 0f, 0f, 0xFFFFFFFF, true, event.getPoseStack(), event.getProjectionMatrix());
	
	    mc.getMainRenderTarget().bindWrite(false);
	}

	public void renderSkybox(float yaw, float pitch, float roll, int color, boolean constant, PoseStack poseStack, Matrix4f projectionMatrix) {
			Minecraft minecraft = Minecraft.getInstance();
			Vec3 pos = minecraft.gameRenderer.getMainCamera().getPosition();
			if (this.skyboxBuffer == null) {
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					RenderSystem.setShader(GameRenderer::getPositionTexShader);
					BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
					bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
					for (int i = 0; i < 6; ++i) {
						switch (i) {
							case 0 :
								bufferBuilder.vertex(-0.5F, -0.5F, -0.5F).uv(0.0F, 0.0F).endVertex();
								bufferBuilder.vertex(-0.5F, -0.5F, 0.5F).uv(0.0F, 0.5F).endVertex();
								bufferBuilder.vertex(0.5F, -0.5F, 0.5F).uv(1.0F / 3.0F, 0.5F).endVertex();
								bufferBuilder.vertex(0.5F, -0.5F, -0.5F).uv(1.0F / 3.0F, 0.0F).endVertex();
								break;
							case 1 :
								bufferBuilder.vertex(-0.5F, 0.5F, 0.5F).uv(1.0F / 3.0F, 0.0F).endVertex();
								bufferBuilder.vertex(-0.5F, 0.5F, -0.5F).uv(1.0F / 3.0F, 0.5F).endVertex();
								bufferBuilder.vertex(0.5F, 0.5F, -0.5F).uv(2.0F / 3.0F, 0.5F).endVertex();
								bufferBuilder.vertex(0.5F, 0.5F, 0.5F).uv(2.0F / 3.0F, 0.0F).endVertex();
								break;
							case 2 :
								bufferBuilder.vertex(0.5F, 0.5F, 0.5F).uv(2.0F / 3.0F, 0.0F).endVertex();
								bufferBuilder.vertex(0.5F, -0.5F, 0.5F).uv(2.0F / 3.0F, 0.5F).endVertex();
								bufferBuilder.vertex(-0.5F, -0.5F, 0.5F).uv(1.0F, 0.5F).endVertex();
								bufferBuilder.vertex(-0.5F, 0.5F, 0.5F).uv(1.0F, 0.0F).endVertex();
								break;
							case 3 :
								bufferBuilder.vertex(-0.5F, 0.5F, 0.5F).uv(0.0F, 0.5F).endVertex();
								bufferBuilder.vertex(-0.5F, -0.5F, 0.5F).uv(0.0F, 1.0F).endVertex();
								bufferBuilder.vertex(-0.5F, -0.5F, -0.5F).uv(1.0F / 3.0F, 1.0F).endVertex();
								bufferBuilder.vertex(-0.5F, 0.5F, -0.5F).uv(1.0F / 3.0F, 0.5F).endVertex();
								break;
							case 4 :
								bufferBuilder.vertex(-0.5F, 0.5F, -0.5F).uv(1.0F / 3.0F, 0.5F).endVertex();
								bufferBuilder.vertex(-0.5F, -0.5F, -0.5F).uv(1.0F / 3.0F, 1.0F).endVertex();
								bufferBuilder.vertex(0.5F, -0.5F, -0.5F).uv(2.0F / 3.0F, 1.0F).endVertex();
								bufferBuilder.vertex(0.5F, 0.5F, -0.5F).uv(2.0F / 3.0F, 0.5F).endVertex();
								break;
							case 5 :
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
				float size = minecraft.options.getEffectiveRenderDistance() << 6;
				poseStack.pushPose();
				poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
				poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
				poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll));
				poseStack.scale(size, size, size);
				RenderSystem.setShaderColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, (color >>> 24) / 255.0F);
				this.skyboxBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, GameRenderer.getPositionTexShader());
				VertexBuffer.unbind();
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				poseStack.popPose();
	}

    private void none() {
    }

	public RenderType get() {
	    return this.sky != null ? this.sky : RenderType.endPortal();
	}
}
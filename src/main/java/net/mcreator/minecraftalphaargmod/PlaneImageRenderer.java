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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PlaneImageRenderer {

    private static final class Entry {
        final VertexBuffer vbo;
        final ResourceLocation texture;
        final Matrix4f matrix;

        Entry(VertexBuffer vbo, ResourceLocation texture, Matrix4f matrix) {
            this.vbo = vbo;
            this.texture = texture;
            this.matrix = matrix;
        }
    }

    private static final List<Entry> queue = new ArrayList<>();
    private static VertexBuffer standardCubeBuffer;
    private static TextureTarget compositeTarget;
    private static VertexBuffer screenQuadBuffer;

    public static VertexBuffer getOrCreateStandardCube() {
        if (standardCubeBuffer == null) {
            Matrix4f identity = new Matrix4f();
            BufferBuilder bb = Tesselator.getInstance().getBuilder();
            bb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            emitStandardCube(bb, identity);
            standardCubeBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            standardCubeBuffer.bind();
            standardCubeBuffer.upload(bb.end());
            VertexBuffer.unbind();
        }
        return standardCubeBuffer;
    }

    public static void enqueue(VertexBuffer vbo, ResourceLocation texture, Matrix4f matrix) {
        queue.add(new Entry(vbo, texture, new Matrix4f(matrix)));
    }

    @SubscribeEvent
    public static void onRenderBlockEntities(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;
        if (queue.isEmpty()) return;

        ShaderInstance planeShader = ShardsHandler.GetPlaneImage();
        if (planeShader == null) { queue.clear(); return; }

        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        int width = window.getWidth();
        int height = window.getHeight();
        if (width <= 0 || height <= 0) { queue.clear(); return; }

        if (compositeTarget == null || compositeTarget.width != width || compositeTarget.height != height) {
            if (compositeTarget != null) compositeTarget.destroyBuffers();
            compositeTarget = new TextureTarget(width, height, true, Minecraft.ON_OSX);
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

        for (Entry entry : queue) {
            RenderSystem.setShaderTexture(0, entry.texture);
            entry.vbo.bind();
            entry.vbo.drawWithShader(entry.matrix, event.getProjectionMatrix(), planeShader);
            VertexBuffer.unbind();
        }

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
        queue.clear();
    }


    public static void emitStandardCube(BufferBuilder bb, Matrix4f m) {
        // SOUTH
        bb.vertex(m, 0f, 0f, 1f).endVertex();
        bb.vertex(m, 1f, 0f, 1f).endVertex();
        bb.vertex(m, 1f, 1f, 1f).endVertex();
        bb.vertex(m, 0f, 1f, 1f).endVertex();
        // NORTH
        bb.vertex(m, 0f, 1f, 0f).endVertex();
        bb.vertex(m, 1f, 1f, 0f).endVertex();
        bb.vertex(m, 1f, 0f, 0f).endVertex();
        bb.vertex(m, 0f, 0f, 0f).endVertex();
        // EAST
        bb.vertex(m, 1f, 1f, 0f).endVertex();
        bb.vertex(m, 1f, 1f, 1f).endVertex();
        bb.vertex(m, 1f, 0f, 1f).endVertex();
        bb.vertex(m, 1f, 0f, 0f).endVertex();
        // WEST
        bb.vertex(m, 0f, 0f, 0f).endVertex();
        bb.vertex(m, 0f, 0f, 1f).endVertex();
        bb.vertex(m, 0f, 1f, 1f).endVertex();
        bb.vertex(m, 0f, 1f, 0f).endVertex();
        // DOWN
        bb.vertex(m, 0f, 0f, 0f).endVertex();
        bb.vertex(m, 1f, 0f, 0f).endVertex();
        bb.vertex(m, 1f, 0f, 1f).endVertex();
        bb.vertex(m, 0f, 0f, 1f).endVertex();
        // UP
        bb.vertex(m, 0f, 1f, 1f).endVertex();
        bb.vertex(m, 1f, 1f, 1f).endVertex();
        bb.vertex(m, 1f, 1f, 0f).endVertex();
        bb.vertex(m, 0f, 1f, 0f).endVertex();
    }

    public static void emitBricksCube(BufferBuilder bb, Matrix4f m) {
        // SOUTH
        bb.vertex(m, 0f,    0f,    0.995f).endVertex();
        bb.vertex(m, 1f,    0f,    0.995f).endVertex();
        bb.vertex(m, 1f,    1f,    0.995f).endVertex();
        bb.vertex(m, 0f,    1f,    0.995f).endVertex();
        // NORTH
        bb.vertex(m, 0f,    1f,    0.005f).endVertex();
        bb.vertex(m, 1f,    1f,    0.005f).endVertex();
        bb.vertex(m, 1f,    0f,    0.005f).endVertex();
        bb.vertex(m, 0f,    0f,    0.005f).endVertex();
        // EAST
        bb.vertex(m, 0.995f, 1f,   0f).endVertex();
        bb.vertex(m, 0.995f, 1f,   0.995f).endVertex();
        bb.vertex(m, 0.995f, 0f,   0.995f).endVertex();
        bb.vertex(m, 0.995f, 0f,   0f).endVertex();
        // WEST
        bb.vertex(m, 0.005f, 0f,   0f).endVertex();
        bb.vertex(m, 0.005f, 0f,   1f).endVertex();
        bb.vertex(m, 0.005f, 1f,   1f).endVertex();
        bb.vertex(m, 0.005f, 1f,   0f).endVertex();
        // BOTTOM
        bb.vertex(m, 0f,    0.005f, 0f).endVertex();
        bb.vertex(m, 1f,    0.005f, 0f).endVertex();
        bb.vertex(m, 1f,    0.005f, 1f).endVertex();
        bb.vertex(m, 0f,    0.005f, 1f).endVertex();
        // TOP
        bb.vertex(m, 0f,    0.995f, 1f).endVertex();
        bb.vertex(m, 1f,    0.995f, 1f).endVertex();
        bb.vertex(m, 1f,    0.995f, 0f).endVertex();
        bb.vertex(m, 0f,    0.995f, 0f).endVertex();
    }
}
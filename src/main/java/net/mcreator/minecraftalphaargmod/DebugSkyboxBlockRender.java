package net.mcreator.minecraftalphaargmod;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import org.lwjgl.opengl.GL20;
import net.minecraft.client.renderer.ShaderInstance;
import com.mojang.blaze3d.shaders.Uniform;
import org.joml.Vector4f;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import net.mcreator.minecraftalphaargmod.FakeSkyRegistry;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.mcreator.minecraftalphaargmod.block.entity.DebugSkyboxBlockEntity;


public class DebugSkyboxBlockRender implements BlockEntityRenderer<DebugSkyboxBlockEntity> {
    
    @Override
    public void render(DebugSkyboxBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        Matrix4f matrix4f = pPoseStack.last().pose();
        renderCube(pBlockEntity, matrix4f, pBufferSource);
    }

    private void renderCube(DebugSkyboxBlockEntity entity, Matrix4f matrix, MultiBufferSource bufferSource) {
        // Render each face with its own texture
    VertexConsumer consumer = bufferSource.getBuffer(FakeSkyRegistry.getDebugSky());
        renderFace(entity, matrix, consumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
        renderFace(entity, matrix, consumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
        renderFace(entity, matrix, consumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
        renderFace(entity, matrix, consumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
        renderFace(entity, matrix, consumer, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
        renderFace(entity, matrix, consumer, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);
    }

    private void renderFace(DebugSkyboxBlockEntity entity, Matrix4f matrix, VertexConsumer buffer, float f, float g, float h, float i, float j, float k, float l, float m, Direction direction) {
        // Add face culling check if needed (like in the reference)
        if (shouldRenderFace(entity, direction)) {
            // Render vertices without UV coordinates since POSITION format doesn't support them
            buffer.vertex(matrix, f, h, j).endVertex();
            buffer.vertex(matrix, g, h, k).endVertex();
            buffer.vertex(matrix, g, i, l).endVertex();
            buffer.vertex(matrix, f, i, m).endVertex();
        }
    }
    
    // Optional: Add face culling logic
    private boolean shouldRenderFace(DebugSkyboxBlockEntity entity, Direction direction) {
        // You can add logic here to determine if a face should be rendered
        // For now, render all faces
        return true;
    }
    
    @Override
    public int getViewDistance() {
        return 256;
    }
}
// Thank you, Nebula, for making this possible.
// If you want to use this code, please credit Nebula. Without him, this wouldn't be possible.
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
import net.mcreator.minecraftalphaargmod.block.entity.PreparationsSkyBlockBlockEntity;

public class PreparationsSkyBlockRender implements BlockEntityRenderer<PreparationsSkyBlockBlockEntity> {

    @Override
    public void render(PreparationsSkyBlockBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        Matrix4f matrix4f = pPoseStack.last().pose();
        renderCube(pBlockEntity, matrix4f, pBufferSource);
    }

    private void renderCube(PreparationsSkyBlockBlockEntity entity, Matrix4f matrix, MultiBufferSource bufferSource) {

    VertexConsumer consumer = bufferSource.getBuffer(FakeSkyRegistry.getKeyPortal());
        renderFace(entity, matrix, consumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
        renderFace(entity, matrix, consumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
        renderFace(entity, matrix, consumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
        renderFace(entity, matrix, consumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
        renderFace(entity, matrix, consumer, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
        renderFace(entity, matrix, consumer, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);
    }

    private void renderFace(PreparationsSkyBlockBlockEntity entity, Matrix4f matrix, VertexConsumer buffer, float f, float g, float h, float i, float j, float k, float l, float m, Direction direction) {

        if (shouldRenderFace(entity, direction)) {

            buffer.vertex(matrix, f, h, j).endVertex();
            buffer.vertex(matrix, g, h, k).endVertex();
            buffer.vertex(matrix, g, i, l).endVertex();
            buffer.vertex(matrix, f, i, m).endVertex();
        }
    }

    private boolean shouldRenderFace(PreparationsSkyBlockBlockEntity entity, Direction direction) {

        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
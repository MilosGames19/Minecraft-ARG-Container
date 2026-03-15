package net.mcreator.minecraftalphaargmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.mcreator.minecraftalphaargmod.block.entity.RenderWireframeBlockBlockEntity;

public class RenderWireframeBlockBlockEntityRenderer implements BlockEntityRenderer<RenderWireframeBlockBlockEntity> {
    private static final float R = 1.0F;
    private static final float G = 1.0F;
    private static final float B = 1.0F;
    private static final float A = 1.0F;

    public RenderWireframeBlockBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RenderWireframeBlockBlockEntity blockEntity, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());

        // A slightly larger offset (0.005) ensures the lines are visible 
        // even when two wireframe blocks are touching.
        float min = -0.0001F;
        float max = 1.005F;

        LevelRenderer.renderLineBox(
            poseStack, consumer, 
            min, min, min, 
            max, max, max, 
            R, G, B, A
        );
    }

    @Override
    public boolean shouldRenderOffScreen(RenderWireframeBlockBlockEntity blockEntity) {
        return true; 
    }
}
// Thank you, Nebula and Noodlegamer76, for making this possible.
// If you want to use this code, please credit Nebula and Noodlegamer76. Without them, this wouldn't be possible.
package net.mcreator.minecraftalphaargmod;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.mcreator.minecraftalphaargmod.block.entity.HubSkyBlockEntity;

public class HubSkyRender implements BlockEntityRenderer<HubSkyBlockEntity> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("the_arg_container", "textures/upscaledhubskyblock.png");

    private static VertexBuffer cubeBuffer;

    @Override
    public void render(HubSkyBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
                       MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if (cubeBuffer == null) {
            org.joml.Matrix4f identity = new org.joml.Matrix4f();
            BufferBuilder bb = Tesselator.getInstance().getBuilder();
            bb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            PlaneImageRenderer.emitStandardCube(bb, identity);
            cubeBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            cubeBuffer.bind();
            cubeBuffer.upload(bb.end());
            VertexBuffer.unbind();
        }
        PlaneImageRenderer.enqueue(cubeBuffer, TEXTURE, pPoseStack.last().pose());
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
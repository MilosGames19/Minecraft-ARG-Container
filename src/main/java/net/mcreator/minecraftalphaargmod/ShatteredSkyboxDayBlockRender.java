// Thank you, Nebula and Noodlegamer76, for making this possible.
// If you want to use this code, please credit Nebula and Noodlegamer76. Without them, this wouldn't be possible.
package net.mcreator.minecraftalphaargmod;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.mcreator.minecraftalphaargmod.block.entity.MoonfallSkyBlockDayBlockEntity;

public class ShatteredSkyboxDayBlockRender implements BlockEntityRenderer<MoonfallSkyBlockDayBlockEntity> {

    @Override
    public void render(MoonfallSkyBlockDayBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
                       MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        FakeSkyRegistry.getShatteredDayFactory().queueMatrix(pPoseStack.last().pose());
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
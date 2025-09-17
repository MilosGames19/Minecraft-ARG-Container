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

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.mcreator.minecraftalphaargmod.block.entity.TheUltimateTruthBlockEntity;

public class TheUltimateTruthRender implements BlockEntityRenderer<TheUltimateTruthBlockEntity> {
  @Override
  public void render(TheUltimateTruthBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
    Matrix4f matrix4f = pPoseStack.last().pose();
    VertexConsumer consumer = pBufferSource.getBuffer((getRenderType()));
    renderCube(matrix4f, consumer);
  }

    
    private static void renderCube(Matrix4f p_173692_, VertexConsumer p_173693_) {
      renderFace(p_173692_, p_173693_, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
      renderFace(p_173692_, p_173693_, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      renderFace(p_173692_, p_173693_, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F);
      renderFace(p_173692_, p_173693_, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F);
      renderFace(p_173692_, p_173693_, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F);
      renderFace(p_173692_, p_173693_, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F);
   }

   private static void renderFace(Matrix4f p_173696_, VertexConsumer p_173697_, float p_173698_, float p_173699_, float p_173700_, float p_173701_, float p_173702_, float p_173703_, float p_173704_, float p_173705_) {
      p_173697_.vertex(p_173696_, p_173698_, p_173700_, p_173702_).endVertex();
      p_173697_.vertex(p_173696_, p_173699_, p_173700_, p_173703_).endVertex();
      p_173697_.vertex(p_173696_, p_173699_, p_173701_, p_173704_).endVertex();
      p_173697_.vertex(p_173696_, p_173698_, p_173701_, p_173705_).endVertex();
   }

   public static RenderType getRenderType() {
        RenderStateShard.ShaderStateShard shaderState = new RenderStateShard.ShaderStateShard(ShardsHandler::GetPlaneImage);

            ResourceLocation OVERLAY = new ResourceLocation("the_arg_container", "textures/ultimatetruth1.png");

        return RenderType.create("generic_render", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(shaderState)
                    .setTextureState(RenderStateShard.MultiTextureStateShard.builder().add(OVERLAY, false, false).build())

                    .setCullState(new RenderStateShard.CullStateShard(true))
                    .createCompositeState(false));
  }

  @Override
    public int getViewDistance() {
        return 256;
    }
}
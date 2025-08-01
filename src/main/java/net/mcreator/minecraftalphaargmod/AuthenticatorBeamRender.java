package net.mcreator.minecraftalphaargmod;

import net.mcreator.minecraftalphaargmod.block.entity.AuthenticatorBlockEntity;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.world.item.DyeColor;

import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

public class AuthenticatorBeamRender implements BlockEntityRenderer<AuthenticatorBlockEntity> {
  public static final ResourceLocation BEAM_LOCATION = new ResourceLocation("textures/entity/beacon_beam.png");

  @Override
  public void render(AuthenticatorBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay)  {
    long i = pBlockEntity.getLevel().getGameTime();
    BeaconRenderer.renderBeaconBeam(pPoseStack,pBufferSource,BEAM_LOCATION ,pPartialTick,1.0F,i,1,1024,DyeColor.BLACK.getTextureDiffuseColors(),0.2F,0.25F);
  }
      @Override
   public boolean shouldRenderOffScreen(AuthenticatorBlockEntity pBlockEntity) {
      return true;
   }

   @Override
   public int getViewDistance() {
      return 256;
   }
  
   @Override
   public boolean shouldRender(AuthenticatorBlockEntity pBlockEntity, Vec3 at) {
      return Vec3.atCenterOf(pBlockEntity.getBlockPos()).multiply(1.0D, 0.0D, 1.0D).closerThan(at.multiply(1.0D, 0.0D, 1.0D), (double)this.getViewDistance());
   }

}
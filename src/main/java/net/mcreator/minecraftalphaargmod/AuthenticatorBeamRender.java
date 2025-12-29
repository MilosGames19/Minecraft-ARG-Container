// Thank you, Nebula, for making this possible.
// If you want to use this code, please credit Nebula. Without him, this wouldn't be possible.
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

  private boolean isEnabled(AuthenticatorBlockEntity pBlockEntity) {
        return pBlockEntity.getPersistentData().getBoolean("EnableABR");
  }

  @Override
  public void render(AuthenticatorBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay)  {
    if (!isEnabled(pBlockEntity)) {
        return; 
    }
    
    long i = pBlockEntity.getLevel().getGameTime();
    BeaconRenderer.renderBeaconBeam(pPoseStack, pBufferSource, BEAM_LOCATION, pPartialTick, 1.0F, i, 0, 1024, DyeColor.BLACK.getTextureDiffuseColors(), 0.2F, 0.25F);
  }
  
  @Override
  public boolean shouldRenderOffScreen(AuthenticatorBlockEntity pBlockEntity) {
    return isEnabled(pBlockEntity); // Only render off-screen if enabled
  }

  @Override
  public int getViewDistance() {
    return 256;
  }
}
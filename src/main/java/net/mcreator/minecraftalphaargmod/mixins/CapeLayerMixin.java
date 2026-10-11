package net.mcreator.minecraftalphaargmod.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mcreator.minecraftalphaargmod.capes.client.ClientCapeCache;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private CapeLayerMixin(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void the_arg_container$renderCustomCape(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        ResourceLocation customTexture = ClientCapeCache.getCape(player.getUUID());
        if (customTexture == null) {
            return;
        }

        if (player.isInvisible() || !player.isModelPartShown(PlayerModelPart.CAPE)) {
            ci.cancel();
            return;
        }

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.is(Items.ELYTRA)) {
            ci.cancel();
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.125F);

        double d0 = Mth.lerp((double) partialTick, player.xCloakO, player.xCloak) - Mth.lerp((double) partialTick, player.xo, player.getX());
        double d1 = Mth.lerp((double) partialTick, player.yCloakO, player.yCloak) - Mth.lerp((double) partialTick, player.yo, player.getY());
        double d2 = Mth.lerp((double) partialTick, player.zCloakO, player.zCloak) - Mth.lerp((double) partialTick, player.zo, player.getZ());

        float f = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);
        double d3 = Mth.sin(f * ((float) Math.PI / 180F));
        double d4 = -Mth.cos(f * ((float) Math.PI / 180F));

        float f1 = Mth.clamp((float) d1 * 10.0F, -6.0F, 32.0F);
        float f2 = Mth.clamp((float) (d0 * d3 + d2 * d4) * 100.0F, 0.0F, 150.0F);
        float f3 = Mth.clamp((float) (d0 * d4 - d2 * d3) * 100.0F, -20.0F, 20.0F);

        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        float f4 = Mth.lerp(partialTick, player.oBob, player.bob);
        f1 += Mth.sin(Mth.lerp(partialTick, player.walkDistO, player.walkDist) * 6.0F) * 32.0F * f4;

        if (player.isCrouching()) {
            f1 += 25.0F;
        }

        poseStack.mulPose(Axis.XP.rotationDegrees(6.0F + f2 / 2.0F + f1));
        poseStack.mulPose(Axis.ZP.rotationDegrees(f3 / 2.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - f3 / 2.0F));

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entitySolid(customTexture));
        this.getParentModel().renderCloak(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
        ci.cancel();
    }
}
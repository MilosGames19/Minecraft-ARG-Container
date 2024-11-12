package net.mcreator.minecraftalphaargmod.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.EntityModel;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports
public class ModelVoid_entity<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("the_arg_container", "model_void_entity"), "main");
	public final ModelPart head;
	public final ModelPart Left_arm;
	public final ModelPart Right_arm;
	public final ModelPart Boddy;
	public final ModelPart Left_leg;
	public final ModelPart Right_leg;

	public ModelVoid_entity(ModelPart root) {
		this.head = root.getChild("head");
		this.Left_arm = root.getChild("Left_arm");
		this.Right_arm = root.getChild("Right_arm");
		this.Boddy = root.getChild("Boddy");
		this.Left_leg = root.getChild("Left_leg");
		this.Right_leg = root.getChild("Right_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(4, 15).addBox(-3.0F, -3.4F, 0.0F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));
		PartDefinition Left_arm = partdefinition.addOrReplaceChild("Left_arm", CubeListBuilder.create(), PartPose.offset(-3.5F, -1.4F, 0.0F));
		PartDefinition cube_r1 = Left_arm.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(16, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 15.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7418F, 0.0F));
		PartDefinition cube_r2 = Left_arm.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(16, 15).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 15.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.3562F, 0.0F));
		PartDefinition Right_arm = partdefinition.addOrReplaceChild("Right_arm", CubeListBuilder.create(), PartPose.offset(3.7F, -2.0F, 0.0F));
		PartDefinition cube_r3 = Right_arm.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 15).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 15.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.6F, 0.0F, 0.0F, 2.3562F, 0.0F));
		PartDefinition cube_r4 = Right_arm.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(12, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 15.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.6F, 0.0F, 0.0F, 0.7418F, 0.0F));
		PartDefinition Boddy = partdefinition.addOrReplaceChild("Boddy", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -14.0F, 0.0F, 6.0F, 15.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 11.6F, -0.1F));
		PartDefinition Left_leg = partdefinition.addOrReplaceChild("Left_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 13.0F, 0.3F));
		PartDefinition cube_r5 = Left_leg.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(8, 22).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.1F, 0.0F, -0.3F, 0.0F, 2.3562F, 0.0F));
		PartDefinition cube_r6 = Left_leg.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(4, 22).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.1F, 0.0F, -0.3F, 0.0F, 0.7418F, 0.0F));
		PartDefinition Right_leg = partdefinition.addOrReplaceChild("Right_leg", CubeListBuilder.create(), PartPose.offset(2.0F, 13.0F, -0.1F));
		PartDefinition cube_r7 = Right_leg.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(20, 12).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.1F, 0.0F, 2.3562F, 0.0F));
		PartDefinition cube_r8 = Right_leg.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(20, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.1F, 0.0F, 0.7418F, 0.0F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Boddy.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw / (180F / (float) Math.PI);
		this.head.xRot = headPitch / (180F / (float) Math.PI);
		this.Left_arm.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
		this.Right_arm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount;
		this.Left_leg.xRot = Mth.cos(limbSwing * 1.0F) * -1.0F * limbSwingAmount;
		this.Right_leg.xRot = Mth.cos(limbSwing * 1.0F) * 1.0F * limbSwingAmount;
	}
}

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

// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports
public class Modelmother<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("the_arg_container", "modelmother"), "main");
	public final ModelPart LeftLeg;
	public final ModelPart RightLeg;
	public final ModelPart Torso;
	public final ModelPart Pelvis;
	public final ModelPart Head;

	public Modelmother(ModelPart root) {
		this.LeftLeg = root.getChild("LeftLeg");
		this.RightLeg = root.getChild("RightLeg");
		this.Torso = root.getChild("Torso");
		this.Pelvis = this.Torso.getChild("Pelvis");
		this.Head = root.getChild("Head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition LeftLeg = partdefinition
				.addOrReplaceChild(
						"LeftLeg", CubeListBuilder.create().texOffs(20, 37).addBox(-4.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 60).addBox(-4.0F, 10.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
								.texOffs(20, 36).addBox(-4.0F, 12.0F, -2.0F, 4.0F, 18.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(20, 14).addBox(-6.0F, 30.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
						PartPose.offset(-3.0F, -10.0F, 0.0F));
		PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg",
				CubeListBuilder.create().texOffs(0, 1).addBox(-2.0F, 0.0F, -2.0F, 6.0F, 34.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(54, 30).addBox(-2.0F, 30.0F, -4.0F, 6.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(5.0F, -10.0F, 0.0F));
		PartDefinition Torso = partdefinition.addOrReplaceChild("Torso",
				CubeListBuilder.create().texOffs(52, 0).addBox(-4.0F, -8.0F, 0.0F, 8.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(19, 0).addBox(-6.0F, -18.0F, -2.0F, 12.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 38)
						.addBox(-8.0F, -16.0F, -2.0F, 2.0F, 18.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 38).addBox(6.0F, -16.0F, -2.0F, 2.0F, 18.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(1.0F, -12.0F, 0.0F));
		PartDefinition Pelvis = Torso.addOrReplaceChild("Pelvis",
				CubeListBuilder.create().texOffs(32, 5).addBox(-4.0F, -2.0F, -2.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(2, 29).addBox(2.0F, -2.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 2.0F, 0.0F));
		PartDefinition Head = partdefinition.addOrReplaceChild("Head",
				CubeListBuilder.create().texOffs(6, 30).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(52, 16).addBox(-4.0F, -4.0F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(36, 42)
						.addBox(-6.0F, -6.0F, -2.0F, 12.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(36, 36).addBox(-6.0F, -14.0F, -2.0F, 12.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(52, 10)
						.addBox(-4.0F, -16.0F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(8, 12).addBox(6.0F, -12.0F, 0.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(8, 11)
						.addBox(-8.0F, -12.0F, 0.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(20, 40).addBox(-2.0F, -18.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(20, 26)
						.addBox(-6.0F, -12.0F, -2.0F, 12.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(1.0F, -30.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		LeftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		RightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Torso.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.LeftLeg.xRot = Mth.cos(limbSwing * 1.0F) * -1.0F * limbSwingAmount;
		this.Head.yRot = netHeadYaw / (180F / (float) Math.PI);
		this.Head.xRot = headPitch / (180F / (float) Math.PI);
		this.RightLeg.xRot = Mth.cos(limbSwing * 1.0F) * 1.0F * limbSwingAmount;
	}
}

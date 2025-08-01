package net.mcreator.minecraftalphaargmod.client.model;

import net.minecraft.world.entity.Entity;
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

// Made with Blockbench 4.12.5
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports
public class Modelcore<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("the_arg_container", "modelcore"), "main");
	public final ModelPart bone;
	public final ModelPart bone2;
	public final ModelPart bone3;

	public Modelcore(ModelPart root) {
		this.bone = root.getChild("bone");
		this.bone2 = root.getChild("bone2");
		this.bone3 = root.getChild("bone3");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(196, 97).addBox(-24.0F, -24.0F, -24.0F, 48.0F, 48.0F, 48.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition bone2 = partdefinition.addOrReplaceChild("bone2",
				CubeListBuilder.create().texOffs(0, 0).addBox(-43.5F, -5.0F, -43.5F, 11.0F, 10.0F, 87.0F, new CubeDeformation(0.0F)).texOffs(0, 97).addBox(32.5F, -5.0F, -43.5F, 11.0F, 10.0F, 87.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-0.5F, -2.0F, -0.5F));
		PartDefinition cube_r1 = bone2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(196, 268).addBox(13.0F, -10.0F, -20.0F, 11.0F, 10.0F, 65.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(12.5F, 5.0F, 19.5F, 0.0F, -1.5708F, 0.0F));
		PartDefinition cube_r2 = bone2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(196, 193).addBox(13.0F, -10.0F, -20.0F, 11.0F, 10.0F, 65.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(12.5F, 5.0F, -56.5F, 0.0F, -1.5708F, 0.0F));
		PartDefinition bone3 = partdefinition.addOrReplaceChild("bone3",
				CubeListBuilder.create().texOffs(0, 194).addBox(-43.5F, -5.0F, -43.5F, 11.0F, 10.0F, 87.0F, new CubeDeformation(0.0F)).texOffs(196, 0).addBox(32.5F, -5.0F, -43.5F, 11.0F, 10.0F, 87.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.5F, -2.0F, -0.5F, -1.5708F, 0.0F, 0.0F));
		PartDefinition cube_r3 = bone3.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(152, 343).addBox(13.0F, -10.0F, -20.0F, 11.0F, 10.0F, 65.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(12.5F, 5.0F, 19.5F, 0.0F, -1.5708F, 0.0F));
		PartDefinition cube_r4 = bone3.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 291).addBox(13.0F, -10.0F, -20.0F, 11.0F, 10.0F, 65.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(12.5F, 5.0F, -56.5F, 0.0F, -1.5708F, 0.0F));
		return LayerDefinition.create(meshdefinition, 512, 512);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bone2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bone3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.bone3.zRot = ageInTicks / 20.f;
		this.bone2.xRot = ageInTicks / 20.f;
		this.bone.yRot = ageInTicks / 20.f;
	}
}

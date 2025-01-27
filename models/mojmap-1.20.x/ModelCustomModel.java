// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class ModelCustomModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "custommodel"), "main");
	private final ModelPart Head;
	private final ModelPart Larm;
	private final ModelPart Rarm;
	private final ModelPart Rleg;
	private final ModelPart Lleg;
	private final ModelPart bb_main;

	public ModelCustomModel(ModelPart root) {
		this.Head = root.getChild("Head");
		this.Larm = root.getChild("Larm");
		this.Rarm = root.getChild("Rarm");
		this.Rleg = root.getChild("Rleg");
		this.Lleg = root.getChild("Lleg");
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(24, 7).addBox(
				-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -22.0F, 0.0F));

		PartDefinition Larm = partdefinition.addOrReplaceChild("Larm", CubeListBuilder.create().texOffs(20, 0)
				.addBox(-0.6F, -0.6F, -0.5F, 1.0F, 31.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offset(4.6F, -17.4F, 0.0F));

		PartDefinition Rarm = partdefinition.addOrReplaceChild("Rarm", CubeListBuilder.create().texOffs(16, 0)
				.addBox(-0.4F, -1.1F, -0.5F, 1.0F, 31.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-4.6F, -16.9F, 0.0F));

		PartDefinition Rleg = partdefinition.addOrReplaceChild("Rleg", CubeListBuilder.create().texOffs(0, 0)
				.addBox(-1.0F, -1.0F, -1.0F, 2.0F, 26.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-2.0F, -1.0F, 0.0F));

		PartDefinition Lleg = partdefinition.addOrReplaceChild("Lleg", CubeListBuilder.create().texOffs(8, 0).addBox(
				-1.0F, -1.0F, -1.0F, 2.0F, 26.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -1.0F, 0.0F));

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main",
				CubeListBuilder.create().texOffs(24, 15)
						.addBox(-1.0F, -37.0F, -1.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(24, 0)
						.addBox(-4.0F, -42.0F, -1.0F, 8.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 28)
						.addBox(-0.5F, -46.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Larm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Rarm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Rleg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Lleg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
		this.Head.yRot = netHeadYaw / (180F / (float) Math.PI);
		this.Head.xRot = headPitch / (180F / (float) Math.PI);
		this.Larm.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
		this.Lleg.xRot = Mth.cos(limbSwing * 1.0F) * -1.0F * limbSwingAmount;
		this.Rleg.xRot = Mth.cos(limbSwing * 1.0F) * 1.0F * limbSwingAmount;
		this.Rarm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount;
	}
}
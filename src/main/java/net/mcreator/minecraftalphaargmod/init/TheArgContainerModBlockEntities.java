
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.minecraftalphaargmod.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.entity.BlockEntityType;

import net.mcreator.minecraftalphaargmod.block.entity.UnusedBlock2BlockEntity;
import net.mcreator.minecraftalphaargmod.block.entity.Unknown2BlockEntity;
import net.mcreator.minecraftalphaargmod.block.entity.TheUltimateTruthBricksBlockEntity;
import net.mcreator.minecraftalphaargmod.block.entity.TheUltimateTruthBlockEntity;
import net.mcreator.minecraftalphaargmod.block.entity.StructureBuilderBlockEntity;
import net.mcreator.minecraftalphaargmod.block.entity.ServerContainersBlockEntity;
import net.mcreator.minecraftalphaargmod.block.entity.SafeBlockEntity;
import net.mcreator.minecraftalphaargmod.block.entity.PerlinKeyblockBlockEntity;
import net.mcreator.minecraftalphaargmod.block.entity.MoonfallGeneratorBlockEntity;
import net.mcreator.minecraftalphaargmod.block.entity.KeySlotterBlockEntity;
import net.mcreator.minecraftalphaargmod.block.entity.HubSkyBlockEntity;
import net.mcreator.minecraftalphaargmod.block.entity.FreezerBlockEntity;
import net.mcreator.minecraftalphaargmod.block.entity.AuthenticatorBlockEntity;
import net.mcreator.minecraftalphaargmod.block.entity.AdminChestBlockEntity;
import net.mcreator.minecraftalphaargmod.TheArgContainerMod;

public class TheArgContainerModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TheArgContainerMod.MODID);
	public static final RegistryObject<BlockEntityType<FreezerBlockEntity>> FREEZER = REGISTRY.register("freezer", () -> BlockEntityType.Builder.of(FreezerBlockEntity::new, TheArgContainerModBlocks.FREEZER.get()).build(null));
	public static final RegistryObject<BlockEntityType<SafeBlockEntity>> SAFE = REGISTRY.register("safe", () -> BlockEntityType.Builder.of(SafeBlockEntity::new, TheArgContainerModBlocks.SAFE.get()).build(null));
	public static final RegistryObject<BlockEntityType<UnusedBlock2BlockEntity>> UNUSED_BLOCK_2 = REGISTRY.register("unused_block_2",
			() -> BlockEntityType.Builder.of(UnusedBlock2BlockEntity::new, TheArgContainerModBlocks.UNUSED_BLOCK_2.get()).build(null));
	public static final RegistryObject<BlockEntityType<KeySlotterBlockEntity>> KEY_SLOTTER = REGISTRY.register("key_slotter", () -> BlockEntityType.Builder.of(KeySlotterBlockEntity::new, TheArgContainerModBlocks.KEY_SLOTTER.get()).build(null));
	public static final RegistryObject<BlockEntityType<ServerContainersBlockEntity>> SERVER_CONTAINERS = REGISTRY.register("server_containers",
			() -> BlockEntityType.Builder.of(ServerContainersBlockEntity::new, TheArgContainerModBlocks.SERVER_CONTAINERS.get()).build(null));
	public static final RegistryObject<BlockEntityType<AuthenticatorBlockEntity>> AUTHENTICATOR = REGISTRY.register("authenticator",
			() -> BlockEntityType.Builder.of(AuthenticatorBlockEntity::new, TheArgContainerModBlocks.AUTHENTICATOR.get()).build(null));
	public static final RegistryObject<BlockEntityType<Unknown2BlockEntity>> UNKNOWN_2 = REGISTRY.register("unknown_2", () -> BlockEntityType.Builder.of(Unknown2BlockEntity::new, TheArgContainerModBlocks.UNKNOWN_2.get()).build(null));
	public static final RegistryObject<BlockEntityType<PerlinKeyblockBlockEntity>> PERLIN_KEYBLOCK = REGISTRY.register("perlin_keyblock",
			() -> BlockEntityType.Builder.of(PerlinKeyblockBlockEntity::new, TheArgContainerModBlocks.PERLIN_KEYBLOCK.get()).build(null));
	public static final RegistryObject<BlockEntityType<AdminChestBlockEntity>> ADMIN_CHEST = REGISTRY.register("admin_chest", () -> BlockEntityType.Builder.of(AdminChestBlockEntity::new, TheArgContainerModBlocks.ADMIN_CHEST.get()).build(null));
	public static final RegistryObject<BlockEntityType<StructureBuilderBlockEntity>> STRUCTURE_BUILDER = REGISTRY.register("structure_builder",
			() -> BlockEntityType.Builder.of(StructureBuilderBlockEntity::new, TheArgContainerModBlocks.STRUCTURE_BUILDER.get()).build(null));
	public static final RegistryObject<BlockEntityType<MoonfallGeneratorBlockEntity>> MOONFALL_GENERATOR = REGISTRY.register("moonfall_generator",
			() -> BlockEntityType.Builder.of(MoonfallGeneratorBlockEntity::new, TheArgContainerModBlocks.MOONFALL_GENERATOR.get()).build(null));
	public static final RegistryObject<BlockEntityType<HubSkyBlockEntity>> HUB_SKY = REGISTRY.register("hub_sky", () -> BlockEntityType.Builder.of(HubSkyBlockEntity::new, TheArgContainerModBlocks.HUB_SKY.get()).build(null));
	public static final RegistryObject<BlockEntityType<TheUltimateTruthBlockEntity>> THE_ULTIMATE_TRUTH = REGISTRY.register("the_ultimate_truth",
			() -> BlockEntityType.Builder.of(TheUltimateTruthBlockEntity::new, TheArgContainerModBlocks.THE_ULTIMATE_TRUTH.get()).build(null));
	public static final RegistryObject<BlockEntityType<TheUltimateTruthBricksBlockEntity>> THE_ULTIMATE_TRUTH_BRICKS = REGISTRY.register("the_ultimate_truth_bricks",
			() -> BlockEntityType.Builder.of(TheUltimateTruthBricksBlockEntity::new, TheArgContainerModBlocks.THE_ULTIMATE_TRUTH_BRICKS.get()).build(null));
}

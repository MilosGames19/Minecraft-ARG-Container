
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.minecraftalphaargmod.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;

import net.mcreator.minecraftalphaargmod.block.entity.UnusedBlock2BlockEntity;
import net.mcreator.minecraftalphaargmod.block.entity.Unknown2BlockEntity;
import net.mcreator.minecraftalphaargmod.block.entity.ServerContainersBlockEntity;
import net.mcreator.minecraftalphaargmod.block.entity.SafeBlockEntity;
import net.mcreator.minecraftalphaargmod.block.entity.KeySlotterBlockEntity;
import net.mcreator.minecraftalphaargmod.MinecraftAlphaArgModMod;

public class MinecraftAlphaArgModModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MinecraftAlphaArgModMod.MODID);
	public static final RegistryObject<BlockEntityType<?>> SAFE = register("safe", MinecraftAlphaArgModModBlocks.SAFE, SafeBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> UNUSED_BLOCK_2 = register("unused_block_2", MinecraftAlphaArgModModBlocks.UNUSED_BLOCK_2, UnusedBlock2BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> KEY_SLOTTER = register("key_slotter", MinecraftAlphaArgModModBlocks.KEY_SLOTTER, KeySlotterBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> SERVER_CONTAINERS = register("server_containers", MinecraftAlphaArgModModBlocks.SERVER_CONTAINERS, ServerContainersBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> UNKNOWN_2 = register("unknown_2", MinecraftAlphaArgModModBlocks.UNKNOWN_2, Unknown2BlockEntity::new);

	private static RegistryObject<BlockEntityType<?>> register(String registryname, RegistryObject<Block> block, BlockEntityType.BlockEntitySupplier<?> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
	}
}

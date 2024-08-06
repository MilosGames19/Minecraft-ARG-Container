package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModItems;
import net.mcreator.minecraftalphaargmod.MinecraftAlphaArgModMod;

public class PlaceProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (world instanceof ServerLevel _serverworld) {
			StructureTemplate template = _serverworld.getStructureManager().getOrCreate(new ResourceLocation("minecraft_alpha_arg_mod", "asset"));
			if (template != null) {
				template.placeInWorld(_serverworld, BlockPos.containing(x - 4, y, z - 4), BlockPos.containing(x - 4, y, z - 4), new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false),
						_serverworld.random, 3);
			}
		}
		MinecraftAlphaArgModMod.queueServerWork(120, () -> {
			if (!world.isClientSide() && world.getServer() != null)
				world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Do it."), false);
			MinecraftAlphaArgModMod.queueServerWork(20, () -> {
				if (!world.isClientSide() && world.getServer() != null)
					world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Now."), false);
			});
		});
		if (entity instanceof Player _player) {
			ItemStack _setstack = new ItemStack(MinecraftAlphaArgModModItems.VOID_KEY.get()).copy();
			_setstack.setCount(4);
			ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
		}
	}
}

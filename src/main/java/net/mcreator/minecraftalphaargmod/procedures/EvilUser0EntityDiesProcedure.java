package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModItems;
import net.mcreator.minecraftalphaargmod.TheArgContainerMod;

public class EvilUser0EntityDiesProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (Math.random() < 0.1) {
			if (world instanceof ServerLevel _level) {
				ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(TheArgContainerModItems.SCYTHE.get()));
				entityToSpawn.setPickUpDelay(10);
				_level.addFreshEntity(entityToSpawn);
			}
		}
		if (!world.isClientSide() && world.getServer() != null)
			world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("<user0> \u00A74THIS ISN'T OVER."), false);
		TheArgContainerMod.queueServerWork(20, () -> {
			if (!world.isClientSide() && world.getServer() != null)
				world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("<user0> \u00A74I WILL BE BACK FOR MY REVENGE!"), false);
			TheArgContainerMod.queueServerWork(20, () -> {
				if (!world.isClientSide() && world.getServer() != null)
					world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("\u00A7o[SERVER: revoked user0 server operator privileges]"), false);
			});
		});
	}
}

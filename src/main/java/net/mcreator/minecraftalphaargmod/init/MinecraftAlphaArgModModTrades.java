
/*
*	MCreator note: This file will be REGENERATED on each build.
*/
package net.mcreator.minecraftalphaargmod.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.common.BasicItemListing;

import net.minecraft.world.item.ItemStack;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MinecraftAlphaArgModModTrades {
	@SubscribeEvent
	public static void registerTrades(VillagerTradesEvent event) {
		if (event.getType() == MinecraftAlphaArgModModVillagerProfessions.GLITCHED_VILLAGER.get()) {
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(MinecraftAlphaArgModModItems.ESSENCE.get(), 20),

					new ItemStack(MinecraftAlphaArgModModBlocks.COMPRESSED_RIFT.get(), 5), 10, 5, 0.05f));
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(MinecraftAlphaArgModModItems.ESSENCE.get(), 20),

					new ItemStack(MinecraftAlphaArgModModBlocks.COMPRESSED_STATIC.get(), 5), 10, 5, 0.05f));
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(MinecraftAlphaArgModModItems.ESSENCE.get()),

					new ItemStack(MinecraftAlphaArgModModBlocks.DEFECTED_WOOD.get(), 5), 10, 5, 0.05f));
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(MinecraftAlphaArgModModItems.ESSENCE.get()),

					new ItemStack(MinecraftAlphaArgModModBlocks.FRACTURED_INFLUENCE.get(), 5), 10, 5, 0.05f));
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(MinecraftAlphaArgModModItems.ESSENCE.get()),

					new ItemStack(MinecraftAlphaArgModModBlocks.FRACTURED_MIST.get(), 5), 10, 5, 0.05f));
			event.getTrades().get(2).add(new BasicItemListing(new ItemStack(MinecraftAlphaArgModModBlocks.ERROR_BLOCK.get()),

					new ItemStack(MinecraftAlphaArgModModBlocks.ESSENCE_CACHE.get(), 30), 10, 5, 0.05f));
			event.getTrades().get(2).add(new BasicItemListing(new ItemStack(MinecraftAlphaArgModModItems.ESSENCE.get(), 52),

					new ItemStack(MinecraftAlphaArgModModBlocks.UNKNOWN.get()), 10, 5, 0.05f));
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(MinecraftAlphaArgModModItems.ESSENCE.get(), 12),

					new ItemStack(MinecraftAlphaArgModModBlocks.UNKNOWN_3.get(), 4), 10, 5, 0.05f));
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(MinecraftAlphaArgModModItems.ESSENCE.get(), 62),

					new ItemStack(MinecraftAlphaArgModModBlocks.UNKNOWN_22.get()), 10, 5, 0.05f));
		}
	}
}

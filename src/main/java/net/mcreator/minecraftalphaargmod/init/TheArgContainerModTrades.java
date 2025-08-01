
/*
*	MCreator note: This file will be REGENERATED on each build.
*/
package net.mcreator.minecraftalphaargmod.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.common.BasicItemListing;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TheArgContainerModTrades {
	@SubscribeEvent
	public static void registerWanderingTrades(WandererTradesEvent event) {
		event.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.EMERALD),

				new ItemStack(TheArgContainerModItems.SKY_SHARD.get(), 5), 20, 5, 0.05f));
		event.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.EMERALD, 3),

				new ItemStack(TheArgContainerModItems.SKY_SHARD.get(), 15), 10, 5, 0.05f));
		event.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.EMERALD, 5),

				new ItemStack(TheArgContainerModItems.SKY_SHARD.get(), 34), 5, 5, 0.05f));
	}

	@SubscribeEvent
	public static void registerTrades(VillagerTradesEvent event) {
		if (event.getType() == TheArgContainerModVillagerProfessions.GLITCHED_VILLAGER.get()) {
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(TheArgContainerModItems.ESSENCE.get(), 20),

					new ItemStack(TheArgContainerModBlocks.COMPRESSED_RIFT.get(), 5), 10, 5, 0.05f));
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(TheArgContainerModItems.ESSENCE.get(), 20),

					new ItemStack(TheArgContainerModBlocks.COMPRESSED_STATIC.get(), 5), 10, 5, 0.05f));
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(TheArgContainerModItems.ESSENCE.get()),

					new ItemStack(TheArgContainerModBlocks.DEFECTED_WOOD.get(), 5), 10, 5, 0.05f));
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(TheArgContainerModItems.ESSENCE.get()),

					new ItemStack(TheArgContainerModBlocks.FRACTURED_INFLUENCE.get(), 5), 10, 5, 0.05f));
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(TheArgContainerModItems.ESSENCE.get()),

					new ItemStack(TheArgContainerModBlocks.FRACTURED_MIST.get(), 5), 10, 5, 0.05f));
			event.getTrades().get(2).add(new BasicItemListing(new ItemStack(TheArgContainerModBlocks.ERROR_BLOCK.get()),

					new ItemStack(TheArgContainerModBlocks.ESSENCE_CACHE.get(), 30), 10, 5, 0.05f));
			event.getTrades().get(2).add(new BasicItemListing(new ItemStack(TheArgContainerModItems.ESSENCE.get(), 52),

					new ItemStack(TheArgContainerModBlocks.UNKNOWN.get()), 10, 5, 0.05f));
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(TheArgContainerModItems.ESSENCE.get(), 12),

					new ItemStack(TheArgContainerModBlocks.UNKNOWN_3.get(), 4), 10, 5, 0.05f));
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(TheArgContainerModItems.ESSENCE.get(), 62),

					new ItemStack(TheArgContainerModBlocks.UNKNOWN_22.get()), 10, 5, 0.05f));
		}
	}
}

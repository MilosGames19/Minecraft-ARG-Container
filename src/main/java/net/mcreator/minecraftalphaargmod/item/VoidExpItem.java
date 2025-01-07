
package net.mcreator.minecraftalphaargmod.item;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;

public class VoidExpItem extends RecordItem {
	public VoidExpItem() {
		super(0, () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("the_arg_container:voidexp")), new Item.Properties().stacksTo(1).rarity(Rarity.COMMON), 1320);
	}
}

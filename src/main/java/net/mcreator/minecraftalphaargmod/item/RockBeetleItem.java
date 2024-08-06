
package net.mcreator.minecraftalphaargmod.item;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;

public class RockBeetleItem extends RecordItem {
	public RockBeetleItem() {
		super(0, () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("minecraft_alpha_arg_mod:rokkubitorurune")), new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 580);
	}
}

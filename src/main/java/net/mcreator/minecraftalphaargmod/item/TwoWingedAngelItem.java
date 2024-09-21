
package net.mcreator.minecraftalphaargmod.item;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;

public class TwoWingedAngelItem extends RecordItem {
	public TwoWingedAngelItem() {
		super(0, () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("the_arg_container:two_winged_angel")), new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 1680);
	}
}


package net.mcreator.minecraftalphaargmod.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class IconItem extends Item {
	public IconItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}
}


package net.mcreator.minecraftalphaargmod.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class MidgemItem extends Item {
	public MidgemItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}
}

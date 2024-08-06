
package net.mcreator.minecraftalphaargmod.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class MyconStrandItem extends Item {
	public MyconStrandItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}
}

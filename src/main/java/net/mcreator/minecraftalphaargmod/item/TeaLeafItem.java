
package net.mcreator.minecraftalphaargmod.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class TeaLeafItem extends Item {
	public TeaLeafItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}
}

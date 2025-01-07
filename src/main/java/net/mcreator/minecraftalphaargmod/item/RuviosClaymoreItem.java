
package net.mcreator.minecraftalphaargmod.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModItems;

public class RuviosClaymoreItem extends SwordItem {
	public RuviosClaymoreItem() {
		super(new Tier() {
			public int getUses() {
				return 5360;
			}

			public float getSpeed() {
				return 4f;
			}

			public float getAttackDamageBonus() {
				return 14f;
			}

			public int getLevel() {
				return 0;
			}

			public int getEnchantmentValue() {
				return 2;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(TheArgContainerModItems.VOID_GEM.get()));
			}
		}, 3, -2.4f, new Item.Properties().fireResistant());
	}
}

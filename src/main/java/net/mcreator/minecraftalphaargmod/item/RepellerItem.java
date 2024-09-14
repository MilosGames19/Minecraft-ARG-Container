
package net.mcreator.minecraftalphaargmod.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModItems;

public class RepellerItem extends SwordItem {
	public RepellerItem() {
		super(new Tier() {
			public int getUses() {
				return 13000;
			}

			public float getSpeed() {
				return 4f;
			}

			public float getAttackDamageBonus() {
				return 10f;
			}

			public int getLevel() {
				return 1;
			}

			public int getEnchantmentValue() {
				return 2;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(MinecraftAlphaArgModModItems.VOID_GEM.get()));
			}
		}, 3, 0f, new Item.Properties());
	}
}

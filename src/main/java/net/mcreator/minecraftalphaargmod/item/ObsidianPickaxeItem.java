
package net.mcreator.minecraftalphaargmod.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModItems;

public class ObsidianPickaxeItem extends PickaxeItem {
	public ObsidianPickaxeItem() {
		super(new Tier() {
			public int getUses() {
				return 5561;
			}

			public float getSpeed() {
				return 14f;
			}

			public float getAttackDamageBonus() {
				return 2f;
			}

			public int getLevel() {
				return 3;
			}

			public int getEnchantmentValue() {
				return 23;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(TheArgContainerModItems.OBSIDIAN_INGOT.get()));
			}
		}, 1, -2.8f, new Item.Properties());
	}
}


package net.mcreator.minecraftalphaargmod.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModItems;

public class ObsidianSwordItem extends SwordItem {
	public ObsidianSwordItem() {
		super(new Tier() {
			public int getUses() {
				return 5561;
			}

			public float getSpeed() {
				return 4f;
			}

			public float getAttackDamageBonus() {
				return 4f;
			}

			public int getLevel() {
				return 1;
			}

			public int getEnchantmentValue() {
				return 22;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(TheArgContainerModItems.OBSIDIAN_INGOT.get()));
			}
		}, 3, -3f, new Item.Properties());
	}
}

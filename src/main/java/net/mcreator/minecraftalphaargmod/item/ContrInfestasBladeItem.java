
package net.mcreator.minecraftalphaargmod.item;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModItems;

public class ContrInfestasBladeItem extends SwordItem {
	public ContrInfestasBladeItem() {
		super(new Tier() {
			public int getUses() {
				return 5870;
			}

			public float getSpeed() {
				return 1f;
			}

			public float getAttackDamageBonus() {
				return 7f;
			}

			public int getLevel() {
				return 1;
			}

			public int getEnchantmentValue() {
				return 18;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(TheArgContainerModItems.BLATTEA_SHARD.get()), new ItemStack(Items.NETHERITE_INGOT), new ItemStack(Blocks.SOUL_LANTERN), new ItemStack(TheArgContainerModItems.ABYSS_CRYSTAL.get()),
						new ItemStack(Blocks.SOUL_CAMPFIRE));
			}
		}, 3, -2.8f, new Item.Properties().fireResistant());
	}
}

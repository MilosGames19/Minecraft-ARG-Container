
package net.mcreator.minecraftalphaargmod.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.LivingEntity;

import net.mcreator.minecraftalphaargmod.procedures.CustomIcon1LivingEntityIsHitWithItemProcedure;

public class CustomIcon1Item extends Item {
	public CustomIcon1Item() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
		CustomIcon1LivingEntityIsHitWithItemProcedure.execute(entity);
		return retval;
	}
}

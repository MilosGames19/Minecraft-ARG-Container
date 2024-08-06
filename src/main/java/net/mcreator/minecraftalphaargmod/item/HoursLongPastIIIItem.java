
package net.mcreator.minecraftalphaargmod.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;

import net.mcreator.minecraftalphaargmod.procedures.HoursLongPastIIIRightclickedProcedure;

public class HoursLongPastIIIItem extends Item {
	public HoursLongPastIIIItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		HoursLongPastIIIRightclickedProcedure.execute(world);
		return ar;
	}
}


package net.mcreator.minecraftalphaargmod.item;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;

import net.mcreator.minecraftalphaargmod.procedures.ClaytonM634TheCoreHouseRightclickedOnBlockProcedure;

public class ClaytonM634TheCoreHouseItem extends Item {
	public ClaytonM634TheCoreHouseItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		ClaytonM634TheCoreHouseRightclickedOnBlockProcedure.execute(context.getLevel(), context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ());
		return InteractionResult.SUCCESS;
	}
}

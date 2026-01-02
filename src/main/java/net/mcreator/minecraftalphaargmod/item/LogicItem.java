
package net.mcreator.minecraftalphaargmod.item;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;

import net.mcreator.minecraftalphaargmod.procedures.LogicRightclickedOnBlockProcedure;

public class LogicItem extends Item {
	public LogicItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		LogicRightclickedOnBlockProcedure.execute(context.getPlayer());
		return InteractionResult.SUCCESS;
	}
}


package net.mcreator.minecraftalphaargmod.item;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.minecraftalphaargmod.procedures.User0RightclickedOnBlockProcedure;

public class User0Item extends RecordItem {
	public User0Item() {
		super(0, () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("the_arg_container:user0")), new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), 3100);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		User0RightclickedOnBlockProcedure.execute(context.getLevel(), context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ(), context.getPlayer());
		return InteractionResult.SUCCESS;
	}
}

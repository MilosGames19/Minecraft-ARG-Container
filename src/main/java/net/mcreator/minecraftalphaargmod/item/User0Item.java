
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
		super(0, () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("minecraft_alpha_arg_mod:user0")), new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), 3100);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		User0RightclickedOnBlockProcedure.execute(context.getPlayer());
		return InteractionResult.SUCCESS;
	}
}

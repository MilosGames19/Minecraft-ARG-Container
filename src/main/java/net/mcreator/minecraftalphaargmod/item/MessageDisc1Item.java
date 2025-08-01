
package net.mcreator.minecraftalphaargmod.item;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import java.util.List;

public class MessageDisc1Item extends RecordItem {
	public MessageDisc1Item() {
		super(0, () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.sculk_shrieker.shriek")), new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 560);
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.literal("\u00A74W\u00A7kA\u00A74R\u00A7cN\u00A7kI\u00A74N\u00A7cG\u00A78: \u00A74A\u00A7kN\u00A74O\u00A7cM\u00A7kA\u00A74L\u00A7cY \u00A78D\u00A74E\u00A7kT\u00A74E\u00A7cC\u00A74T\u00A7kE\u00A74D\u00A7r"));
	}
}

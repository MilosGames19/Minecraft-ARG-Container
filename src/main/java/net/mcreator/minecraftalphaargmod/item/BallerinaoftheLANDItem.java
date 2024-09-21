
package net.mcreator.minecraftalphaargmod.item;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;

public class BallerinaoftheLANDItem extends RecordItem {
	public BallerinaoftheLANDItem() {
		super(0, () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("the_arg_container:ballerinaoftheland")), new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 1920);
	}
}

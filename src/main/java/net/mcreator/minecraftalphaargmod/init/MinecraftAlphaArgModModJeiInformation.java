
package net.mcreator.minecraftalphaargmod.init;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.IModPlugin;

import java.util.List;

@JeiPlugin
public class MinecraftAlphaArgModModJeiInformation implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation("minecraft_alpha_arg_mod:information");
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addIngredientInfo(List.of(new ItemStack(MinecraftAlphaArgModModBlocks.CELESTIAL_FLAME.get())), VanillaTypes.ITEM_STACK, Component.translatable("jei.minecraft_alpha_arg_mod.celestialflameinfo"));
	}
}

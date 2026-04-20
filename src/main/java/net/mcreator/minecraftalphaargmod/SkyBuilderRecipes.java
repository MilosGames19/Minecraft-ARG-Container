package net.mcreator.minecraftalphaargmod.world.inventory;

import net.minecraft.world.item.ItemStack;
import net.mcreator.minecraftalphaargmod.init.TheArgContainerModItems;

import java.util.List;
import java.util.function.Supplier;

public final class SkyBuilderRecipes {

    private SkyBuilderRecipes() {}

    public record Recipe(int inputCount, Supplier<ItemStack> output) {
        public ItemStack getOutput() {
            return output.get().copy();
        }
    }

    public static final List<Recipe> RECIPES = List.of(
            new Recipe( 8, () -> new ItemStack(TheArgContainerModItems.HUB_SKY.get())),
            new Recipe( 8, () -> new ItemStack(TheArgContainerModItems.HUB_SKY_TWO.get())),
            new Recipe( 8, () -> new ItemStack(TheArgContainerModItems.HUB_SKY_THREE.get())),
            new Recipe( 8, () -> new ItemStack(TheArgContainerModItems.HUB_SKY_FOUR.get())),
            new Recipe( 8, () -> new ItemStack(TheArgContainerModItems.HUB_SKY_FIVE.get())),
            new Recipe( 8, () -> new ItemStack(TheArgContainerModItems.THE_ULTIMATE_TRUTH.get())),
            new Recipe( 8, () -> new ItemStack(TheArgContainerModItems.THE_ULTIMATE_TRUTH_BRICKS.get())),
            new Recipe(16, () -> new ItemStack(TheArgContainerModItems.HUB_SKYBOX.get())),
            new Recipe(16, () -> new ItemStack(TheArgContainerModItems.PREPARATIONS_SKY_BLOCK.get())),
            new Recipe(16, () -> new ItemStack(TheArgContainerModItems.MOONFALL_SKY_BLOCK_NIGHT.get())),
            new Recipe(16, () -> new ItemStack(TheArgContainerModItems.MOONFALL_SKY_BLOCK_DAY.get())),
            new Recipe(16, () -> new ItemStack(TheArgContainerModItems.DEBUG_SKYBOX.get())),
            new Recipe(16, () -> new ItemStack(TheArgContainerModItems.HUB_SKY.get()))
    );
}
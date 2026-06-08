package net.mcreator.minecraftalphaargmod.client;

import net.mcreator.minecraftalphaargmod.client.book.CustomBookRegistry;
import net.mcreator.minecraftalphaargmod.client.book.CustomBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientBookHooks {

    public static void openReadOnly(ItemStack stack, InteractionHand hand) {
        CustomBookRegistry.get(stack.getItem()).ifPresent(def ->
            Minecraft.getInstance().setScreen(new CustomBookScreen(stack, hand, CustomBookScreen.Mode.READ_ONLY, def))
        );
    }

    public static void openWritable(ItemStack stack, InteractionHand hand) {
        CustomBookRegistry.get(stack.getItem()).ifPresent(def ->
            Minecraft.getInstance().setScreen(new CustomBookScreen(stack, hand, CustomBookScreen.Mode.WRITABLE, def))
        );
    }
}
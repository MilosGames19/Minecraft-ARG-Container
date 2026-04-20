package net.mcreator.minecraftalphaargmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModItems;
import net.mcreator.minecraftalphaargmod.world.inventory.SkyBuilderGuiMenu;
import net.mcreator.minecraftalphaargmod.world.inventory.SkyBuilderRecipes;

import java.util.function.Supplier;

public class CraftSkyBuilderPacket {

    private final int recipeIndex;
    private final boolean shiftClick;

    public CraftSkyBuilderPacket(int recipeIndex, boolean shiftClick) {
        this.recipeIndex = recipeIndex;
        this.shiftClick  = shiftClick;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(recipeIndex);
        buf.writeBoolean(shiftClick);
    }

    public static CraftSkyBuilderPacket decode(FriendlyByteBuf buf) {
        return new CraftSkyBuilderPacket(buf.readVarInt(), buf.readBoolean());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            if (!(player.containerMenu instanceof SkyBuilderGuiMenu menu)) return;

            if (recipeIndex < 0 || recipeIndex >= SkyBuilderRecipes.RECIPES.size()) return;

            SkyBuilderRecipes.Recipe recipe = SkyBuilderRecipes.RECIPES.get(recipeIndex);

            Slot inputSlot  = menu.slots.get(0);
            Slot outputSlot = menu.slots.get(1);

            ItemStack inputStack = inputSlot.getItem();

            if (inputStack.getItem() != TheArgContainerModItems.SKY_SHARD.get()) return;
            if (inputStack.getCount() < recipe.inputCount()) return;

            if (shiftClick) {
                int craftCount = inputStack.getCount() / recipe.inputCount();
                inputStack.shrink(craftCount * recipe.inputCount());
                inputSlot.set(inputStack.isEmpty() ? ItemStack.EMPTY : inputStack);

                for (int i = 0; i < craftCount; i++) {
                    ItemStack result = recipe.getOutput();
                    if (!player.getInventory().add(result)) {
                        player.drop(result, false);
                    }
                }
            } else {
                if (!outputSlot.getItem().isEmpty()) return;

                inputStack.shrink(recipe.inputCount());
                inputSlot.set(inputStack.isEmpty() ? ItemStack.EMPTY : inputStack);
                outputSlot.setByPlayer(recipe.getOutput());
            }

            menu.broadcastChanges();
        });
        ctx.get().setPacketHandled(true);
    }
}
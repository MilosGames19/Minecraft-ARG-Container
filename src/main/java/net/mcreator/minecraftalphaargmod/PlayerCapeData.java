package net.mcreator.minecraftalphaargmod.capes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class PlayerCapeData {

    private static final String ROOT_TAG = "the_arg_container_capes";
    private static final String CAPE_TAG = "selected_cape";

    public static String getSelectedCape(Player player) {
        CompoundTag persisted = player.getPersistentData();
        CompoundTag root = persisted.getCompound(Player.PERSISTED_NBT_TAG);
        CompoundTag modTag = root.getCompound(ROOT_TAG);
        if (!modTag.contains(CAPE_TAG)) return null;
        return modTag.getString(CAPE_TAG);
    }

    public static void setSelectedCape(Player player, String capeId) {
        CompoundTag persisted = player.getPersistentData();
        CompoundTag root = persisted.getCompound(Player.PERSISTED_NBT_TAG);
        CompoundTag modTag = root.getCompound(ROOT_TAG);

        if (capeId == null) {
            modTag.remove(CAPE_TAG);
        } else {
            modTag.putString(CAPE_TAG, capeId);
        }

        root.put(ROOT_TAG, modTag);
        persisted.put(Player.PERSISTED_NBT_TAG, root);
    }
}
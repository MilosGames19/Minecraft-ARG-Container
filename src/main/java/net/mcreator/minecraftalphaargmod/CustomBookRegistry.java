package net.mcreator.minecraftalphaargmod.client.book;

import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class CustomBookRegistry {

    private static final Map<Item, BookDefinition> REGISTRY = new HashMap<>();

    private CustomBookRegistry() {}

    public static void register(Item item, BookDefinition def) {
        REGISTRY.put(item, def);
    }

    public static Optional<BookDefinition> get(Item item) {
        return Optional.ofNullable(REGISTRY.get(item));
    }

    public static boolean isRegistered(Item item) {
        return REGISTRY.containsKey(item);
    }
}
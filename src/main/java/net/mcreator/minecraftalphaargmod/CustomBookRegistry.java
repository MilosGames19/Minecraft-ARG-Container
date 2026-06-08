package net.mcreator.minecraftalphaargmod.client.book;

import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Central registry mapping Items → BookDefinitions.
 *
 * Usage example (call from your mod's client setup or FMLClientSetupEvent):
 * <pre>
 *   CustomBookRegistry.register(
 *       MyItems.MY_JOURNAL.get(),
 *       BookDefinition.builder(Component.literal("My Journal"))
 *           .texture(new ResourceLocation("the_arg_container", "textures/gui/book.png"))
 *           .writable()
 *           .maxPages(20)
 *           .build()
 *   );
 * </pre>
 */
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
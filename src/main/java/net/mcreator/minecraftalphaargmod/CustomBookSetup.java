package net.mcreator.minecraftalphaargmod.client.book;

import net.mcreator.minecraftalphaargmod.item.WritableDocumentItem;
import net.mcreator.minecraftalphaargmod.item.WrittenDocumentItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CustomBookSetup {

    private static final ResourceLocation TBOTV_TEXTURE =
            new ResourceLocation("the_arg_container", "textures/screens/tbotv_doc.png");

    private CustomBookSetup() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            register("writable_document",
                BookDefinition.builder(Component.translatable("item.the_arg_container.writable_document"))
                    .texture(TBOTV_TEXTURE).writable().maxPages(50).build());

            register("written_document",
                BookDefinition.builder(Component.translatable("item.the_arg_container.written_document"))
                    .texture(TBOTV_TEXTURE).maxPages(50).build());
        });
    }

    private static void register(String path, BookDefinition def) {
        Item item = ForgeRegistries.ITEMS.getValue(
                new ResourceLocation("the_arg_container", path));
        if (item != null) CustomBookRegistry.register(item, def);
    }
}
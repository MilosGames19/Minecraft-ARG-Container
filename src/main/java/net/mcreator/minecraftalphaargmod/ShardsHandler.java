package net.mcreator.minecraftalphaargmod;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import java.util.List;
import com.mojang.blaze3d.shaders.Program;
import net.minecraft.client.renderer.ShaderInstance;
import com.mojang.datafixers.util.Pair;
import com.google.common.collect.Lists;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.minecraftalphaargmod.TheArgContainerMod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TheArgContainerMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShardsHandler {

    private static ShaderInstance plane_image;
    @Nullable

    private static ShaderInstance fake_sky;
    @Nullable

    @SubscribeEvent
    public static void shaderRegistry(RegisterShadersEvent event) throws IOException
    {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation("the_arg_container","plane_image"), DefaultVertexFormat.POSITION), shaderInstance -> {
            plane_image = shaderInstance;
        });

        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation("the_arg_container","fake_sky"), DefaultVertexFormat.POSITION), shaderInstance -> {
            fake_sky = shaderInstance;
        });

    }



    @Nullable
    public static ShaderInstance GetPlaneImage() {
        return plane_image;
    }

    @Nullable
    public static ShaderInstance GetFakeSky() {
        return fake_sky;
    }
}
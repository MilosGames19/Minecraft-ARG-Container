package net.mcreator.minecraftalphaargmod.procedures;

import org.joml.Vector3f;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.client.DimensionSpecialEffectsManager;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;

import java.util.function.Consumer;
import java.util.Set;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RenderLightProcedure {
	private static float skyLevel = 0.0F;
	private static float blockLevel = 0.0F;
	private static Vector3f skyColor = null;
	private static Vector3f blockColor = null;
	private static final Consumer<Object[]> CONSUMER = params -> {
		int pixelX = (Integer) params[5];
		int pixelY = (Integer) params[6];
		if (pixelX == 0 && pixelY == 0) {
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.gameRenderer.getMainCamera().getEntity();
			if (entity != null) {
				ClientLevel level = minecraft.level;
				float partialTick = (Float) params[1];
				Vec3 pos = entity.getPosition(partialTick);
				execute(null, entity);
			}
		}
		calculateColor((Vector3f) params[7], pixelX, pixelY);
	};

	private static float calculateBaseLevel(float level) {
		return level * level * (level * -2.0F + 3.0F);
	}

	private static void calculateColor(Vector3f lightColor, int pixelX, int pixelY) {
		if (pixelX == pixelY)
			return;
		if (pixelX > pixelY) {
			if (blockColor == null)
				return;
			if (blockLevel == 0.0F)
				return;
			float level = Math.abs(calculateBaseLevel(pixelX / 15.0F) - calculateBaseLevel(pixelY / 15.0F)) * blockLevel;
			lightColor.set(Mth.clamp(Mth.lerp(level, lightColor.x(), blockColor.x()), 0.0F, 1.0F), Mth.clamp(Mth.lerp(level, lightColor.y(), blockColor.y()), 0.0F, 1.0F), Mth.clamp(Mth.lerp(level, lightColor.z(), blockColor.z()), 0.0F, 1.0F));
		} else {
			if (skyColor == null)
				return;
			if (skyLevel == 0.0F)
				return;
			float level = Math.abs(pixelX - pixelY) / 15.0F * skyLevel;
			lightColor.set(Mth.clamp(Mth.lerp(level, lightColor.x(), skyColor.x()), 0.0F, 1.0F), Mth.clamp(Mth.lerp(level, lightColor.y(), skyColor.y()), 0.0F, 1.0F), Mth.clamp(Mth.lerp(level, lightColor.z(), skyColor.z()), 0.0F, 1.0F));
		}
	}

	public static void setBlockColor(int blockColor) {
		setBlockColor(1.0F, blockColor);
	}

	public static void setBlockColor(float level, int blockColor) {
		RenderLightProcedure.blockLevel = Mth.clamp(level, 0.0F, 1.0F);
		RenderLightProcedure.blockColor = new Vector3f((blockColor >> 16 & 255) / 255.0F, (blockColor >> 8 & 255) / 255.0F, (blockColor & 255) / 255.0F);
	}

	public static void setSkyColor(int skyColor) {
		setSkyColor(1.0F, skyColor);
	}

	public static void setSkyColor(float level, int skyColor) {
		RenderLightProcedure.skyLevel = Mth.clamp(level, 0.0F, 1.0F);
		RenderLightProcedure.skyColor = new Vector3f((skyColor >> 16 & 255) / 255.0F, (skyColor >> 8 & 255) / 255.0F, (skyColor & 255) / 255.0F);
	}

	@SubscribeEvent
	public static void lightColorSetup(FMLClientSetupEvent event) {
		try {
			Field field = DimensionSpecialEffectsManager.class.getDeclaredField("EFFECTS");
			field.setAccessible(true);
			for (DimensionSpecialEffects dimensionSpecialEffects : ((com.google.common.collect.ImmutableMap<ResourceLocation, DimensionSpecialEffects>) field.get(null)).values()) {
				Class<?> effects = dimensionSpecialEffects.getClass();
				((Set<Consumer<Object[]>>) effects.getField("CUSTOM_LIGHTS").get(null)).add(CONSUMER);
			}
		} catch (Exception e) {
		}
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		if ((entity.level().dimension()) == ResourceKey.create(Registries.DIMENSION, new ResourceLocation("the_arg_container:moonfalldimension"))) {
			setBlockColor(255 << 24 | 255 << 16 | 255 << 8 | 255);
		}
	}
}

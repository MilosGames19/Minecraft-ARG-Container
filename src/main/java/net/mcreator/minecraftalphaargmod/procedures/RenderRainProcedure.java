package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.client.DimensionSpecialEffectsManager;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;

import java.util.function.Predicate;
import java.util.Set;

import java.lang.reflect.Field;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.systems.RenderSystem;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RenderRainProcedure {
	private static float partialTick = 0.0F;
	private static int ticks = 0;
	private static final Predicate<Object[]> PREDICATE = params -> {
		ticks = (Integer) params[1];
		partialTick = (Float) params[2];
		Minecraft minecraft = Minecraft.getInstance();
		Entity entity = minecraft.gameRenderer.getMainCamera().getEntity();
		if (entity != null) {
			ClientLevel level = minecraft.level;
			Vec3 pos = entity.getPosition(partialTick);
			execute(null, level, level.dimension());
			return true;
		}
		return false;
	};

	public static void renderRain(float speed, int x, int z, int range, int color, boolean constant) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		Vec3 pos = minecraft.gameRenderer.getMainCamera().getPosition();
		double camX = pos.x();
		double camY = pos.y();
		double camZ = pos.z();
		int iCamX = Mth.floor(camX);
		int iCamY = Mth.floor(camY);
		int iCamZ = Mth.floor(camZ);
		int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
		int length = Minecraft.useFancyGraphics() ? 10 : 5;
		int btm = iCamY - (range > length ? range : length);
		int top = iCamY + (range > length ? range : length);
		if (btm < height)
			btm = height;
		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;
		float alpha = (color >>> 24) / 255.0F;
		if (!constant)
			alpha *= level.getRainLevel(partialTick);
		if (top > height) {
			int idx = x - iCamX;
			int idz = z - iCamZ;
			double r = Mth.sqrt(idx * idx + idz * idz);
			double vecX = (-idz / r) * 0.5F;
			double vecZ = (idx / r) * 0.5F;
			RandomSource randomSource = RandomSource.create((long) (x * x * 3121 + x * 45238971 ^ z * z * 418711 + z * 13761));
			int counts = ticks + x * x * 3121 + x * 45238971 + z * z * 418711 + z * 13761 & 31;
			float anime = -((counts + partialTick) * speed) / 32.0F * (3.0F + randomSource.nextFloat());
			double dx = x + 0.5F - camX;
			double dz = z + 0.5F - camZ;
			if (range > 0) {
				float coef = (float) Math.sqrt(dx * dx + dz * dz) / range;
				alpha *= (1.0F - coef * coef) * 0.5F + 0.5F;
				alpha = alpha < 0 ? 0.0F : alpha;
			}
			int y = iCamY < height ? height : iCamY;
			int packedLight = LevelRenderer.getLightColor(level, new BlockPos(x, y, z));
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShader(GameRenderer::getParticleShader);
			BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
			bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
			bufferBuilder.vertex(x - camX - vecX + 0.5D, top - camY, z - camZ - vecZ + 0.5D).uv(0.0F, btm * 0.25F + anime).color(red, green, blue, alpha).uv2(packedLight).endVertex();
			bufferBuilder.vertex(x - camX - vecX + 0.5D, btm - camY, z - camZ - vecZ + 0.5D).uv(0.0F, top * 0.25F + anime).color(red, green, blue, alpha).uv2(packedLight).endVertex();
			bufferBuilder.vertex(x - camX + vecX + 0.5D, btm - camY, z - camZ + vecZ + 0.5D).uv(1.0F, top * 0.25F + anime).color(red, green, blue, alpha).uv2(packedLight).endVertex();
			bufferBuilder.vertex(x - camX + vecX + 0.5D, top - camY, z - camZ + vecZ + 0.5D).uv(1.0F, btm * 0.25F + anime).color(red, green, blue, alpha).uv2(packedLight).endVertex();
			BufferUploader.drawWithShader(bufferBuilder.end());
		}
	}

	public static void renderSnow(float speed, int x, int z, int range, int color, boolean constant) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		Vec3 pos = minecraft.gameRenderer.getMainCamera().getPosition();
		double camX = pos.x();
		double camY = pos.y();
		double camZ = pos.z();
		int iCamX = Mth.floor(camX);
		int iCamY = Mth.floor(camY);
		int iCamZ = Mth.floor(camZ);
		int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
		int length = Minecraft.useFancyGraphics() ? 10 : 5;
		int btm = iCamY - (range > length ? range : length);
		int top = iCamY + (range > length ? range : length);
		if (btm < height)
			btm = height;
		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;
		float alpha = (color >>> 24) / 255.0F;
		if (!constant)
			alpha *= level.getRainLevel(partialTick);
		if (top > height) {
			RandomSource randomSource = RandomSource.create((long) (x * x * 3121 + x * 45238971 ^ z * z * 418711 + z * 13761));
			int idx = x - iCamX;
			int idz = z - iCamZ;
			double r = Mth.sqrt(idx * idx + idz * idz);
			double vecX = (-idz / r) * 0.5F;
			double vecZ = (idx / r) * 0.5F;
			float anime = -((ticks & 511) + partialTick) / 512.0F;
			float time = ticks + partialTick;
			float du = (float) (randomSource.nextDouble() + randomSource.nextGaussian() * time * 0.01D) * speed;
			float dv = (float) (randomSource.nextDouble() + randomSource.nextGaussian() * time * 0.001D) * (speed / 10.0F);
			double dx = x + 0.5F - camX;
			double dz = z + 0.5F - camZ;
			if (range > 0) {
				float coef = (float) Math.sqrt(dx * dx + dz * dz) / range;
				alpha *= (1.0F - coef * coef) * 0.5F + 0.5F;
				alpha = alpha < 0 ? 0.0F : alpha;
			}
			int y = iCamY < height ? height : iCamY;
			int packedLight = LevelRenderer.getLightColor(level, new BlockPos(x, y, z));
			int sky = ((packedLight & '\uffff') * 3 + 240) / 4;
			int block = ((packedLight >> 16 & '\uffff') * 3 + 240) / 4;
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShader(GameRenderer::getParticleShader);
			BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
			bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
			bufferBuilder.vertex(x - camX - vecX + 0.5D, top - camY, z - camZ - vecZ + 0.5D).uv(0.0F + du, btm * 0.25F + anime + dv).color(red, green, blue, alpha).uv2(sky, block).endVertex();
			bufferBuilder.vertex(x - camX - vecX + 0.5D, btm - camY, z - camZ - vecZ + 0.5D).uv(0.0F + du, top * 0.25F + anime + dv).color(red, green, blue, alpha).uv2(sky, block).endVertex();
			bufferBuilder.vertex(x - camX + vecX + 0.5D, btm - camY, z - camZ + vecZ + 0.5D).uv(1.0F + du, top * 0.25F + anime + dv).color(red, green, blue, alpha).uv2(sky, block).endVertex();
			bufferBuilder.vertex(x - camX + vecX + 0.5D, top - camY, z - camZ + vecZ + 0.5D).uv(1.0F + du, btm * 0.25F + anime + dv).color(red, green, blue, alpha).uv2(sky, block).endVertex();
			BufferUploader.drawWithShader(bufferBuilder.end());
		}
	}

	public static void renderWeather(boolean rain, boolean snow, float speed, int range, int color, boolean constant) {
		if (range > 0) {
			Minecraft minecraft = Minecraft.getInstance();
			ClientLevel level = minecraft.level;
			Vec3 pos = minecraft.gameRenderer.getMainCamera().getPosition();
			int ix = Mth.floor(pos.x());
			int iy = Mth.floor(pos.y());
			int iz = Mth.floor(pos.z());
			for (int i = -range; i <= range; ++i) {
				for (int j = -range; j <= range; ++j) {
					BlockPos blockPos = new BlockPos(ix + i, iy, iz + j);
					Biome biome = level.getBiome(blockPos).value();
					if (biome.hasPrecipitation()) {
						Biome.Precipitation precipitation = biome.getPrecipitationAt(blockPos);
						if (rain && precipitation == Biome.Precipitation.RAIN) {
							RenderSystem.setShaderTexture(0, new ResourceLocation("minecraft:textures/environment/rain.png"));
							renderRain(speed, ix + i, iz + j, range, color, constant);
						}
						if (snow && precipitation == Biome.Precipitation.SNOW) {
							RenderSystem.setShaderTexture(0, new ResourceLocation("minecraft:textures/environment/snow.png"));
							renderSnow(speed, ix + i, iz + j, range, color, constant);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void weatherSetup(FMLClientSetupEvent event) {
		try {
			Field field = DimensionSpecialEffectsManager.class.getDeclaredField("EFFECTS");
			field.setAccessible(true);
			for (DimensionSpecialEffects dimensionSpecialEffects : ((com.google.common.collect.ImmutableMap<ResourceLocation, DimensionSpecialEffects>) field.get(null)).values()) {
				Class<?> effects = dimensionSpecialEffects.getClass();
				((Set<Predicate<Object[]>>) effects.getField("CUSTOM_WEATHER").get(null)).add(PREDICATE);
			}
		} catch (Exception e) {
		}
	}

	public static void execute(LevelAccessor world, ResourceKey<Level> dimension) {
		execute(null, world, dimension);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, ResourceKey<Level> dimension) {
		if (dimension == null)
			return;
		if (dimension == Level.OVERWORLD) {
			if (world instanceof ClientLevel) {
				BlockPos _renderCenter = Minecraft.getInstance().gameRenderer.getMainCamera().getBlockPosition();
				int _renderLength = Minecraft.useFancyGraphics() ? 10 : 5;
				int _renderRange = Minecraft.useFancyGraphics() ? 10 : 5;
				int positionx, positiony, positionz;
				_renderLength = _renderRange > _renderLength ? _renderRange : _renderLength;
				for (int _renderZ = -_renderRange; _renderZ <= _renderRange; ++_renderZ) {
					for (int _renderX = -_renderRange; _renderX <= _renderRange; ++_renderX) {
						positionx = _renderCenter.getX() + _renderX;
						positionz = _renderCenter.getZ() + _renderZ;
						positiony = world.getHeight(Heightmap.Types.MOTION_BLOCKING, positionx, positionz);
						if (positiony <= _renderCenter.getY() + _renderLength) {
							positiony = positiony < _renderCenter.getY() - _renderLength ? _renderCenter.getY() - _renderLength : positiony;
							if ((new Object() {
								public boolean is(LevelAccessor levelAccessor, BlockPos blockPos, Biome.Precipitation precipitation) {
									return world.getBiome(blockPos).value().getPrecipitationAt(blockPos) == precipitation;
								}
							}).is(world, new BlockPos(positionx, positiony, positionz), Biome.Precipitation.RAIN)) {
								RenderSystem.setShaderTexture(0, new ResourceLocation(("minecraft" + ":textures/" + "environment/rain" + ".png")));
								renderRain(1, positionx, positionz, Minecraft.useFancyGraphics() ? 10 : 5, 255 << 24 | 255 << 16 | 255 << 8 | 255, false);
							} else if ((new Object() {
								public boolean is(LevelAccessor levelAccessor, BlockPos blockPos, Biome.Precipitation precipitation) {
									return world.getBiome(blockPos).value().getPrecipitationAt(blockPos) == precipitation;
								}
							}).is(world, new BlockPos(positionx, positiony, positionz), Biome.Precipitation.SNOW)) {
								RenderSystem.setShaderTexture(0, new ResourceLocation(("minecraft" + ":textures/" + "environment/snow" + ".png")));
								renderSnow(1, positionx, positionz, Minecraft.useFancyGraphics() ? 10 : 5, 255 << 24 | 255 << 16 | 255 << 8 | 255, false);
							}
						}
					}
				}
			}
		}
	}
}

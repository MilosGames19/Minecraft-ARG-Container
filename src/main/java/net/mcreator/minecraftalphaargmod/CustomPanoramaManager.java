package net.mcreator.minecraftalphaargmod.client.panorama;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.resources.ResourceLocation;
import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public final class CustomPanoramaManager {

	private static final Logger LOGGER = LogManager.getLogger("The ARG Container");
	private static final String NAMESPACE = "the_arg_container";
	private static final String TEX_PREFIX = "textures/panoramas/";

	private static final List<ResourceLocation> PANORAMA_BASES = new ArrayList<>();
	private static boolean discovered = false;
	private static int currentIndex = -1;
	private static final Random RNG = new Random();

	private static Unsafe UNSAFE = null;
	private static Field panoramaRendField = null;
	private static long cubeMapOffset = -1;
	private static boolean reflectionReady = false;

	private static TitleScreen lastScreen = null;

	private CustomPanoramaManager() {
	}

	public static void onTitleScreenOpen(TitleScreen screen) {
		lastScreen = screen;
		if (!isEnabled())
			return;
		if (!discovered)
			discoverPanoramas();
		if (PANORAMA_BASES.isEmpty())
			return;
		if (currentIndex < 0)
			currentIndex = RNG.nextInt(PANORAMA_BASES.size());
		applyPanorama(screen, currentIndex);
	}

	public static void cycle(TitleScreen screen, int direction) {
		lastScreen = screen;
		if (!isEnabled())
			return;
		if (!discovered)
			discoverPanoramas();
		if (PANORAMA_BASES.isEmpty())
			return;
		currentIndex = Math.floorMod((currentIndex < 0 ? 0 : currentIndex) + direction, PANORAMA_BASES.size());
		applyPanorama(screen, currentIndex);
		LOGGER.info("[CustomPanoramaManager] Cycled to panorama #{} ({})", currentIndex + 1, PANORAMA_BASES.get(currentIndex));
	}

	private static boolean isEnabled() {
		try {
			return McconfigConfiguration.CUSTOM_PANORAMA.get();
		} catch (Exception e) {
			return false;
		}
	}

	private static void discoverPanoramas() {
		discovered = true;
		PANORAMA_BASES.clear();
		Minecraft mc = Minecraft.getInstance();
		for (int i = 1;; i++) {
			ResourceLocation probe = new ResourceLocation(NAMESPACE, TEX_PREFIX + "panorama_" + i + "/panorama_0.png");
			if (mc.getResourceManager().getResource(probe).isEmpty())
				break;
			ResourceLocation base = new ResourceLocation(NAMESPACE, TEX_PREFIX + "panorama_" + i + "/panorama");
			PANORAMA_BASES.add(base);
			LOGGER.info("[CustomPanoramaManager] Found panorama folder: panorama_{}", i);
		}
		LOGGER.info("[CustomPanoramaManager] Total panoramas discovered: {}", PANORAMA_BASES.size());
	}

	private static void applyPanorama(TitleScreen screen, int index) {
		if (index < 0 || index >= PANORAMA_BASES.size())
			return;
		try {
			ensureReflectionReady();
			if (!reflectionReady)
				return;

			PanoramaRenderer renderer = (PanoramaRenderer) panoramaRendField.get(screen);
			if (renderer == null)
				return;

			CubeMap newCubeMap = new CubeMap(PANORAMA_BASES.get(index));
			UNSAFE.putObject(renderer, cubeMapOffset, newCubeMap);

		} catch (Exception e) {
			LOGGER.warn("[CustomPanoramaManager] Failed to swap panorama: {}", e.getMessage());
		}
	}

	private static void ensureReflectionReady() {
		if (reflectionReady)
			return;
		try {
			Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			unsafeField.setAccessible(true);
			UNSAFE = (Unsafe) unsafeField.get(null);

			for (Field f : TitleScreen.class.getDeclaredFields()) {
				if (f.getType() == PanoramaRenderer.class) {
					panoramaRendField = f;
					panoramaRendField.setAccessible(true);
					break;
				}
			}

			for (Field f : PanoramaRenderer.class.getDeclaredFields()) {
				if (f.getType() == CubeMap.class) {
					cubeMapOffset = UNSAFE.objectFieldOffset(f);
					break;
				}
			}

			if (panoramaRendField != null && cubeMapOffset != -1) {
				reflectionReady = true;
				LOGGER.info("[CustomPanoramaManager] Reflection handles acquired.");
			} else {
				LOGGER.error("[CustomPanoramaManager] Could not locate required fields by type.");
			}
		} catch (Exception e) {
			LOGGER.error("[CustomPanoramaManager] Reflection setup failed: {}", e.getMessage());
		}
	}

	public static int getPanoramaCount() {
		return PANORAMA_BASES.size();
	}

	public static int getCurrentDisplay() {
		return currentIndex + 1;
	}
}
package net.mcreator.minecraftalphaargmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.client.Camera;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.mojang.blaze3d.systems.RenderSystem;

import org.lwjgl.glfw.GLFW;
import java.util.Random;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class DirectorPreventionHandler {

    private static boolean directorVisible = false;
    private static long glitchEndTime = 0;
    private static long lastF1AttemptTime = 0;
    private static long lastDamageTime = 0;
    private static final long F1_COOLDOWN = 1000;
    private static final long GLITCH_DURATION = 5000;
    private static final long DAMAGE_COOLDOWN = 1000;
    private static final float DAMAGE_AMOUNT = 5.0f;
    private static final Random random = new Random();
    private static boolean wasF1Pressed = false;
    private static boolean wasGuiHidden = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            directorVisible = false;
            return;
        }

        // Check if the_director is visible
        directorVisible = isDirectorVisible(mc);

        // Check for F1 key press
        long window = mc.getWindow().getWindow();
        boolean isF1Pressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_F1) == GLFW.GLFW_PRESS;
        
        // Detect F1 press (transition from not pressed to pressed)
        if (isF1Pressed && !wasF1Pressed && directorVisible) {
            long currentTime = System.currentTimeMillis();
            
            if (currentTime - lastF1AttemptTime > F1_COOLDOWN) {
                lastF1AttemptTime = currentTime;
                
                // Show action bar message
                mc.player.displayClientMessage(
                    Component.literal("§4YOU DON'T WANT TO DO THAT"),
                    true
                );
                
                // Trigger glitch effect
                glitchEndTime = currentTime + GLITCH_DURATION;
                
                // Damage player if in survival
                if (!mc.player.isCreative() && !mc.player.isSpectator()) {
                    if (currentTime - lastDamageTime > DAMAGE_COOLDOWN) {
                        mc.player.hurt(mc.player.damageSources().magic(), DAMAGE_AMOUNT);
                        lastDamageTime = currentTime;
                    }
                }
            }
        }
        
        wasF1Pressed = isF1Pressed;
        
        // Force GUI to stay visible if director is visible and F1 was just pressed
        if (directorVisible) {
            boolean isGuiHidden = mc.options.hideGui;
            
            // If GUI just got hidden (transition), force it back on
            if (isGuiHidden && !wasGuiHidden) {
                mc.options.hideGui = false;
            }
            
            // Always keep GUI visible when director is visible
            if (isGuiHidden) {
                mc.options.hideGui = false;
            }
            
            wasGuiHidden = false;
        } else {
            wasGuiHidden = mc.options.hideGui;
        }
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        long currentTime = System.currentTimeMillis();
        if (currentTime < glitchEndTime) {
            renderGlitchEffect(event.getGuiGraphics(), event.getWindow().getGuiScaledWidth(), 
                             event.getWindow().getGuiScaledHeight(), currentTime);
        }
    }

    private static void renderGlitchEffect(GuiGraphics guiGraphics, int screenWidth, int screenHeight, long currentTime) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        
        float intensity = Math.min(1.0f, (glitchEndTime - currentTime) / 1000.0f);
        
        // RGB split effect
        if (random.nextFloat() < 0.3f * intensity) {
            renderColorSplit(guiGraphics, screenWidth, screenHeight, intensity);
        }
        
        // Horizontal glitch bars
        if (random.nextFloat() < 0.5f * intensity) {
            renderGlitchBars(guiGraphics, screenWidth, screenHeight, intensity);
        }
        
        // Screen shake effect (visual distortion)
        if (random.nextFloat() < 0.4f * intensity) {
            renderStaticNoise(guiGraphics, screenWidth, screenHeight, intensity);
        }
        
        // Scan lines
        if (random.nextFloat() < 0.2f * intensity) {
            renderScanLines(guiGraphics, screenWidth, screenHeight, intensity);
        }
        
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void renderColorSplit(GuiGraphics guiGraphics, int screenWidth, int screenHeight, float intensity) {
        int offset = (int) (10 * intensity);
        
        // Red channel shift
        fillWithColor(guiGraphics, 0, 0, screenWidth, screenHeight, 
                     (int)(255 * intensity * 0.15f), 0, 0, (int)(50 * intensity));
        
        // Cyan channel shift
        fillWithColor(guiGraphics, offset, 0, screenWidth + offset, screenHeight, 
                     0, (int)(255 * intensity * 0.15f), (int)(255 * intensity * 0.15f), (int)(50 * intensity));
    }

    private static void renderGlitchBars(GuiGraphics guiGraphics, int screenWidth, int screenHeight, float intensity) {
        int numBars = random.nextInt(3) + 2;
        
        for (int i = 0; i < numBars; i++) {
            int y = random.nextInt(screenHeight);
            int height = random.nextInt(50) + 10;
            int offset = random.nextInt(20) - 10;
            
            fillWithColor(guiGraphics, offset, y, screenWidth + offset, y + height, 
                         255, 255, 255, (int)(100 * intensity));
        }
    }

    private static void renderStaticNoise(GuiGraphics guiGraphics, int screenWidth, int screenHeight, float intensity) {
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(screenWidth);
            int y = random.nextInt(screenHeight);
            int size = random.nextInt(5) + 1;
            int brightness = random.nextInt(256);
            
            fillWithColor(guiGraphics, x, y, x + size, y + size, 
                         brightness, brightness, brightness, (int)(150 * intensity));
        }
    }

    private static void renderScanLines(GuiGraphics guiGraphics, int screenWidth, int screenHeight, float intensity) {
        for (int y = 0; y < screenHeight; y += 4) {
            fillWithColor(guiGraphics, 0, y, screenWidth, y + 1, 
                         0, 0, 0, (int)(20 * intensity));
        }
    }

    private static void fillWithColor(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, 
                                     int r, int g, int b, int a) {
        guiGraphics.fill(x1, y1, x2, y2, (a << 24) | (r << 16) | (g << 8) | b);
    }

    private static boolean isDirectorVisible(Minecraft mc) {
        if (mc.level == null || mc.player == null || mc.gameRenderer == null) {
            return false;
        }

        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity.getType().toString().contains("the_director")) {
                Vec3 entityPos = entity.position();
                double distance = cameraPos.distanceTo(entityPos);
                
                // Check if entity is within reasonable distance and in view
                if (distance < 64.0 && isInView(entity, camera)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    private static boolean isInView(Entity entity, Camera camera) {
        Vec3 cameraPos = camera.getPosition();
        Vec3 entityPos = entity.position();
        Vec3 toEntity = entityPos.subtract(cameraPos).normalize();
        Vec3 cameraForward = new Vec3(camera.getLookVector());
        
        double dotProduct = toEntity.dot(cameraForward);
        double angle = Math.toDegrees(Math.acos(dotProduct));
        
        return angle <= 90.0; // Entity is in front of camera
    }

    public static boolean isDirectorCurrentlyVisible() {
        return directorVisible;
    }

    public static boolean isGlitching() {
        return System.currentTimeMillis() < glitchEndTime;
    }
}
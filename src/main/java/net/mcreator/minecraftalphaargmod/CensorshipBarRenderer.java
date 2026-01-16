package net.mcreator.minecraftalphaargmod;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.*;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CensorshipBarRenderer {

    private static final double BOX_EXPANSION = 0.5; 
    private static final int BAR_COLOR = 0x000000;
    private static final float BAR_OPACITY = 1.0f;
    private static final int FAKE_FPS = 30;
    private static final int TRACKING_UPDATE_INTERVAL = Math.max(1, Math.round(20.0f / FAKE_FPS));
    private static final int JITTER_INTENSITY = 1; 
    private static final double MAX_FOV_ANGLE = 120.0;

    private static final Random JITTER_RNG = new Random();
    private static final Map<UUID, EntityTrackingData> trackingData = new HashMap<>();
    private static final Set<UUID> targetEntities = new HashSet<>();

    public static void addTargetEntity(UUID entityUUID) {
        targetEntities.add(entityUUID);
        trackingData.putIfAbsent(entityUUID, new EntityTrackingData());
    }

    public static void addTargetEntity(Entity entity) {
        if (entity != null) addTargetEntity(entity.getUUID());
    }

    public static void removeTargetEntity(UUID entityUUID) {
        targetEntities.remove(entityUUID);
        trackingData.remove(entityUUID);
    }

    public static void clearTargets() {
        targetEntities.clear();
        trackingData.clear();
    }

    public static int getTargetCount() {
        return targetEntities.size();
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType().toString().contains("the_director")) {
            addTargetEntity(entity.getUUID());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        Camera camera = mc.gameRenderer.getMainCamera();
        long gameTime = mc.level.getGameTime();
        boolean isFirstPerson = mc.options.getCameraType() == CameraType.FIRST_PERSON;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        
        for (UUID entityUUID : new ArrayList<>(targetEntities)) {
            Entity entity = findEntityByUUID(mc, entityUUID);
            
            if (entity == null || !entity.isAlive()) {
                removeTargetEntity(entityUUID);
                continue;
            }

            if (entity == mc.player && isFirstPerson) {
                continue;
            }

            EntityTrackingData data = trackingData.computeIfAbsent(entityUUID, k -> new EntityTrackingData());

            if (!isEntityInFOV(entity, camera, event.getPartialTick())) {
                data.isVisible = false;
                data.lastBounds = null;
                continue;
            }

            if (!isEntityVisible(entity, camera, mc)) {
                data.isVisible = false;
                data.lastBounds = null;
                continue;
            }
            data.isVisible = true;

            boolean shouldUpdate = (gameTime % TRACKING_UPDATE_INTERVAL == 0) && (gameTime != data.lastUpdateTime);
            
            if (shouldUpdate || data.lastBounds == null) {
                ScreenRect bounds = calculateScreenBounds(entity, camera, mc, event.getPartialTick());
                if (bounds != null) {
                    data.lastBounds = bounds;
                    data.lastUpdateTime = gameTime;
                } else {
                    data.lastBounds = null;
                }
            }

            if (data.isVisible && data.lastBounds != null) {
                renderCensorBox(event.getGuiGraphics().pose().last().pose(), data.lastBounds);
            }
        }
        
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static boolean isEntityInFOV(Entity entity, Camera camera, float partialTick) {
        Vec3 cameraPos = camera.getPosition();
        Vec3 entityPos = entity.getPosition(partialTick);
        Vec3 toEntity = entityPos.subtract(cameraPos).normalize();
        Vec3 cameraForward = new Vec3(camera.getLookVector());
        
        double dotProduct = toEntity.dot(cameraForward);
        double angle = Math.toDegrees(Math.acos(dotProduct));
        
        return angle <= MAX_FOV_ANGLE;
    }

    private static ScreenRect calculateScreenBounds(Entity entity, Camera camera, Minecraft mc, float partialTick) {
        Vec3 entityPos = entity.getPosition(partialTick);
        double w = entity.getBbWidth() / 2.0;
        double h = entity.getBbHeight();

        double expandW = w * (1.0 + BOX_EXPANSION);
        double expandH = h * (1.0 + BOX_EXPANSION);
        double yOffset = (h * BOX_EXPANSION) / 2.0;

        Vec3[] corners = new Vec3[] {
            entityPos.add(-expandW, -yOffset, -expandW),
            entityPos.add(expandW, -yOffset, -expandW),
            entityPos.add(expandW, -yOffset, expandW),
            entityPos.add(-expandW, -yOffset, expandW),
            entityPos.add(-expandW, h + yOffset, -expandW),
            entityPos.add(expandW, h + yOffset, -expandW),
            entityPos.add(expandW, h + yOffset, expandW),
            entityPos.add(-expandW, h + yOffset, expandW)
        };

        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        for (Vec3 corner : corners) {
            ScreenPosition pos = projectToScreen(corner, camera, mc, partialTick);
            minX = Math.min(minX, pos.x);
            maxX = Math.max(maxX, pos.x);
            minY = Math.min(minY, pos.y);
            maxY = Math.max(maxY, pos.y);
        }

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        
        if (minX >= maxX || minY >= maxY) return null;
        if (maxX < -screenWidth || minX > screenWidth * 2) return null;
        if (maxY < -screenHeight || minY > screenHeight * 2) return null;
        
        return new ScreenRect(minX, minY, maxX - minX, maxY - minY);
    }

    private static ScreenPosition projectToScreen(Vec3 worldPos, Camera camera, Minecraft mc, float partialTick) {
        double baseFov = mc.options.fov().get();
        double fovMod = mc.player.getFieldOfViewModifier(); 
        double fov = baseFov * fovMod;
        float aspectRatio = (float) mc.getWindow().getWidth() / (float) mc.getWindow().getHeight();

        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.setPerspective((float) Math.toRadians(fov), aspectRatio, 0.05f, mc.options.renderDistance().get() * 16.0f);

        Matrix4f viewMatrix = new Matrix4f();
        Vec3 cameraPos = camera.getPosition();
        Vector3f eye = new Vector3f((float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);
        Vector3f forward = camera.getLookVector();
        Vector3f up = camera.getUpVector();
        viewMatrix.lookAt(eye, new Vector3f(eye).add(forward), up);

        Vector4f pos = new Vector4f((float) worldPos.x, (float) worldPos.y, (float) worldPos.z, 1.0f);
        pos.mul(viewMatrix);
        pos.mul(projectionMatrix);

        if (pos.w <= 0) {
            pos.w = 0.01f;
        }

        pos.x /= pos.w;
        pos.y /= pos.w;

        pos.x = Math.max(-2.0f, Math.min(2.0f, pos.x));
        pos.y = Math.max(-2.0f, Math.min(2.0f, pos.y));

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int screenX = (int) ((pos.x * 0.5f + 0.5f) * screenWidth);
        int screenY = (int) ((1.0f - (pos.y * 0.5f + 0.5f)) * screenHeight);

        return new ScreenPosition(screenX, screenY, true);
    }

    private static void renderCensorBox(Matrix4f matrix, ScreenRect rect) {
        int jitterX = JITTER_RNG.nextInt(JITTER_INTENSITY * 2 + 1) - JITTER_INTENSITY;
        int jitterY = JITTER_RNG.nextInt(JITTER_INTENSITY * 2 + 1) - JITTER_INTENSITY;

        int x1 = rect.x + jitterX;
        int y1 = rect.y + jitterY;
        int x2 = x1 + rect.width;
        int y2 = y1 + rect.height;

        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        
        float r = ((BAR_COLOR >> 16) & 0xFF) / 255.0f;
        float g = ((BAR_COLOR >> 8) & 0xFF) / 255.0f;
        float b = (BAR_COLOR & 0xFF) / 255.0f;

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x1, y2, 1000).color(r, g, b, BAR_OPACITY).endVertex();
        bufferBuilder.vertex(matrix, x2, y2, 1000).color(r, g, b, BAR_OPACITY).endVertex();
        bufferBuilder.vertex(matrix, x2, y1, 1000).color(r, g, b, BAR_OPACITY).endVertex();
        bufferBuilder.vertex(matrix, x1, y1, 1000).color(r, g, b, BAR_OPACITY).endVertex();

        BufferUploader.drawWithShader(bufferBuilder.end());
    }

    private static boolean isEntityVisible(Entity entity, Camera camera, Minecraft mc) {
        Vec3 cameraPos = camera.getPosition();
        Vec3 entityPos = entity.position();
        double height = entity.getBbHeight();
        return hasLineOfSight(cameraPos, entityPos, mc) || 
               hasLineOfSight(cameraPos, entityPos.add(0, height * 0.5, 0), mc) || 
               hasLineOfSight(cameraPos, entityPos.add(0, height, 0), mc);
    }

    private static boolean hasLineOfSight(Vec3 from, Vec3 to, Minecraft mc) {
        ClipContext ctx = new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mc.player);
        return mc.level.clip(ctx).getType() == HitResult.Type.MISS;
    }

    private static Entity findEntityByUUID(Minecraft mc, UUID uuid) {
        if (mc.level == null) return null;
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity.getUUID().equals(uuid)) return entity;
        }
        return null;
    }

    private static class EntityTrackingData {
        ScreenRect lastBounds = null;
        long lastUpdateTime = 0;
        boolean isVisible = false;
    }

    private static class ScreenRect {
        int x, y, width, height;
        ScreenRect(int x, int y, int width, int height) {
            this.x = x; this.y = y; this.width = width; this.height = height;
        }
    }

    private static class ScreenPosition {
        int x, y;
        boolean onScreen;
        ScreenPosition(int x, int y, boolean onScreen) {
            this.x = x; this.y = y; this.onScreen = onScreen;
        }
    }
}
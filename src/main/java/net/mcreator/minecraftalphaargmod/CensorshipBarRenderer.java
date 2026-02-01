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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
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
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CensorshipBarRenderer {

    private static final double BOX_EXPANSION = 1.0; // Increased from 0.5 for better coverage
    private static final int BAR_COLOR = 0x000000;
    private static final float BAR_OPACITY = 1.0f;
    private static final int FAKE_FPS = 60; // Increased FPS for smoother tracking
    private static final int TRACKING_UPDATE_INTERVAL = Math.max(1, Math.round(20.0f / FAKE_FPS));
    private static final int JITTER_INTENSITY = 1; 
    private static final double MAX_FOV_ANGLE = 120.0;

    private static final Random JITTER_RNG = new Random();
    private static final Map<UUID, EntityTrackingData> trackingData = new ConcurrentHashMap<>();
    private static final Set<UUID> targetEntities = Collections.synchronizedSet(new HashSet<>());
    private static final Map<UUID, Entity> entityCache = new ConcurrentHashMap<>();

    // Reusable JOML objects to reduce garbage collection
    private static final Matrix4f viewProjectionMatrix = new Matrix4f();
    private static final Matrix4f projectionMatrix = new Matrix4f();
    private static final Matrix4f viewMatrix = new Matrix4f();
    private static final Vector4f tempPos = new Vector4f();
    private static final Vector3f tempEye = new Vector3f();
    private static final Vector3f tempForward = new Vector3f();
    private static final Vector3f tempUp = new Vector3f();
    
    // Camera position for relative rendering to avoid precision loss
    private static double camX, camY, camZ;

    public static void addTargetEntity(UUID entityUUID) {
        targetEntities.add(entityUUID);
        trackingData.putIfAbsent(entityUUID, new EntityTrackingData());
    }

    public static void addTargetEntity(Entity entity) {
        if (entity != null) {
            addTargetEntity(entity.getUUID());
            entityCache.put(entity.getUUID(), entity);
        }
    }

    public static void removeTargetEntity(UUID entityUUID) {
        targetEntities.remove(entityUUID);
        trackingData.remove(entityUUID);
        entityCache.remove(entityUUID);
    }

    public static void clearTargets() {
        targetEntities.clear();
        trackingData.clear();
        entityCache.clear();
    }

    public static int getTargetCount() {
        return targetEntities.size();
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType().toString().contains("the_director")) {
            addTargetEntity(entity);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        Camera camera = mc.gameRenderer.getMainCamera();
        long gameTime = mc.level.getGameTime();
        boolean isFirstPerson = mc.options.getCameraType() == CameraType.FIRST_PERSON;

        // Pre-calculate View-Projection Matrix once per frame
        updateViewProjectionMatrix(mc, camera, event.getPartialTick());

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        
        // Use a copy of the key set to avoid ConcurrentModificationException
        Set<UUID> currentTargets = new HashSet<>(targetEntities);
        Iterator<UUID> iterator = currentTargets.iterator();
        
        while (iterator.hasNext()) {
            UUID entityUUID = iterator.next();
            Entity entity = getEntity(mc, entityUUID);
            
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

            if (!isEntityVisible(entity, camera, mc, event.getPartialTick())) {
                data.isVisible = false;
                data.lastBounds = null;
                continue;
            }
            data.isVisible = true;

            // Always update tracking every frame to prevent lag behind fast camera movements
            ScreenRect bounds = calculateScreenBounds(entity, camera, mc, event.getPartialTick());
            if (bounds != null) {
                data.lastBounds = bounds;
                data.lastUpdateTime = gameTime;
            } else {
                data.lastBounds = null;
            }

            if (data.isVisible && data.lastBounds != null) {
                renderCensorBox(event.getGuiGraphics().pose().last().pose(), data.lastBounds, camera, entity, event.getPartialTick());
            }
        }
        
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static Entity getEntity(Minecraft mc, UUID uuid) {
        Entity cached = entityCache.get(uuid);
        if (cached != null && cached.isAlive()) {
            return cached;
        }
        Entity found = findEntityByUUID(mc, uuid);
        if (found != null) {
            entityCache.put(uuid, found);
        } else {
            entityCache.remove(uuid);
        }
        return found;
    }

    private static void updateViewProjectionMatrix(Minecraft mc, Camera camera, float partialTick) {
        double baseFov = mc.options.fov().get();
        double fovMod = mc.player.getFieldOfViewModifier(); 
        double fov = baseFov * fovMod;
        float aspectRatio = (float) mc.getWindow().getWidth() / (float) mc.getWindow().getHeight();

        projectionMatrix.identity();
        projectionMatrix.setPerspective((float) Math.toRadians(fov), aspectRatio, 0.05f, mc.options.renderDistance().get() * 16.0f);

        viewMatrix.identity();
        Vec3 cameraPos = camera.getPosition();
        camX = cameraPos.x;
        camY = cameraPos.y;
        camZ = cameraPos.z;

        // Render relative to camera (0,0,0) to avoid float precision issues at large coordinates
        tempEye.set(0, 0, 0);
        tempForward.set(camera.getLookVector());
        tempUp.set(camera.getUpVector());
        
        Vector3f target = new Vector3f(tempForward);
        viewMatrix.lookAt(tempEye, target, tempUp);

        viewProjectionMatrix.set(projectionMatrix).mul(viewMatrix);
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

        double[] xOffsets = {-expandW, expandW};
        double[] yOffsets = {-yOffset, h + yOffset};
        double[] zOffsets = {-expandW, expandW};

        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        for (double xo : xOffsets) {
            for (double yo : yOffsets) {
                for (double zo : zOffsets) {
                    double worldX = entityPos.x + xo;
                    double worldY = entityPos.y + yo;
                    double worldZ = entityPos.z + zo;
                    
                    ScreenPosition pos = projectToScreen(worldX, worldY, worldZ, mc);
                    minX = Math.min(minX, pos.x);
                    maxX = Math.max(maxX, pos.x);
                    minY = Math.min(minY, pos.y);
                    maxY = Math.max(maxY, pos.y);
                }
            }
        }

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        
        if (minX >= maxX || minY >= maxY) return null;
        if (maxX < -screenWidth || minX > screenWidth * 2) return null;
        if (maxY < -screenHeight || minY > screenHeight * 2) return null;
        
        return new ScreenRect(minX, minY, maxX - minX, maxY - minY);
    }

    private static ScreenPosition projectToScreen(double x, double y, double z, Minecraft mc) {
        // Transform to camera-relative coordinates
        float relX = (float) (x - camX);
        float relY = (float) (y - camY);
        float relZ = (float) (z - camZ);

        tempPos.set(relX, relY, relZ, 1.0f);
        tempPos.mul(viewProjectionMatrix);

        if (tempPos.w <= 0) {
            tempPos.w = 0.01f;
        }

        float ndcX = tempPos.x / tempPos.w;
        float ndcY = tempPos.y / tempPos.w;

        ndcX = Math.max(-2.0f, Math.min(2.0f, ndcX));
        ndcY = Math.max(-2.0f, Math.min(2.0f, ndcY));

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int screenX = (int) ((ndcX * 0.5f + 0.5f) * screenWidth);
        int screenY = (int) ((1.0f - (ndcY * 0.5f + 0.5f)) * screenHeight);

        return new ScreenPosition(screenX, screenY, true);
    }

    private static void renderCensorBox(Matrix4f matrix, ScreenRect rect, Camera camera, Entity entity, float partialTick) {
        // Calculate distance to entity
        Vec3 cameraPos = camera.getPosition();
        Vec3 entityPos = entity.getPosition(partialTick);
        double distance = cameraPos.distanceTo(entityPos);

        // Scale jitter based on distance (closer = more jitter, farther = less jitter)
        // Clamp distance to avoid division by zero or extreme values
        double clampedDist = Math.max(1.0, Math.min(distance, 50.0));
        int dynamicJitter = (int) Math.max(0, JITTER_INTENSITY * (10.0 / clampedDist));

        int jitterX = 0;
        int jitterY = 0;
        
        if (dynamicJitter > 0) {
            jitterX = JITTER_RNG.nextInt(dynamicJitter * 2 + 1) - dynamicJitter;
            jitterY = JITTER_RNG.nextInt(dynamicJitter * 2 + 1) - dynamicJitter;
        }

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

    private static boolean isEntityVisible(Entity entity, Camera camera, Minecraft mc, float partialTick) {
        Vec3 cameraPos = camera.getPosition();
        Vec3 entityPos = entity.getPosition(partialTick);
        double height = entity.getBbHeight();
        double width = entity.getBbWidth();
        
        if (hasLineOfSight(cameraPos, entityPos.add(0, height * 0.5, 0), mc)) return true;
        if (hasLineOfSight(cameraPos, entityPos, mc)) return true;
        if (hasLineOfSight(cameraPos, entityPos.add(0, height, 0), mc)) return true;

        // Check sides to prevent "peeking" around corners
        Vec3 viewDir = entityPos.subtract(cameraPos).normalize();
        Vec3 sideDir = new Vec3(-viewDir.z, 0, viewDir.x).normalize().scale(width * 0.5);

        if (hasLineOfSight(cameraPos, entityPos.add(0, height * 0.5, 0).add(sideDir), mc)) return true;
        if (hasLineOfSight(cameraPos, entityPos.add(0, height * 0.5, 0).subtract(sideDir), mc)) return true;

        return false;
    }

    private static boolean hasLineOfSight(Vec3 from, Vec3 to, Minecraft mc) {
        Vec3 currentFrom = from;
        Vec3 direction = to.subtract(from);
        double totalDistSqr = direction.lengthSqr();
        
        if (totalDistSqr < 1.0e-6) return true;
        
        direction = direction.normalize();
        
        int steps = 0;
        while (steps < 20) {
            ClipContext ctx = new ClipContext(currentFrom, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mc.player);
            BlockHitResult result = mc.level.clip(ctx);
            
            if (result.getType() == HitResult.Type.MISS) {
                return true;
            }
            
            BlockPos pos = result.getBlockPos();
            BlockState state = mc.level.getBlockState(pos);
            
            // Check if block is see-through (translucent, cutout, etc.)
            // We consider a block transparent if it doesn't block view, propagates light, or has low opacity.
            boolean isTransparent = !state.canOcclude() || 
                                    !state.isViewBlocking(mc.level, pos) ||
                                    state.propagatesSkylightDown(mc.level, pos) || 
                                    state.getLightBlock(mc.level, pos) < 15;
            
            if (isTransparent) {
                // Move slightly past the hit point to continue raycast
                currentFrom = result.getLocation().add(direction.scale(0.1));
                
                // Check if we passed the target
                if (currentFrom.distanceToSqr(from) >= totalDistSqr) {
                    return true;
                }
                steps++;
            } else {
                return false;
            }
        }
        return false;
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
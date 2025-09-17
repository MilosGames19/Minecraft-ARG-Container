package net.mcreator.minecraftalphaargmod.util;

import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class MoonLookUtil {
    private MoonLookUtil() {}

    public static boolean isLookingAtMoon(Player player, float partialTicks, double toleranceDegrees) {
        if (player == null) return false;

        // === CONFIG CHECK ===
        if (!McconfigConfiguration.DONT_LOOK_AT_THE_MOON.get()) return false;

        // === CLOCK CHECK ===
        boolean hasClock = player.getMainHandItem().is(Items.CLOCK) || player.getOffhandItem().is(Items.CLOCK);
        if (!hasClock) return false;

        Level level = player.level();
        if (!Level.OVERWORLD.equals(level.dimension())) return false;
        if (level.isDay()) return false;

        // Must see sky at eye level and not be blinded/darkened
        BlockPos eyePos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
        if (!level.canSeeSky(eyePos)) return false;
        if (player.hasEffect(MobEffects.BLINDNESS) || player.hasEffect(MobEffects.DARKNESS)) return false;

        Vec3 moonDir = getMoonDirection(level, partialTicks);
        if (moonDir.y <= 0.0) return false;

        // Raycast from eye toward moon
        Vec3 eyeVec = player.getEyePosition(partialTicks);
        Vec3 moonTarget = eyeVec.add(moonDir.scale(300));
        BlockHitResult hit = level.clip(new ClipContext(
                eyeVec, moonTarget,
                ClipContext.Block.VISUAL, ClipContext.Fluid.NONE,
                player
        ));
        if (hit.getType() == HitResult.Type.BLOCK) return false; // Blocked view

        // Angle tolerance
        Vec3 look = player.getLookAngle().normalize();
        double dot = Mth.clamp(look.dot(moonDir), -1.0, 1.0);
        double angleDeg = Math.toDegrees(Math.acos(dot));
        return angleDeg <= toleranceDegrees;
    }

    /**
     * Matches renderSky: Y(-90°) then X(timeOfDay * 360°) on base (0,-1,0)
     */
    public static Vec3 getMoonDirection(Level level, float partialTicks) {
        double a = level.getTimeOfDay(partialTicks) * (Math.PI * 2.0);
        return new Vec3(Math.sin(a), -Math.cos(a), 0.0).normalize();
    }
}

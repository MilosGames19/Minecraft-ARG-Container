package net.mcreator.minecraftalphaargmod.animation;

import net.minecraft.client.gui.components.AbstractWidget;
import java.util.WeakHashMap;

public final class ButtonFlyInTracker {

    public static final long ANIM_DURATION_MS = 300L;
    private static final WeakHashMap<AbstractWidget, Long> spawnTimes = new WeakHashMap<>();

    private ButtonFlyInTracker() {}

    public static int getYOffset(AbstractWidget widget, int screenHeight) {
        long spawnTime = spawnTimes.computeIfAbsent(widget, w -> System.currentTimeMillis());

        long elapsed = System.currentTimeMillis() - spawnTime;
        if (elapsed >= ANIM_DURATION_MS) {
            return 0;
        }

        float progress = (float) elapsed / (float) ANIM_DURATION_MS;
        float eased = easeOutQuad(1.0F - progress);
        return (int) (screenHeight * eased);
    }

    private static float easeOutQuad(float t) {
        return t * (2.0F - t);
    }
}
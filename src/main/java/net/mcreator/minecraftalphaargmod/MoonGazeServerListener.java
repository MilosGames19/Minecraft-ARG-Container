package net.mcreator.minecraftalphaargmod.gaze;

import net.mcreator.minecraftalphaargmod.util.MoonLookUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = MoonGazeServerListener.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MoonGazeServerListener {
    public static final String MODID = "the_arg_container";
    private static final int REQUIRED_TICKS = 600; // 30 seconds
    private static final double TOLERANCE_DEGREES = 10.0;
    private static final Component MESSAGE = Component.literal("You shouldn't have done that.");
    private static final Map<UUID, Integer> GAZE_TICKS = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        Player p = e.player;
        if (p.level().isClientSide) return;
        if (!(p instanceof ServerPlayer sp)) return;

        boolean looking = MoonLookUtil.isLookingAtMoon(sp, 0.0f, TOLERANCE_DEGREES);
        int ticks = GAZE_TICKS.getOrDefault(sp.getUUID(), 0);
        ticks = looking ? Math.min(REQUIRED_TICKS, ticks + 1) : 0;
        GAZE_TICKS.put(sp.getUUID(), ticks);

        if (ticks >= REQUIRED_TICKS) {
            // First: do the End Stone wave
            EndstoneWave.start(sp, 70, () -> {
                sp.sendSystemMessage(MESSAGE);
                sp.connection.disconnect(MESSAGE);
            });

            GAZE_TICKS.remove(sp.getUUID());
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        GAZE_TICKS.remove(e.getEntity().getUUID());
    }
}

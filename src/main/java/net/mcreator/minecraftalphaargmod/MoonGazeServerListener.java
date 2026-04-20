package net.mcreator.minecraftalphaargmod.gaze;

import net.mcreator.minecraftalphaargmod.util.MoonLookUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = MoonGazeServerListener.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MoonGazeServerListener {

	public static final String MODID = "the_arg_container";

	private static final int REQUIRED_TICKS = 600;
	private static final int DECAY_PER_TICK = 2;
	private static final double TOLERANCE_DEG = 10.0;
	private static final int WAVE_RADIUS = 70;
	private static final Component KICK_MSG = Component.literal("You shouldn't have done that.");

	private static final Map<UUID, Integer> GAZE_TICKS = new HashMap<>();
	private static final Map<UUID, ResourceKey<Level>> LAST_DIMENSION = new HashMap<>();

	private MoonGazeServerListener() {
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
		if (e.phase != TickEvent.Phase.END)
			return;

		Player p = e.player;
		if (p.level().isClientSide)
			return;
		if (!(p instanceof ServerPlayer sp))
			return;

		UUID id = sp.getUUID();

		ResourceKey<Level> currentDim = sp.level().dimension();
		ResourceKey<Level> lastDim = LAST_DIMENSION.put(id, currentDim);
		if (lastDim != null && !lastDim.equals(currentDim)) {
			GAZE_TICKS.remove(id);
			return;
		}

		boolean looking = MoonLookUtil.isLookingAtMoon(sp, 0.0f, TOLERANCE_DEG);
		int ticks = GAZE_TICKS.getOrDefault(id, 0);

		if (looking) {
			ticks = Math.min(REQUIRED_TICKS, ticks + 1);
		} else {
			ticks = Math.max(0, ticks - DECAY_PER_TICK);
		}

		if (ticks <= 0) {
			GAZE_TICKS.remove(id);
		} else {
			GAZE_TICKS.put(id, ticks);
		}

		if (ticks >= REQUIRED_TICKS) {
			GAZE_TICKS.remove(id);
			LAST_DIMENSION.remove(id);
			EndstoneWave.start(sp, WAVE_RADIUS, () -> {
				sp.sendSystemMessage(KICK_MSG);
				sp.connection.disconnect(KICK_MSG);
			});
		}
	}

	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent e) {
		if (e.getEntity() instanceof ServerPlayer sp) {
			GAZE_TICKS.remove(sp.getUUID());
		}
	}

	@SubscribeEvent
	public static void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
		UUID id = e.getEntity().getUUID();
		GAZE_TICKS.remove(id);
		LAST_DIMENSION.remove(id);
	}

	@SubscribeEvent
	public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent e) {
		GAZE_TICKS.remove(e.getEntity().getUUID());
	}
}
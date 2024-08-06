package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.entity.Entity;

import net.mcreator.minecraftalphaargmod.network.MinecraftAlphaArgModModVariables;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class CountdownProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			execute(event, event.player);
		}
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		{
			double _setval = (entity.getCapability(MinecraftAlphaArgModModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new MinecraftAlphaArgModModVariables.PlayerVariables())).Cooldown - 1;
			entity.getCapability(MinecraftAlphaArgModModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
				capability.Cooldown = _setval;
				capability.syncPlayerVariables(entity);
			});
		}
	}
}

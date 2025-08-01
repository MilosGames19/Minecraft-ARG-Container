package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.minecraftalphaargmod.entity.RecruiterEntity;
import net.mcreator.minecraftalphaargmod.TheArgContainerMod;

import javax.annotation.Nullable;

import java.util.List;
import java.util.Comparator;

@Mod.EventBusSubscriber
public class RecruiterOnEntityTickUpdateProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			execute(event, event.player.level(), event.player);
		}
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		double Distance = 0;
		Distance = 20;
		{
			final Vec3 _center = new Vec3((entity.getX() + entity.getLookAngle().x * Distance), (entity.getY() + entity.getLookAngle().y * Distance), (entity.getZ() + entity.getLookAngle().z * Distance));
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(1 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (entityiterator instanceof RecruiterEntity) {
					TheArgContainerMod.queueServerWork(20, () -> {
						if (!entityiterator.level().isClientSide())
							entityiterator.discard();
					});
				}
			}
		}
		Distance = Distance + 1;
	}
}

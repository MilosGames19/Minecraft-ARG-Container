package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModEntities;
import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class RecruiterSpawnProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			execute(event, event.player.level(), event.player.getX(), event.player.getY(), event.player.getZ(), event.player);
		}
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		double eX = 0;
		double eY = 0;
		double eZ = 0;
		double nOffset = 0;
		double nEntityCount = 0;
		double nSize = 0;
		boolean bBreak = false;
		if (entity.getPersistentData().getDouble("The_ARG_Container_Recruter_spawn") == 0) {
			entity.getPersistentData().putDouble("The_ARG_Container_Recruter_spawn", 24000);
		} else {
			entity.getPersistentData().putDouble("The_ARG_Container_Recruter_spawn", (entity.getPersistentData().getDouble("The_ARG_Container_Recruter_spawn") - 1));
		}
		if (entity.getPersistentData().getDouble("The_ARG_Container_Recruter_spawn") == 0 && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z)) && McconfigConfiguration.SV_ALLOWRNET.get() == true) {
			nSize = 30;
			nEntityCount = 1;
			nOffset = 1;
			for (int index0 = 0; index0 < (int) nEntityCount; index0++) {
				eX = entity.getX() + Mth.nextInt(RandomSource.create(), (int) (0 - nSize), (int) nSize);
				eY = entity.getY() + nOffset;
				eZ = entity.getZ() + Mth.nextInt(RandomSource.create(), (int) (0 - nSize), (int) nSize);
				for (int index1 = 0; index1 < (int) (nOffset * 2 + 1); index1++) {
					if (world.getBlockState(BlockPos.containing(Math.floor(eX + 0.5), Math.floor(eY - 1), Math.floor(eZ + 0.5))).isFaceSturdy(world, BlockPos.containing(Math.floor(eX + 0.5), Math.floor(eY - 1), Math.floor(eZ + 0.5)), Direction.UP)) {
						bBreak = true;
						break;
					}
					eY = eY - 1;
				}
				if (bBreak) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = TheArgContainerModEntities.RECRUITER_V_2.get().spawn(_level, BlockPos.containing(eX, eY, eZ), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
						}
					}
					bBreak = false;
				}
			}
		}
	}
}

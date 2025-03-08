package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.level.BlockEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.minecraftalphaargmod.init.TheArgContainerModEntities;
import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class RecruiterSpawnProcedure {
	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		execute(event, event.getLevel(), event.getPlayer());
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		double eX = 0;
		double eY = 0;
		double eZ = 0;
		double nOffset = 0;
		double nEntityCount = 0;
		double nSize = 0;
		boolean bBreak = false;
		if (Mth.nextDouble(RandomSource.create(), 1, 1000) == 15 && McconfigConfiguration.SV_ALLOWRNET.get() == true) {
			if (!world.isClientSide() && world.getServer() != null)
				world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("DEBUG: Success"), false);
			nSize = 30;
			nEntityCount = 1;
			nOffset = 6;
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
						Entity entityToSpawn = TheArgContainerModEntities.RECRUITER.get().spawn(_level, BlockPos.containing(eX, eY, eZ), MobSpawnType.MOB_SUMMONED);
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

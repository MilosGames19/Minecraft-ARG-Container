package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import net.mcreator.minecraftalphaargmod.init.MinecraftAlphaArgModModEntities;
import net.mcreator.minecraftalphaargmod.entity.User0CloneEntity;
import net.mcreator.minecraftalphaargmod.entity.EvilUser0Entity;

public class CurseofUser0ActiveTickConditionProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (!(!world.getEntitiesOfClass(User0CloneEntity.class, AABB.ofSize(new Vec3(x, y, z), 40, 40, 40), e -> true).isEmpty())) {
			for (int index0 = 0; index0 < 5; index0++) {
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = MinecraftAlphaArgModModEntities.USER_0_CLONE.get().spawn(_level, BlockPos.containing(x + Mth.nextDouble(RandomSource.create(), -20, 20), y + 3, z + Mth.nextDouble(RandomSource.create(), -20, 20)),
							MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
					}
				}
			}
			if (!world.getEntitiesOfClass(EvilUser0Entity.class, AABB.ofSize(new Vec3(x, y, z), 100, 100, 100), e -> true).isEmpty() && Math.random() < 0.1) {
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = MinecraftAlphaArgModModEntities.EVIL_USER_0.get().spawn(_level, BlockPos.containing(x + Mth.nextDouble(RandomSource.create(), -2, 2), y + 3, z + Mth.nextDouble(RandomSource.create(), -2, 2)),
							MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
					}
				}
				if (world instanceof ServerLevel _level) {
					LightningBolt entityToSpawn = EntityType.LIGHTNING_BOLT.create(_level);
					entityToSpawn.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(x, y, z)));
					entityToSpawn.setVisualOnly(true);
					_level.addFreshEntity(entityToSpawn);
				}
			}
		}
	}
}

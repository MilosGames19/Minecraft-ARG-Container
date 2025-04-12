package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerPlayer;

public class ObserverMobEntityIsHurtProcedure {
	public static void execute(double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		boolean found = false;
		double sx = 0;
		double sy = 0;
		double sz = 0;
		if (Math.random() < 0.3) {
			{
				Entity _ent = entity;
				_ent.teleportTo((x + Mth.nextDouble(RandomSource.create(), 1, 15)), (y + Math.random()), (z + Mth.nextDouble(RandomSource.create(), 1, 15)));
				if (_ent instanceof ServerPlayer _serverPlayer)
					_serverPlayer.connection.teleport((x + Mth.nextDouble(RandomSource.create(), 1, 15)), (y + Math.random()), (z + Mth.nextDouble(RandomSource.create(), 1, 15)), _ent.getYRot(), _ent.getXRot());
			}
		}
	}
}

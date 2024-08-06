package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;

public class ObserverMobEntityIsHurtProcedure {
	public static void execute(double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		{
			Entity _ent = entity;
			_ent.teleportTo((x + Math.random()), (y + Math.random()), (z + Math.random()));
			if (_ent instanceof ServerPlayer _serverPlayer)
				_serverPlayer.connection.teleport((x + Math.random()), (y + Math.random()), (z + Math.random()), _ent.getYRot(), _ent.getXRot());
		}
	}
}

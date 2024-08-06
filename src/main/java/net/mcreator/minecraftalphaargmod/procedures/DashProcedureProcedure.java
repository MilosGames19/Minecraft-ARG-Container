package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

import net.mcreator.minecraftalphaargmod.network.MinecraftAlphaArgModModVariables;

public class DashProcedureProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((entity.getCapability(MinecraftAlphaArgModModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new MinecraftAlphaArgModModVariables.PlayerVariables())).Cooldown < 1) {
			if (entity.isSprinting()) {
				entity.setDeltaMovement(new Vec3((entity.getDeltaMovement().x() + entity.getLookAngle().x * 2), (entity.getDeltaMovement().y() + entity.getLookAngle().y * 2), (entity.getDeltaMovement().z() + entity.getLookAngle().z * 2)));
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("minecraft_alpha_arg_mod:dash")), SoundSource.NEUTRAL, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("minecraft_alpha_arg_mod:dash")), SoundSource.NEUTRAL, 1, 1, false);
					}
				}
				{
					double _setval = 60;
					entity.getCapability(MinecraftAlphaArgModModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
						capability.Cooldown = _setval;
						capability.syncPlayerVariables(entity);
					});
				}
			}
		}
	}
}

package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.client.Minecraft;

import net.mcreator.minecraftalphaargmod.network.TheArgContainerModVariables;

public class SpaceDashProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayer _serverPlayer) {
					return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SURVIVAL;
				} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SURVIVAL;
				}
				return false;
			}
		}.checkGamemode(entity)) {
			if ((entity.getCapability(TheArgContainerModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new TheArgContainerModVariables.PlayerVariables())).Cooldown < 1) {
				if (entity.isShiftKeyDown()) {
					entity.setDeltaMovement(new Vec3((entity.getDeltaMovement().x() + entity.getLookAngle().x * 1.6), (entity.getDeltaMovement().y() + entity.getLookAngle().y * 3.5), (entity.getDeltaMovement().z() + entity.getLookAngle().z * 1.6)));
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("the_arg_container:maror")), SoundSource.NEUTRAL, 1, 1);
						} else {
							_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("the_arg_container:maror")), SoundSource.NEUTRAL, 1, 1, false);
						}
					}
					{
						double _setval = 20;
						entity.getCapability(TheArgContainerModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
							capability.Cooldown = _setval;
							capability.syncPlayerVariables(entity);
						});
					}
				}
			}
		}
	}
}

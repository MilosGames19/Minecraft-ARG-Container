package net.mcreator.minecraftalphaargmod.procedures;

// Alphaver code

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
import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;

public class DashProcedureProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null)
            return;

        // Allow dash if: ability enabled, survival mode, and (sprinting OR in air)
        if (McconfigConfiguration.ALPHAVER_DASH_ABILITY.get() == true && new Object() {
            public boolean checkGamemode(Entity _ent) {
                if (_ent instanceof ServerPlayer _serverPlayer) {
                    return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SURVIVAL;
                } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                    return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                        && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SURVIVAL;
                }
                return false;
            }
        }.checkGamemode(entity) && (entity.isSprinting() || !entity.onGround())) {

            double currentDashTimer = (entity.getCapability(TheArgContainerModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .orElse(new TheArgContainerModVariables.PlayerVariables())).Cooldown;

            if (currentDashTimer == 0) {
                if (world instanceof Level _level) {
                    if (!_level.isClientSide()) {
                        _level.playSound(null, BlockPos.containing(x, y, z),
                            ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("the_arg_container:dash")),
                            SoundSource.NEUTRAL, 0.6f, 1.0f);
                    } else {
                        _level.playLocalSound(x, y, z,
                            ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("the_arg_container:dash")),
                            SoundSource.NEUTRAL, 0.6f, 1.0f, false);
                    }
                }

                {
                    double _setval = 60;
                    entity.getCapability(TheArgContainerModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                        capability.Cooldown = _setval;
                        capability.syncPlayerVariables(entity);
                    });
                }

                entity.setDeltaMovement(new Vec3(
                    entity.getDeltaMovement().x() * 10.0,
                    entity.getDeltaMovement().y() * 3.0,
                    entity.getDeltaMovement().z() * 10.0
                ));

                if (entity instanceof Player _player)
                    _player.getFoodData().setFoodLevel((int) ((entity instanceof Player _plr ? _plr.getFoodData().getFoodLevel() : 0) - 2));

            } else if (currentDashTimer < 15) {
                if (world instanceof Level _level) {
                    if (!_level.isClientSide()) {
                        _level.playSound(null, BlockPos.containing(x, y, z),
                            ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("minecraft:entity.player.attack.sweep")),
                            SoundSource.NEUTRAL, 1.0f, 1.0f);
                    } else {
                        _level.playLocalSound(x, y, z,
                            ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("minecraft:entity.player.attack.sweep")),
                            SoundSource.NEUTRAL, 1.0f, 1.0f, false);
                    }
                }

                Vec3 lookAngle = entity.getLookAngle();

                entity.setDeltaMovement(new Vec3(
                    entity.getDeltaMovement().x() + lookAngle.x,
                    entity.getDeltaMovement().y() + 0.2,
                    entity.getDeltaMovement().z() + lookAngle.z
                ));

                {
                    double _setval = 60;
                    entity.getCapability(TheArgContainerModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                        capability.Cooldown = _setval;
                        capability.syncPlayerVariables(entity);
                    });
                }
            }
        }
    }

    public static double angleFromXY(double x, double y) {
        return Math.atan2(y, x);
    }
}

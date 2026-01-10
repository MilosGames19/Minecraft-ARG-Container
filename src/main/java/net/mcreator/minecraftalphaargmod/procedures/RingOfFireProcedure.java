package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.client.Minecraft;

public class RingOfFireProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) return;

        if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 160, 0, false, false, false));
        }

        boolean isAdventure = false;
        if (entity instanceof ServerPlayer _serverPlayer) {
            isAdventure = _serverPlayer.gameMode.getGameModeForPlayer() == GameType.ADVENTURE;
        } else if (entity.level().isClientSide() && entity instanceof Player _player) {
            isAdventure = Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null 
                && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.ADVENTURE;
        }

        if (!isAdventure) {
            int range = 2;
            for (int xi = -range; xi <= range; xi++) {
                for (int zi = -range; zi <= range; zi++) {
                    double distance = Math.sqrt(xi * xi + zi * zi);
                    
                    if (distance >= 1.5 && distance <= 2.5) {
                        
                        for (int yi = 2; yi >= -2; yi--) {
                            BlockPos targetPos = BlockPos.containing(x + xi, y + yi, z + zi);
                            
                            if (world.isEmptyBlock(targetPos) && world.getBlockState(targetPos.below()).isSolidRender(world, targetPos.below())) {
                                world.setBlock(targetPos, Blocks.FIRE.defaultBlockState(), 3);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
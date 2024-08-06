package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.levelgen.Heightmap;
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
		if (entity == null)
			return;
		double i = 0;
		double angle = 0;
		double radius = 0;
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 1));
		if (!(new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayer _serverPlayer) {
					return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.ADVENTURE;
				} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.ADVENTURE;
				}
				return false;
			}
		}.checkGamemode(entity))) {
			if (world.isEmptyBlock(BlockPos.containing(x - 1, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x - 1), (int) (z + 2)), z + 2))) {
				world.setBlock(BlockPos.containing(x - 1, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x - 1), (int) (z + 2)), z + 2), Blocks.FIRE.defaultBlockState(), 3);
			}
			if (world.isEmptyBlock(BlockPos.containing(x, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) (z + 2)), z + 2))) {
				world.setBlock(BlockPos.containing(x, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) (z + 2)), z + 2), Blocks.FIRE.defaultBlockState(), 3);
			}
			if (world.isEmptyBlock(BlockPos.containing(x + 1, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x + 1), (int) (z + 2)), z + 2))) {
				world.setBlock(BlockPos.containing(x + 1, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x + 1), (int) (z + 2)), z + 2), Blocks.FIRE.defaultBlockState(), 3);
			}
			if (world.isEmptyBlock(BlockPos.containing(x + 2, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x + 2), (int) (z - 1)), z - 1))) {
				world.setBlock(BlockPos.containing(x + 2, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x + 2), (int) (z - 1)), z - 1), Blocks.FIRE.defaultBlockState(), 3);
			}
			if (world.isEmptyBlock(BlockPos.containing(x + 2, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x + 2), (int) z), z))) {
				world.setBlock(BlockPos.containing(x + 2, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x + 2), (int) z), z), Blocks.FIRE.defaultBlockState(), 3);
			}
			if (world.isEmptyBlock(BlockPos.containing(x + 2, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x + 2), (int) (z + 1)), z + 1))) {
				world.setBlock(BlockPos.containing(x + 2, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x + 2), (int) (z + 1)), z + 1), Blocks.FIRE.defaultBlockState(), 3);
			}
			if (world.isEmptyBlock(BlockPos.containing(x - 1, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x - 1), (int) (z - 2)), z - 2))) {
				world.setBlock(BlockPos.containing(x - 1, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x - 1), (int) (z - 2)), z - 2), Blocks.FIRE.defaultBlockState(), 3);
			}
			if (world.isEmptyBlock(BlockPos.containing(x, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) (z - 2)), z - 2))) {
				world.setBlock(BlockPos.containing(x, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) (z - 2)), z - 2), Blocks.FIRE.defaultBlockState(), 3);
			}
			if (world.isEmptyBlock(BlockPos.containing(x + 1, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x + 1), (int) (z - 2)), z - 2))) {
				world.setBlock(BlockPos.containing(x + 1, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x + 1), (int) (z - 2)), z - 2), Blocks.FIRE.defaultBlockState(), 3);
			}
			if (world.isEmptyBlock(BlockPos.containing(x - 2, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x - 2), (int) (z - 1)), z - 1))) {
				world.setBlock(BlockPos.containing(x - 2, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x - 2), (int) (z - 1)), z - 1), Blocks.FIRE.defaultBlockState(), 3);
			}
			if (world.isEmptyBlock(BlockPos.containing(x - 2, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x - 2), (int) z), z))) {
				world.setBlock(BlockPos.containing(x - 2, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x - 2), (int) z), z), Blocks.FIRE.defaultBlockState(), 3);
			}
			if (world.isEmptyBlock(BlockPos.containing(x - 2, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x - 2), (int) (z + 1)), z + 1))) {
				world.setBlock(BlockPos.containing(x - 2, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) (x - 2), (int) (z + 1)), z + 1), Blocks.FIRE.defaultBlockState(), 3);
			}
		}
		radius = 1.5;
		i = 0;
		while (i < 360) {
			angle = i * (3.1415926535 / 180);
			if (Math.random() <= 0.5) {
				world.addParticle(ParticleTypes.FLAME, (radius * Math.cos(angle) + x), (y + 0.0625), (radius * Math.sin(angle) + z), (Mth.nextDouble(RandomSource.create(), -0.02, 0.02)), 0.075, (Mth.nextDouble(RandomSource.create(), -0.02, 0.02)));
			}
			i = i + 4.99999999;
		}
	}
}

package net.mcreator.minecraftalphaargmod.procedures;

// Alphaver code

import org.checkerframework.checker.units.qual.radians;

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
import net.mcreator.minecraftalphaargmod.TheArgContainerMod;

public class SpaceDashProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;

		boolean serverSide = (world instanceof Level lvl) && !lvl.isClientSide();

		if (!McconfigConfiguration.ALPHAVER_LONG_JUMP_ABILITY.get())
			return;
		if (!isSurvival(entity))
			return;

		if (!entity.isCrouching())
			return;

		if (entity instanceof Player p && p.getFoodData().getFoodLevel() <= 6)
			return;

		TheArgContainerModVariables.PlayerVariables vars = entity.getCapability(TheArgContainerModVariables.PLAYER_VARIABLES_CAPABILITY).orElse(null);
		double cooldown = (vars != null) ? vars.Cooldown : 0D;

		if (cooldown > 0)
			return;

		if (world instanceof Level lvl) {
			var sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("the_arg_container", "maror"));
			if (sound != null) {
				if (!lvl.isClientSide()) {
					lvl.playSound(null, BlockPos.containing(x, y, z), sound, SoundSource.PLAYERS, 1.0F, 1.0F);
				} else {
					lvl.playLocalSound(x, y, z, sound, SoundSource.PLAYERS, 1.0F, 1.0F, false);
				}
			} 
		}

		if (vars != null) {
			vars.Cooldown = 20;

			vars.syncPlayerVariables(entity);
		}

		if (serverSide && entity instanceof Player player) {

			double moveForward = player.zza;
			double moveStrafe = player.xxa;

			boolean hasInput = Math.abs(moveForward) > 1.0E-3 || Math.abs(moveStrafe) > 1.0E-3;
			if (!hasInput)
				moveForward = 1.0;

			double angleDeg = Math.atan2(-moveStrafe, moveForward) / Math.PI * 180.0;
			double radians = Math.toRadians(player.getYRot() + 90.0F + angleDeg);
			double cos = Math.cos(radians);
			double sin = Math.sin(radians);

			Vec3 before = player.getDeltaMovement();
			Vec3 after = new Vec3(before.x + cos, before.y + 0.5D, before.z + sin);
			player.setDeltaMovement(after);
			player.hurtMarked = true;

		}
	}

	private static boolean isSurvival(Entity entity) {
		if (entity instanceof ServerPlayer sp) {
			return sp.gameMode.getGameModeForPlayer() == GameType.SURVIVAL;
		} else if (entity instanceof Player p) {
			var mc = Minecraft.getInstance();
			if (mc != null && mc.getConnection() != null) {
				var info = mc.getConnection().getPlayerInfo(p.getUUID());
				return info != null && info.getGameMode() == GameType.SURVIVAL;
			}
		}
		return false;
	}
}

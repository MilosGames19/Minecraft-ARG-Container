package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

import net.mcreator.minecraftalphaargmod.network.TheArgContainerModVariables;
import net.mcreator.minecraftalphaargmod.TheArgContainerMod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class ModCheckProcedure {
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		execute(event, event.getEntity().level(), event.getEntity());
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (ModList.get().isLoaded("thebrokenscript") == true && TheArgContainerModVariables.MapVariables.get(world).ModCheck == false) {
			if (entity instanceof ServerPlayer _player) {
				Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("the_arg_container:tbs"));
				AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
				if (!_ap.isDone()) {
					for (String criteria : _ap.getRemainingCriteria())
						_player.getAdvancements().award(_adv, criteria);
				}
			}
			TheArgContainerMod.queueServerWork(200, () -> {
				TheArgContainerModVariables.MapVariables.get(world).ModCheck = true;
				TheArgContainerModVariables.MapVariables.get(world).syncData(world);
				TheArgContainerMod.queueServerWork(18000, () -> {
					if (entity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal("\u00A7eThe_Creator joined the game"), false);
					TheArgContainerModVariables.MapVariables.get(world).ModCheck = true;
					TheArgContainerModVariables.MapVariables.get(world).syncData(world);
					TheArgContainerMod.queueServerWork(100, () -> {
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("<The_Creator> You made it work, did you?"), false);
						TheArgContainerMod.queueServerWork(100, () -> {
							if (entity instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("<The_Creator> Technically you're not breaking anything, so we might as well let you be."), false);
							TheArgContainerMod.queueServerWork(60, () -> {
								if (entity instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("<The_Creator> Farewell."), false);
								TheArgContainerMod.queueServerWork(40, () -> {
									if (entity instanceof Player _player && !_player.level().isClientSide())
										_player.displayClientMessage(Component.literal("\u00A7eThe_Creator left the game"), false);
								});
							});
						});
					});
				});
			});
		}
	}
}

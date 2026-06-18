package net.mcreator.minecraftalphaargmod;

import com.mojang.brigadier.CommandDispatcher;
import net.mcreator.minecraftalphaargmod.client.NotificationPositionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class NotificationPositionCommand {

	@SubscribeEvent
	public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
		register(event.getDispatcher());
	}

	private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("notifypos").executes(ctx -> {
			Minecraft.getInstance().tell(() -> Minecraft.getInstance().setScreen(new NotificationPositionScreen()));
			return 1;
		}));
	}
}
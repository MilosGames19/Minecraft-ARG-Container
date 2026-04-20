package net.mcreator.minecraftalphaargmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CensorshipCommand {

	private static final UUID AUTHORIZED_UUID = UUID.fromString("2d10b449-51f2-4d96-b2e3-c3bdc0332a81");

	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

		dispatcher.register(Commands.literal("censor").requires(CensorshipCommand::isAuthorized)
				.then(Commands.literal("add").then(Commands.argument("target", EntityArgument.player()).executes(context -> addCensor(context, EntityArgument.getPlayer(context, "target")))).executes(CensorshipCommand::addCensorSelf))
				.then(Commands.literal("remove").then(Commands.argument("target", EntityArgument.player()).executes(context -> removeCensor(context, EntityArgument.getPlayer(context, "target")))).executes(CensorshipCommand::removeCensorSelf))
				.then(Commands.literal("list").executes(CensorshipCommand::listCensored)).then(Commands.literal("clear").executes(CensorshipCommand::clearAll)));
	}

	private static boolean isAuthorized(CommandSourceStack source) {
		if (!source.isPlayer())
			return false;
		try {
			return source.getPlayerOrException().getUUID().equals(AUTHORIZED_UUID);
		} catch (Exception e) {
			return false;
		}
	}

	private static int addCensor(CommandContext<CommandSourceStack> context, ServerPlayer target) {
		ServerPlayer executor = getExecutorPlayer(context);
		if (executor == null)
			return 0;

		CensorshipBarNetwork.sendToPlayer(executor, new CensorshipBarSyncPacket(CensorshipBarSyncPacket.Action.ADD, target.getUUID()));

		context.getSource().sendSuccess(() -> Component.literal("§aCensorship applied to " + target.getName().getString()), false);
		return 1;
	}

	private static int addCensorSelf(CommandContext<CommandSourceStack> context) {
		ServerPlayer executor = getExecutorPlayer(context);
		if (executor == null)
			return 0;

		CensorshipBarNetwork.sendToPlayer(executor, new CensorshipBarSyncPacket(CensorshipBarSyncPacket.Action.ADD, executor.getUUID()));

		context.getSource().sendSuccess(() -> Component.literal("§aCensorship applied to yourself"), false);
		return 1;
	}

	private static int removeCensor(CommandContext<CommandSourceStack> context, ServerPlayer target) {
		ServerPlayer executor = getExecutorPlayer(context);
		if (executor == null)
			return 0;

		CensorshipBarNetwork.sendToPlayer(executor, new CensorshipBarSyncPacket(CensorshipBarSyncPacket.Action.REMOVE, target.getUUID()));

		context.getSource().sendSuccess(() -> Component.literal("§eCensorship removed from " + target.getName().getString()), false);
		return 1;
	}

	private static int removeCensorSelf(CommandContext<CommandSourceStack> context) {
		ServerPlayer executor = getExecutorPlayer(context);
		if (executor == null)
			return 0;

		CensorshipBarNetwork.sendToPlayer(executor, new CensorshipBarSyncPacket(CensorshipBarSyncPacket.Action.REMOVE, executor.getUUID()));

		context.getSource().sendSuccess(() -> Component.literal("§eCensorship removed from yourself"), false);
		return 1;
	}

	private static int listCensored(CommandContext<CommandSourceStack> context) {

		context.getSource().sendSuccess(() -> Component.literal("§6Use /censor add/remove to manage censored entities."), false);
		return 1;
	}

	private static int clearAll(CommandContext<CommandSourceStack> context) {
		ServerPlayer executor = getExecutorPlayer(context);
		if (executor == null)
			return 0;

		CensorshipBarNetwork.sendToPlayer(executor, new CensorshipBarSyncPacket(CensorshipBarSyncPacket.Action.CLEAR, null));

		context.getSource().sendSuccess(() -> Component.literal("§cAll censorship cleared."), false);
		return 1;
	}

	private static ServerPlayer getExecutorPlayer(CommandContext<CommandSourceStack> context) {
		try {
			return context.getSource().getPlayerOrException();
		} catch (Exception e) {
			context.getSource().sendFailure(Component.literal("§cOnly players can use this command."));
			return null;
		}
	}
}
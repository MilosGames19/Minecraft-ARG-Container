package net.mcreator.minecraftalphaargmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mcreator.minecraftalphaargmod.capes.CapeCommand;
import net.mcreator.minecraftalphaargmod.capes.CapeRegistry;
import net.mcreator.minecraftalphaargmod.network.OpenNotificationConfigPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import static net.minecraft.commands.Commands.literal;

@Mod.EventBusSubscriber(modid = "the_arg_container")
public class TheArgContainerCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("the_arg_container")
                .then(literal("cape_selector")
                        .requires(source -> source.getEntity() instanceof ServerPlayer player
                                && CapeRegistry.canAccessCapes(player))
                        .executes(CapeCommand::openSelector))
                .then(literal("notification_config")
                        .requires(source -> source.getEntity() instanceof ServerPlayer)
                        .executes(TheArgContainerCommand::openNotificationConfig)));
    }

    private static int openNotificationConfig(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        TheArgContainerMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new OpenNotificationConfigPacket());
        return 1;
    }
}
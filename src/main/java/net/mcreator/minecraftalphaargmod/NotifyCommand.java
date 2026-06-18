package net.mcreator.minecraftalphaargmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mcreator.minecraftalphaargmod.network.ServerNotificationNetwork;
import net.mcreator.minecraftalphaargmod.network.ServerNotificationPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber
public class NotifyCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("notify")
                .requires(src -> src.hasPermission(2))

                .then(Commands.argument("text", StringArgumentType.greedyString())
                    .executes(ctx -> send(ctx.getSource(),
                        StringArgumentType.getString(ctx, "text"),
                        0xFFFFFF, false))
                )

                .then(Commands.literal("color")
                    .then(Commands.argument("hex", StringArgumentType.word())

                        .then(Commands.literal("server")
                            .then(Commands.argument("text", StringArgumentType.greedyString())
                                .executes(ctx -> send(ctx.getSource(),
                                    StringArgumentType.getString(ctx, "text"),
                                    parseColor(StringArgumentType.getString(ctx, "hex")),
                                    true))
                            )
                        )

                        .then(Commands.argument("text", StringArgumentType.greedyString())
                            .executes(ctx -> send(ctx.getSource(),
                                StringArgumentType.getString(ctx, "text"),
                                parseColor(StringArgumentType.getString(ctx, "hex")),
                                false))
                        )
                    )
                )

                .then(Commands.literal("server")
                    .then(Commands.argument("text", StringArgumentType.greedyString())
                        .executes(ctx -> send(ctx.getSource(),
                            StringArgumentType.getString(ctx, "text"),
                            0xFFFFFF, true))
                    )
                )
        );
    }


    private static int send(CommandSourceStack src, String text, int color, boolean fromServer) {
        ServerNotificationPacket packet = new ServerNotificationPacket(text, color, fromServer);
        for (ServerPlayer player : src.getServer().getPlayerList().getPlayers()) {
            ServerNotificationNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player), packet);
        }
        return 1;
    }

    private static int parseColor(String hex) {
        try {
            String clean = hex.startsWith("#") ? hex.substring(1) : hex;
            return Integer.parseInt(clean, 16) & 0xFFFFFF;
        } catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }
}
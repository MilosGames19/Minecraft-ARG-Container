package net.mcreator.minecraftalphaargmod;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerPlayerEvents {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer joiningPlayer) {
            ArgNetworkHandler.sendAdminsToPlayer(joiningPlayer);
        }
    }

    @SubscribeEvent
    public static void onCommand(CommandEvent event) {
        String input = event.getParseResults().getReader().getString().trim();
        if (input.startsWith("op ") || input.startsWith("deop ") ||
                input.equals("op") || input.equals("deop")) {

            CommandSourceStack source = event.getParseResults().getContext().getSource();
            MinecraftServer server = source.getServer();

            server.tell(new net.minecraft.server.TickTask(server.getTickCount() + 1, () -> {
                var players = server.getPlayerList().getPlayers();
                if (!players.isEmpty()) {
                    ArgNetworkHandler.broadcastAdmins(players.get(0));
                }
            }));
        }
    }
}
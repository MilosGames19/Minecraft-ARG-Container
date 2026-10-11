package net.mcreator.minecraftalphaargmod.capes;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mcreator.minecraftalphaargmod.capes.network.CapeNetwork;
import net.mcreator.minecraftalphaargmod.capes.network.OpenCapeSelectorPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import static net.minecraft.commands.Commands.literal;

@Mod.EventBusSubscriber(modid = "the_arg_container")
public class CapeCommand {

    // @SubscribeEvent
    // public static void onRegisterCommands(RegisterCommandsEvent event) {
    //     register(event.getDispatcher());
    // }

    // private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    //     dispatcher.register(literal("targc_cape_menu")
    //             .requires(source -> source.getEntity() instanceof ServerPlayer player && CapeRegistry.hasAnyRole(player))
    //             .executes(CapeCommand::openSelector));
    // }

    public static int openSelector(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();

        // if (!CapeRegistry.hasAnyRole(player)) {
        //     return 0;
        // }
        if (!CapeRegistry.canAccessCapes(player)) {
            return 0;
        }

        String selected = PlayerCapeData.getSelectedCape(player);
        boolean testMode = CapeRegistry.isLeadDev(player);
        OpenCapeSelectorPacket packet = OpenCapeSelectorPacket.of(CapeRegistry.getSelectableCapes(player), selected, testMode);

        CapeNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
        return 1;
    }
}
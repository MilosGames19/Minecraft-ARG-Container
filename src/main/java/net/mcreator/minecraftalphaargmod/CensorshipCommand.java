package net.mcreator.minecraftalphaargmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CensorshipCommand {
    
    // Authorized UUID (without dashes)
    private static final UUID AUTHORIZED_UUID = UUID.fromString("2d10b449-51f2-4d96-b2e3-c3bdc0332a81");
    private static final String AUTHORIZED_NAME = "Dev";
    
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        
        dispatcher.register(
            Commands.literal("censor")
                .requires(source -> isAuthorized(source))
                .then(Commands.literal("add")
                    .then(Commands.argument("target", EntityArgument.player())
                        .executes(context -> addCensor(context, EntityArgument.getPlayer(context, "target")))
                    )
                    .executes(context -> addCensorSelf(context))
                )
                .then(Commands.literal("remove")
                    .then(Commands.argument("target", EntityArgument.player())
                        .executes(context -> removeCensor(context, EntityArgument.getPlayer(context, "target")))
                    )
                    .executes(context -> removeCensorSelf(context))
                )
                .then(Commands.literal("list")
                    .executes(CensorshipCommand::listCensored)
                )
        );
    }
    
    /**
     * Check if command source is authorized (username "Dev" or specific UUID)
     */
    private static boolean isAuthorized(CommandSourceStack source) {
        if (!source.isPlayer()) return false;
        
        try {
            ServerPlayer player = source.getPlayerOrException();
            String username = player.getName().getString();
            UUID uuid = player.getUUID();
            
            return username.equals(AUTHORIZED_NAME) || uuid.equals(AUTHORIZED_UUID);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Add censorship to target player
     */
    private static int addCensor(CommandContext<CommandSourceStack> context, ServerPlayer target) {
        // Add to client-side renderer via packet or direct call
        CensorshipBarRenderer.addTargetEntity(target);
        
        context.getSource().sendSuccess(
            () -> Component.literal("§aCensorship applied to " + target.getName().getString()),
            false
        );
        
        return 1;
    }
    
    /**
     * Add censorship to command executor
     */
    private static int addCensorSelf(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            CensorshipBarRenderer.addTargetEntity(player);
            
            context.getSource().sendSuccess(
                () -> Component.literal("§aCensorship applied to yourself"),
                false
            );
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("§cOnly players can use this command"));
            return 0;
        }
    }
    
    /**
     * Remove censorship from target player
     */
    private static int removeCensor(CommandContext<CommandSourceStack> context, ServerPlayer target) {
        CensorshipBarRenderer.removeTargetEntity(target.getUUID());
        
        context.getSource().sendSuccess(
            () -> Component.literal("§eCensorship removed from " + target.getName().getString()),
            false
        );
        
        return 1;
    }
    
    /**
     * Remove censorship from command executor
     */
    private static int removeCensorSelf(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            CensorshipBarRenderer.removeTargetEntity(player.getUUID());
            
            context.getSource().sendSuccess(
                () -> Component.literal("§eCensorship removed from yourself"),
                false
            );
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("§cOnly players can use this command"));
            return 0;
        }
    }
    
    /**
     * List all currently censored entities
     */
    private static int listCensored(CommandContext<CommandSourceStack> context) {
        int count = CensorshipBarRenderer.getTargetCount();
        
        context.getSource().sendSuccess(
            () -> Component.literal("§6Currently censoring " + count + " entities"),
            false
        );
        
        return 1;
    }
}
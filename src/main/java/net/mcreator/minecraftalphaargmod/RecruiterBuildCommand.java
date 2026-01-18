package net.mcreator.minecraftalphaargmod.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mcreator.minecraftalphaargmod.entity.RecruiterV2Entity;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RecruiterBuildCommand {
    
    private static final UUID AUTHORIZED_UUID = UUID.fromString("2d10b449-51f2-4d96-b2e3-c3bdc0332a81");
    private static final String AUTHORIZED_NAME = "Dev";
    
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("recruiterbuild")
                .requires(source -> {
                    // Only show command to authorized users
                    if (source.getEntity() instanceof ServerPlayer player) {
                        return isAuthorized(player);
                    }
                    return false;
                })
                .then(Commands.argument("radius", DoubleArgumentType.doubleArg(1.0, 100.0))
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        double radius = DoubleArgumentType.getDouble(ctx, "radius");
                        return executeBuildMode(player, radius);
                    }))
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    return executeBuildMode(player, 10.0);
                })
        );
    }
    
    private static boolean isAuthorized(ServerPlayer player) {
        // Check if player has the authorized UUID
        if (player.getUUID().equals(AUTHORIZED_UUID)) {
            return true;
        }
        
        // Check if player name is "Dev"
        if (player.getName().getString().equals(AUTHORIZED_NAME)) {
            return true;
        }
        
        return false;
    }
    
    private static int executeBuildMode(ServerPlayer player, double radius) {
        // Double-check authorization (security)
        if (!isAuthorized(player)) {
            // For unauthorized users, pretend command doesn't exist
            player.sendSystemMessage(Component.literal("Unknown command"));
            return 0;
        }
        
        ServerLevel level = player.serverLevel();
        AABB searchBox = new AABB(
            player.getX() - radius, player.getY() - radius, player.getZ() - radius,
            player.getX() + radius, player.getY() + radius, player.getZ() + radius
        );
        
        List<RecruiterV2Entity> recruiters = level.getEntitiesOfClass(RecruiterV2Entity.class, searchBox);
        
        if (recruiters.isEmpty()) {
            player.sendSystemMessage(Component.literal("§cNo recruiters found within " + radius + " blocks"));
            return 0;
        }
        
        int count = 0;
        for (RecruiterV2Entity recruiter : recruiters) {
            recruiter.enableBuildingMode();
            count++;
        }
        
        player.sendSystemMessage(Component.literal("§aEnabled building mode for " + count + " recruiter(s)"));
        return count;
    }
}
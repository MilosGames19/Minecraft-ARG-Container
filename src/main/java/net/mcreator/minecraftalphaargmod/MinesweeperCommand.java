package net.mcreator.minecraftalphaargmod.minesweeper;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MinesweeperCommand {
    
    private static final SuggestionProvider<CommandSourceStack> DIFFICULTY_SUGGESTIONS = (ctx, builder) -> {
        return net.minecraft.commands.SharedSuggestionProvider.suggest(
            new String[]{"beginner", "intermediate", "expert"}, builder);
    };
    
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("minesweeper")
                .then(Commands.literal("start")
                    .then(Commands.argument("difficulty", StringArgumentType.word())
                        .suggests(DIFFICULTY_SUGGESTIONS)
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            String diff = StringArgumentType.getString(ctx, "difficulty").toLowerCase();
                            
                            MinesweeperManager.Difficulty difficulty;
                            switch (diff) {
                                case "beginner": difficulty = MinesweeperManager.Difficulty.BEGINNER; break;
                                case "intermediate": difficulty = MinesweeperManager.Difficulty.INTERMEDIATE; break;
                                case "expert": difficulty = MinesweeperManager.Difficulty.EXPERT; break;
                                default:
                                    player.sendSystemMessage(Component.literal("§cInvalid difficulty! Use: beginner, intermediate, or expert"));
                                    return 0;
                            }
                            
                            BlockPos origin = player.blockPosition().below();
                            MinesweeperManager.createBoard(player.level(), origin, difficulty, player.getUUID());
                            return 1;
                        })))
                .then(Commands.literal("restore")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        MinesweeperBoard board = MinesweeperManager.getBoard(player.getUUID());
                        if (board == null) {
                            player.sendSystemMessage(Component.literal("§cNo active board to restore!"));
                            return 0;
                        }
                        MinesweeperManager.restoreBoard(player.getUUID());
                        player.sendSystemMessage(Component.literal("§aTerrain restored!"));
                        return 1;
                    }))
                .then(Commands.literal("help")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        player.sendSystemMessage(Component.literal("§6=== Minesweeper Commands ==="));
                        player.sendSystemMessage(Component.literal("§e/minesweeper start <difficulty>§7 - Start a new game"));
                        player.sendSystemMessage(Component.literal("  §7beginner: 9x9, 10 mines"));
                        player.sendSystemMessage(Component.literal("  §7intermediate: 16x16, 40 mines"));
                        player.sendSystemMessage(Component.literal("  §7expert: 30x16, 99 mines"));
                        player.sendSystemMessage(Component.literal("§e/minesweeper restore§7 - Restore terrain"));
                        player.sendSystemMessage(Component.literal("§e/minesweeper help§7 - Show this help"));
                        return 1;
                    }))
        );
    }
}
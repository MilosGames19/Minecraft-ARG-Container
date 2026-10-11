package net.mcreator.minecraftalphaargmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.mcreator.minecraftalphaargmod.network.ServerNotificationNetwork;
import net.mcreator.minecraftalphaargmod.network.ServerNotificationPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

@Mod.EventBusSubscriber(modid = "the_arg_container")
public class NotifyCommand {

    private static final int OP_PERMISSION_LEVEL = 2;
    private static final int DEFAULT_COLOR = 0xFFFFFF;

    private static final Pattern HEX_PATTERN = Pattern.compile("^#?[0-9a-fA-F]{6}$");
    private static final String[] NAMED_COLOR_SUGGESTIONS = buildNamedColorSuggestions();
    // Placeholder text, not a real value - "RRGGBB" itself fails HEX_PATTERN if tab-completed
    // (see Bug #37), so suggest actual example colors instead.
    private static final String[] HEX_VALUE_SUGGESTIONS = { "FF0000", "00FF00", "0000FF" };

    // Disabled by default. Toggle with "/notify feedback true|false".
    // Both the feedback executor and send() run on the server main thread, so this doesn't
    // need to be volatile (see Bug #36) - only touched by one thread at a time.
    private static boolean feedbackEnabled = false;

    private static final DynamicCommandExceptionType ERROR_INVALID_NAMED_COLOR = new DynamicCommandExceptionType(
        color -> Component.literal("Invalid color '" + color + "', expected a vanilla color name (e.g. gold, aqua, dark_red)"));

    private static final DynamicCommandExceptionType ERROR_INVALID_HEX_COLOR = new DynamicCommandExceptionType(
        color -> Component.literal("Invalid hex color '" + color + "', expected 6 hex digits (e.g. RRGGBB)"));

    private static String[] buildNamedColorSuggestions() {
        List<String> suggestions = new ArrayList<>();
        for (ChatFormatting formatting : ChatFormatting.values()) {
            if (formatting.isColor()) {
                suggestions.add(formatting.getName());
            }
        }
        return suggestions.toArray(new String[0]);
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("notify")
            .requires(src -> src.hasPermission(OP_PERMISSION_LEVEL));

        RequiredArgumentBuilder<CommandSourceStack, EntitySelector> targets =
            Commands.argument("targets", EntityArgument.players());

        attachMessageNodes(targets, false);

        LiteralArgumentBuilder<CommandSourceStack> serverLiteral = Commands.literal("server");
        attachMessageNodes(serverLiteral, true);
        targets.then(serverLiteral);

        LiteralArgumentBuilder<CommandSourceStack> feedbackLiteral = Commands.literal("feedback")
            .then(Commands.argument("enabled", BoolArgumentType.bool())
                .executes(ctx -> {
                    feedbackEnabled = BoolArgumentType.getBool(ctx, "enabled");
                    ctx.getSource().sendSuccess(() -> Component.literal(
                        "Notify command feedback " + (feedbackEnabled ? "enabled" : "disabled") + "."), false);
                    return 1;
                }));

        dispatcher.register(root.then(targets).then(feedbackLiteral));
    }

    // /notify <targets> [color X|hex X] <message>                                    -> no title, message renders centered
    // /notify <targets> title <title> [color X|hex X] [msgcolor X|msghex X] <message> -> title + message, each independently colorable
    private static void attachMessageNodes(ArgumentBuilder<CommandSourceStack, ?> parent, boolean fromServer) {
        attachContentNodes(parent, fromServer, false);

        RequiredArgumentBuilder<CommandSourceStack, String> titleArg =
            Commands.argument("title", StringArgumentType.string());
        attachContentNodes(titleArg, fromServer, true);
        parent.then(Commands.literal("title").then(titleArg));
    }

    private static void attachContentNodes(ArgumentBuilder<CommandSourceStack, ?> parent, boolean fromServer, boolean hasTitle) {
        // No color specified at all: title (if any) and message both use the default color.
        parent.then(Commands.argument("message", StringArgumentType.greedyString())
            .executes(ctx -> send(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"),
                extractTitle(ctx, hasTitle), StringArgumentType.getString(ctx, "message"),
                DEFAULT_COLOR, DEFAULT_COLOR, fromServer)));

        RequiredArgumentBuilder<CommandSourceStack, String> colorArg = Commands.argument("color", StringArgumentType.word())
            .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(NAMED_COLOR_SUGGESTIONS, builder))
            .then(Commands.argument("message", StringArgumentType.greedyString())
                .executes(ctx -> {
                    int c = parseNamedColor(StringArgumentType.getString(ctx, "color"));
                    return send(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"),
                        extractTitle(ctx, hasTitle), StringArgumentType.getString(ctx, "message"), c, c, fromServer);
                }));
        if (hasTitle) {
            attachMessageColorFollowUps(colorArg, fromServer,
                ctx -> parseNamedColor(StringArgumentType.getString(ctx, "color")));
        }
        parent.then(Commands.literal("color").then(colorArg));

        RequiredArgumentBuilder<CommandSourceStack, String> hexArg = Commands.argument("hex", StringArgumentType.word())
            .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(HEX_VALUE_SUGGESTIONS, builder))
            .then(Commands.argument("message", StringArgumentType.greedyString())
                .executes(ctx -> {
                    int c = parseHexColor(StringArgumentType.getString(ctx, "hex"));
                    return send(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"),
                        extractTitle(ctx, hasTitle), StringArgumentType.getString(ctx, "message"), c, c, fromServer);
                }));
        if (hasTitle) {
            attachMessageColorFollowUps(hexArg, fromServer,
                ctx -> parseHexColor(StringArgumentType.getString(ctx, "hex")));
        }
        parent.then(Commands.literal("hex").then(hexArg));

        if (hasTitle) {
            // Title left at the default color, only the message gets an explicit color.
            attachMessageColorFollowUps(parent, fromServer, ctx -> DEFAULT_COLOR);
        }
    }

    /**
     * Adds "msgcolor <color> <message>" and "msghex <hex> <message>" as follow-ups to a node
     * that has already resolved (or defaulted) the title's color, letting the message be given
     * its own independent color instead of always matching the title.
     */
    private static void attachMessageColorFollowUps(ArgumentBuilder<CommandSourceStack, ?> parent, boolean fromServer,
                                                      TitleColorResolver titleColor) {
        parent.then(Commands.literal("msgcolor")
            .then(Commands.argument("msgColor", StringArgumentType.word())
                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(NAMED_COLOR_SUGGESTIONS, builder))
                .then(Commands.argument("message", StringArgumentType.greedyString())
                    .executes(ctx -> send(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"),
                        extractTitle(ctx, true), StringArgumentType.getString(ctx, "message"),
                        titleColor.resolve(ctx), parseNamedColor(StringArgumentType.getString(ctx, "msgColor")), fromServer)))));

        parent.then(Commands.literal("msghex")
            .then(Commands.argument("msgHex", StringArgumentType.word())
                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(HEX_VALUE_SUGGESTIONS, builder))
                .then(Commands.argument("message", StringArgumentType.greedyString())
                    .executes(ctx -> send(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"),
                        extractTitle(ctx, true), StringArgumentType.getString(ctx, "message"),
                        titleColor.resolve(ctx), parseHexColor(StringArgumentType.getString(ctx, "msgHex")), fromServer)))));
    }

    @FunctionalInterface
    private interface TitleColorResolver {
        int resolve(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException;
    }

    @Nullable
    private static String extractTitle(CommandContext<CommandSourceStack> ctx, boolean hasTitle) {
        return hasTitle ? StringArgumentType.getString(ctx, "title") : null;
    }

    private static int send(CommandSourceStack src, Collection<ServerPlayer> targets, @Nullable String title, String text, int titleColor, int messageColor, boolean fromServer) {
        ServerNotificationPacket packet = new ServerNotificationPacket(title, text, titleColor, messageColor, fromServer);
        for (ServerPlayer player : targets) {
            ServerNotificationNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player), packet);
        }

        int count = targets.size();
        if (feedbackEnabled) {
            src.sendSuccess(() -> Component.literal("Sent notification to " + count
                + (count == 1 ? " player" : " players")), true);
        }
        return count;
    }

    private static int parseNamedColor(String input) throws CommandSyntaxException {
        ChatFormatting named = ChatFormatting.getByName(input);
        if (named == null || !named.isColor()) {
            throw ERROR_INVALID_NAMED_COLOR.create(input);
        }
        return named.getColor();
    }

    private static int parseHexColor(String input) throws CommandSyntaxException {
        if (!HEX_PATTERN.matcher(input).matches()) {
            throw ERROR_INVALID_HEX_COLOR.create(input);
        }
        String clean = input.startsWith("#") ? input.substring(1) : input;
        return Integer.parseInt(clean, 16) & 0xFFFFFF;
    }
}
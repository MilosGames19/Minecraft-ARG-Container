package net.mcreator.minecraftalphaargmod.client.console;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@OnlyIn(Dist.CLIENT)
public class AlphaCommandManager {

    public static final AlphaCommandManager INSTANCE = new AlphaCommandManager();

    public static final Set<String> BLACKLISTED_COMMANDS = Set.of(
        "sv_allowrnet"
    );

    public String currentInput = "";

    private int suggestionIndex = -1;
    private String inputBeforeTab = null;
    private List<Suggestion> cachedSuggestions = new ArrayList<>();

    private AlphaCommandManager() {}

    public void appendChar(char c) {
        currentInput += c;
        resetTabState();
    }

    public void backspace() {
        if (!currentInput.isEmpty()) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        }
        resetTabState();
    }

    public void clearInput() {
        currentInput = "";
        resetTabState();
    }

    private void resetTabState() {
        suggestionIndex = -1;
        inputBeforeTab = null;
        cachedSuggestions = new ArrayList<>();
    }

    public static boolean isBlacklisted(String input) {
        String query = input.trim();
        if (query.startsWith("/")) query = query.substring(1);
        if (query.isEmpty()) return false;
        String root = query.split("\\s+")[0].toLowerCase();
        return BLACKLISTED_COMMANDS.contains(root);
    }

    public void tabComplete() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) return;

        if (inputBeforeTab == null) {
            inputBeforeTab = currentInput;
            cachedSuggestions = fetchRawSuggestions(currentInput);
            suggestionIndex = -1;
        }

        if (cachedSuggestions.isEmpty()) return;

        suggestionIndex = (suggestionIndex + 1) % cachedSuggestions.size();
        Suggestion suggestion = cachedSuggestions.get(suggestionIndex);

        String prefix = inputBeforeTab.startsWith("/") ? "/" : "";
        String query = inputBeforeTab.startsWith("/") ? inputBeforeTab.substring(1) : inputBeforeTab;
        String before = query.substring(0, suggestion.getRange().getStart());
        currentInput = prefix + before + suggestion.getText();
    }

    public void execute() {
        String cmd = currentInput.trim();
        currentInput = "";
        resetTabState();
        if (cmd.isEmpty()) return;
        String commandStr = cmd.startsWith("/") ? cmd.substring(1) : cmd;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.getConnection() != null) {
            mc.player.connection.sendCommand(commandStr);
        }
    }

    public boolean isCompleteCommand(String input) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) return false;
        String query = input.trim();
        if (query.startsWith("/")) query = query.substring(1);
        if (query.isEmpty()) return false;

        StringReader reader = new StringReader(query);
        ParseResults<SharedSuggestionProvider> parsed = mc.getConnection().getCommands()
                .parse(reader, mc.player.connection.getSuggestionsProvider());

        return parsed.getExceptions().isEmpty()
                && !parsed.getReader().canRead()
                && parsed.getContext().getCommand() != null;
    }

    public List<String> getDisplaySuggestions() {
        if (!cachedSuggestions.isEmpty()) {
            List<String> result = new ArrayList<>();
            for (Suggestion s : cachedSuggestions) {
                result.add(s.getText());
            }
            return result;
        }
        return getSuggestions(currentInput);
    }

    public int getSuggestionIndex() {
        return suggestionIndex;
    }

    public List<String> getSuggestions(String input) {
        List<String> result = new ArrayList<>();
        for (Suggestion s : fetchRawSuggestions(input)) {
            result.add(s.getText());
        }
        return result;
    }

    private List<Suggestion> fetchRawSuggestions(String input) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) return new ArrayList<>();
        String query = input.startsWith("/") ? input.substring(1) : input;
        try {
            StringReader reader = new StringReader(query);
            ParseResults<SharedSuggestionProvider> parsed = mc.getConnection().getCommands()
                    .parse(reader, mc.player.connection.getSuggestionsProvider());
            Suggestions suggestions = mc.getConnection().getCommands()
                    .getCompletionSuggestions(parsed, query.length()).get();
            return new ArrayList<>(suggestions.getList());
        } catch (InterruptedException | ExecutionException e) {
            return new ArrayList<>();
        }
    }
}
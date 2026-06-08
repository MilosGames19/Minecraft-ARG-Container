package net.mcreator.minecraftalphaargmod.mixins;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.mcreator.minecraftalphaargmod.client.console.AlphaCommandManager;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.client.gui.components.CommandSuggestions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(CommandSuggestions.class)
public class CommandSuggestionFilterMixin {

    @Inject(method = "sortSuggestions", at = @At("RETURN"), cancellable = true)
    private void onSortSuggestions(Suggestions suggestions, CallbackInfoReturnable<List<Suggestion>> cir) {
        List<Suggestion> original = cir.getReturnValue();
        if (original == null || original.isEmpty()) return;

        List<Suggestion> filtered = original.stream()
                .filter(s -> {
                    String root = s.getText().split("\\s+")[0];
                    return !AlphaCommandManager.isBlacklisted("/" + root);
                })
                .collect(Collectors.toList());

        if (filtered.size() != original.size()) {
            cir.setReturnValue(filtered);
        }
    }

    @Inject(method = "formatText", at = @At("HEAD"), cancellable = true)
    private static void onFormatText(
            ParseResults<SharedSuggestionProvider> parse,
            String input,
            int offset,
            CallbackInfoReturnable<FormattedCharSequence> cir) {

        if (input == null || input.isEmpty()) return;
        String query = input.startsWith("/") ? input.substring(1) : input;
        String root = query.split("\\s+")[0];
        if (!root.isEmpty() && AlphaCommandManager.isBlacklisted("/" + root)) {
            cir.setReturnValue(FormattedCharSequence.forward(input, Style.EMPTY));
        }
    }
}
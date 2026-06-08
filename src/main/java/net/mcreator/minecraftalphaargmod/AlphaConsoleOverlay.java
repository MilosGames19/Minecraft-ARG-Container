package net.mcreator.minecraftalphaargmod.client.console;

import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AlphaConsoleOverlay {

    public static final AlphaConsoleOverlay INSTANCE = new AlphaConsoleOverlay();

    private AlphaConsoleOverlay() {}

    public static void drawConsole(GuiGraphics guiGraphics, int screenWidth, int screenHeight, Minecraft mc, boolean focused) {
        AlphaCommandManager manager = AlphaCommandManager.INSTANCE;

        guiGraphics.fillGradient(1, 1, screenWidth - 1, 13, -266724838, -1608902118);
        guiGraphics.fillGradient(2, 2, screenWidth - 2, 12, -265080013, -1607257293);

        int promptWidth = mc.font.width("3emj MC16.05 >");
        guiGraphics.drawString(mc.font, "3emj MC16.05 >", 3, 3, 16187136, false);
        guiGraphics.drawString(mc.font,
                manager.currentInput + (focused ? "_" : ""),
                8 + promptWidth, 3, 0xFFFFFF, false);

        String input = manager.currentInput;
        if (input.isEmpty()) return;

        List<String> suggestions = manager.getDisplaySuggestions();
        int selectedIdx = manager.getSuggestionIndex();
        boolean isComplete = manager.isCompleteCommand(input);

        if (!suggestions.isEmpty()) {
            int rowH = 10;
            int listTop = 16;
            int bottomPadding = 18;
            int availablePixels = screenHeight - listTop - bottomPadding;
            int maxRows = Math.max(1, availablePixels / rowH);
            int visibleCount = Math.min(suggestions.size(), maxRows);

            int hintRows = isComplete || suggestions.size() > visibleCount ? 1 : 0;
            int boxH = (visibleCount + hintRows) * rowH;

            guiGraphics.fillGradient(1, 14, screenWidth - 1, 14 + boxH + 8, -1608902118, -266724838);
            guiGraphics.fillGradient(2, 15, screenWidth - 2, 15 + boxH + 6, -1607257293, -265080013);

            for (int i = 0; i < visibleCount; i++) {
                boolean isSelected = (i == selectedIdx);
                int color = isSelected ? 0xFFFFFF : 0xAABBAA;
                String label = (isSelected ? "§f> " : "  ") + suggestions.get(i);
                guiGraphics.drawString(mc.font, label, 4, listTop + i * rowH, color, false);
            }

            int hintY = listTop + visibleCount * rowH + 1;
            if (suggestions.size() > visibleCount) {
                guiGraphics.drawString(mc.font,
                        "§7... " + (suggestions.size() - visibleCount) + " more (Tab to cycle)",
                        4, hintY, 0x606060, false);
            } else if (isComplete) {
                guiGraphics.drawString(mc.font,
                        "§7Tab: complete  §8|  §7Enter: run now",
                        4, hintY, 0x606060, false);
            }

        } else if (isComplete) {
            guiGraphics.fillGradient(1, 14, screenWidth - 1, 24, -1608902118, -266724838);
            guiGraphics.fillGradient(2, 15, screenWidth - 2, 23, -1607257293, -265080013);
            guiGraphics.drawString(mc.font, "§aPress Enter to execute", 4, 15, 0x55FF55, false);
        }
    }
}
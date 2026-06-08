package net.mcreator.minecraftalphaargmod.client.console;

import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class AlphaConsoleScreen extends Screen {

    public AlphaConsoleScreen() {
        super(Component.empty());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (McconfigConfiguration.ALPHAVER_CONSOLE.get()) {
            AlphaConsoleOverlay.drawConsole(guiGraphics, width, height, minecraft, true);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            AlphaCommandManager.INSTANCE.execute();
            onClose();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            AlphaCommandManager.INSTANCE.backspace();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_TAB) {
            AlphaCommandManager.INSTANCE.tabComplete();
            return true;
        }
        return true;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        AlphaCommandManager.INSTANCE.appendChar(codePoint);
        return true;
    }
}
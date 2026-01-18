package net.mcreator.minecraftalphaargmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class QuickloadButtonHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuickloadButtonHandler.class);
    private static final int ROW_SPACING = 4;
    private static final int BUTTON_HEIGHT = 20;
    private static final int QUICK_BUTTON_MIN_WIDTH = 60;
    private static final int QUICK_BUTTON_MAX_WIDTH = 90;
    private static final int QUICK_BUTTON_PADDING = 12;
    private static final String QUICK_BUTTON_TEXT = "Quickload";
    private static final AtomicBoolean isLoading = new AtomicBoolean(false);

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof TitleScreen)) return;
        
        // Reset loading state whenever the title screen is initialized.
        isLoading.set(false);

        if (!McconfigConfiguration.QUICKLOAD_BUTTON.get()) return;

        Screen screen = event.getScreen();
        Minecraft mc = Minecraft.getInstance();

        Optional<LevelSummary> lastWorld = getLastPlayedWorld(mc);
        if (lastWorld.isEmpty()) return;

        String singleplayerButtonText = Component.translatable("menu.singleplayer").getString();
        Optional<Button> singleplayerButton = event.getListenersList().stream()
            .filter(widget -> widget instanceof Button)
            .map(widget -> (Button) widget)
            .filter(button -> button.getMessage() != null &&
                            button.getMessage().getString().equals(singleplayerButtonText))
            .findFirst();

        if (singleplayerButton.isEmpty()) return;

        Button spButton = singleplayerButton.get();
        int quickloadWidth = calculateQuickloadButtonWidth(mc);
        
        // Resize singleplayer FIRST
        int newSpWidth = spButton.getWidth() - quickloadWidth - ROW_SPACING;
        spButton.setWidth(newSpWidth);
        
        // Position quickload based on NEW singleplayer width
        int quickloadX = spButton.getX() + newSpWidth + ROW_SPACING;
        int quickloadY = spButton.getY();

        Tooltip tooltip = Tooltip.create(Component.literal(
            String.format("Load: \"%s\"", lastWorld.get().getLevelName())));

        Button quickloadButton = Button.builder(Component.literal(QUICK_BUTTON_TEXT),
            button -> {
                if (!isLoading.compareAndSet(false, true)) {
                    return; // Already loading
                }
                button.active = false;
                loadLastWorld(mc, screen, lastWorld.get(), button);
            })
            .bounds(quickloadX, quickloadY, quickloadWidth, BUTTON_HEIGHT)
            .tooltip(tooltip)
            .build();

        event.addListener(quickloadButton);
    }

    private static int calculateQuickloadButtonWidth(Minecraft mc) {
        int textWidth = mc.font.width(QUICK_BUTTON_TEXT) + QUICK_BUTTON_PADDING;
        return Math.max(QUICK_BUTTON_MIN_WIDTH, Math.min(QUICK_BUTTON_MAX_WIDTH, textWidth));
    }

    private static Optional<LevelSummary> getLastPlayedWorld(Minecraft mc) {
        try {
            LevelStorageSource levelSource = mc.getLevelSource();
            if (levelSource == null) return Optional.empty();

            LevelStorageSource.LevelCandidates candidates = levelSource.findLevelCandidates();
            if (candidates == null) return Optional.empty();

            return levelSource.loadLevelSummaries(candidates)
                .join()
                .stream()
                .filter(summary -> !summary.isLocked())
                .max(Comparator.comparingLong(LevelSummary::getLastPlayed));
        } catch (Exception e) {
            LOGGER.error("Failed to load world summaries", e);
            return Optional.empty();
        }
    }

    private static void loadLastWorld(Minecraft mc, Screen parent, LevelSummary levelSummary, Button button) {
        if (levelSummary.isLocked()) {
            LOGGER.error("World is locked: {}", levelSummary.getLevelId());
            mc.setScreen(new SelectWorldScreen(parent));
            resetLoadingState(button);
            return;
        }
        mc.setScreen(new DelayedLoadScreen(parent, levelSummary, button));
    }

    private static void resetLoadingState(Button button) {
        isLoading.set(false);
        if (button != null) {
            button.active = true;
        }
    }

    private static class DelayedLoadScreen extends Screen {
        private final Screen parent;
        private final LevelSummary levelSummary;
        private final Button quickloadButton;
        private int ticksExisted = 0;

        protected DelayedLoadScreen(Screen parent, LevelSummary levelSummary, Button quickloadButton) {
            super(Component.literal("Reading world data..."));
            this.parent = parent;
            this.levelSummary = levelSummary;
            this.quickloadButton = quickloadButton;
        }

        @Override
        public void tick() {
            super.tick();
            this.ticksExisted++;

            // Trigger the load on the second tick. This ensures the screen has had a chance to render at least once.
            if (this.ticksExisted == 2) {
                try {
                    assert this.minecraft != null;
                    this.minecraft.createWorldOpenFlows().loadLevel(this.parent, this.levelSummary.getLevelId());
                } catch (Exception e) {
                    LOGGER.error("Failed to load world: {}", this.levelSummary.getLevelId(), e);
                    assert this.minecraft != null;
                    this.minecraft.execute(() -> {
                        this.minecraft.setScreen(new SelectWorldScreen(this.parent));
                        resetLoadingState(this.quickloadButton);
                    });
                }
            }
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.render(guiGraphics, mouseX, mouseY, partialTick);
            this.renderBackground(guiGraphics);
            guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 4 - 10, 0xFFFFFF);
        }

        @Override
        public boolean shouldCloseOnEsc() {
            return false;
        }
    }
}

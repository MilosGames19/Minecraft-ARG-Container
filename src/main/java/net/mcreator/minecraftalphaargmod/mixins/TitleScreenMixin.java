package net.mcreator.minecraftalphaargmod.mixins;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    private static final Logger LOGGER = LoggerFactory.getLogger(TitleScreenMixin.class);
    
    // Layout constants
    private static final int TOTAL_ROW_WIDTH = 200;
    private static final int ROW_SPACING = 4;
    private static final int BUTTON_HEIGHT = 20;
    private static final int QUICK_BUTTON_MIN_WIDTH = 60;
    private static final int QUICK_BUTTON_MAX_WIDTH = 90;
    private static final int QUICK_BUTTON_PADDING = 12;
    private static final String QUICK_BUTTON_TEXT = "Quickload";

    private Button quickloadButton;
    private int cachedQuickloadTextWidth = -1;
    private final AtomicBoolean isLoading = new AtomicBoolean(false);

    protected TitleScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "createNormalMenuOptions", at = @At("TAIL"))
    private void addQuickloadButton(int y, int spacingY, CallbackInfo ci) {
        if (!isInitializationValid()) {
            return;
        }

        Optional<LevelSummary> lastWorld = getLastPlayedWorld();
        if (lastWorld.isPresent()) {
            createQuickloadButton(y, lastWorld.get());
        }
        
        adjustSingleplayerButton(y, lastWorld.isPresent());
    }

    private boolean isInitializationValid() {
        return this.minecraft != null && 
               this.font != null && 
               McconfigConfiguration.QUICKLOAD_BUTTON.get();
    }

    private void createQuickloadButton(int y, LevelSummary lastWorld) {
        int quickloadWidth = calculateQuickloadButtonWidth();
        int[] buttonPositions = calculateButtonPositions(quickloadWidth);
        
        Tooltip tooltip = Tooltip.create(Component.literal(
            String.format("Load: \"%s\"", lastWorld.getLevelName())));

        this.quickloadButton = Button.builder(Component.literal(QUICK_BUTTON_TEXT), 
            button -> {
                if (!isLoading.get()) {
                    isLoading.set(true);
                    button.active = false;
                    loadLastWorld(lastWorld, button);
                }
            })
            .bounds(buttonPositions[1], y, quickloadWidth, BUTTON_HEIGHT)
            .tooltip(tooltip)
            .build();

        this.addRenderableWidget(this.quickloadButton);
    }

    private int calculateQuickloadButtonWidth() {
        if (cachedQuickloadTextWidth == -1) {
            cachedQuickloadTextWidth = this.font.width(QUICK_BUTTON_TEXT) + QUICK_BUTTON_PADDING;
        }
        return Math.max(QUICK_BUTTON_MIN_WIDTH, 
               Math.min(QUICK_BUTTON_MAX_WIDTH, cachedQuickloadTextWidth));
    }

    private int[] calculateButtonPositions(int quickloadWidth) {
        int centerX = this.width / 2;
        int rowLeft = centerX - (TOTAL_ROW_WIDTH / 2);
        int singleplayerWidth = TOTAL_ROW_WIDTH - ROW_SPACING - quickloadWidth;
        
        return new int[] {
            rowLeft,                              // Singleplayer X
            rowLeft + singleplayerWidth + ROW_SPACING  // Quickload X
        };
    }

    private void adjustSingleplayerButton(int y, boolean hasQuickload) {
        int singleplayerWidth = hasQuickload ? 
            TOTAL_ROW_WIDTH - ROW_SPACING - calculateQuickloadButtonWidth() : 
            TOTAL_ROW_WIDTH;
            
        int[] positions = calculateButtonPositions(calculateQuickloadButtonWidth());
        
        this.renderables.stream()
            .filter(widget -> widget instanceof Button)
            .map(widget -> (Button) widget)
            .filter(button -> button.getMessage() != null &&
                            button.getMessage().getString().contains("Singleplayer"))
            .findFirst()
            .ifPresent(button -> {
                button.setX(positions[0]);
                button.setY(y);
                button.setWidth(singleplayerWidth);
                button.setHeight(BUTTON_HEIGHT);
            });
    }

    private Optional<LevelSummary> getLastPlayedWorld() {
        try {
            if (this.minecraft == null) {
                return Optional.empty();
            }

            LevelStorageSource levelSource = this.minecraft.getLevelSource();
            if (levelSource == null) {
                return Optional.empty();
            }

            LevelStorageSource.LevelCandidates candidates = levelSource.findLevelCandidates();
            if (candidates == null) {
                return Optional.empty();
            }

            return levelSource.loadLevelSummaries(candidates)
                .join()
                .stream()
                .filter(summary -> !summary.isLocked()) // Only consider unlocked worlds
                .max(Comparator.comparingLong(LevelSummary::getLastPlayed));
        } catch (Exception e) {
            LOGGER.error("Failed to load world summaries", e);
            return Optional.empty();
        }
    }

    private void loadLastWorld(LevelSummary levelSummary, Button button) {
        if (minecraft == null || levelSummary == null) {
            LOGGER.error("Cannot load world - minecraft or levelSummary is null");
            resetLoadingState(button);
            return;
        }

        // Check if world is already locked
        try {
            if (levelSummary.isLocked()) {
                LOGGER.error("World is locked: {}", levelSummary.getLevelId());
                minecraft.setScreen(new SelectWorldScreen(this));
                resetLoadingState(button);
                return;
            }

            // Show loading message
            minecraft.setScreen(new Screen(Component.translatable("selectWorld.loading")) {
                @Override
                protected void init() {}
            });

            // Attempt to load the world
            try {
                minecraft.createWorldOpenFlows().loadLevel(this, levelSummary.getLevelId());
            } catch (Exception e) {
                LOGGER.error("Failed to load world: {}", levelSummary.getLevelId(), e);
                minecraft.execute(() -> {
                    minecraft.setScreen(new SelectWorldScreen(this));
                    resetLoadingState(button);
                });
            }
        } catch (Exception e) {
            LOGGER.error("Critical error loading world: {}", levelSummary.getLevelId(), e);
            minecraft.execute(() -> {
                minecraft.setScreen(new SelectWorldScreen(this));
                resetLoadingState(button);
            });
        }
    }

    private void resetLoadingState(Button button) {
        isLoading.set(false);
        if (button != null) {
            button.active = true;
        }
    }
}
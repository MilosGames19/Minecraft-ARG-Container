package net.mcreator.minecraftalphaargmod.mixins;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components. Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin. Mixin;
import org.spongepowered.asm. mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;

import java.util. Comparator;
import java.util. Optional;
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
        if (!lastWorld.isPresent()) {
            adjustSingleplayerButton(y, false);
            return;
        }

        // Try to find the existing Singleplayer button so we can anchor next to it. 
        Optional<Button> singleplayerButton = this.renderables.stream()
            .filter(widget -> widget instanceof Button)
            .map(widget -> (Button) widget)
            .filter(button -> {
                if (button.getMessage() == null) return false;
                // Use contains to be resilient across translations
                return button.getMessage().getString().contains("Singleplayer");
            })
            .findFirst();

        if (singleplayerButton.isPresent()) {
            createQuickloadButtonAnchored(singleplayerButton.get(), lastWorld.get());
            // After creating anchored quickload, ensure the singleplayer width is reduced if needed
            adjustAnchoredSingleplayer(singleplayerButton.get());
        } else {
            // fallback to original centered row logic
            createQuickloadButton(y, lastWorld.get());
            adjustSingleplayerButton(y, true);
        }
    }

    private boolean isInitializationValid() {
        return this.minecraft != null &&
               this.font != null &&
               McconfigConfiguration. QUICKLOAD_BUTTON. get();
    }

    /**
     * Create a Quickload button anchored to the given singleplayer Button. 
     * The quickload is placed to the right of the anchor and vertically aligned with it.
     */
    private void createQuickloadButtonAnchored(Button anchor, LevelSummary lastWorld) {
        int quickloadWidth = calculateQuickloadButtonWidth();

        // Position quickload directly to the right of the singleplayer anchor
        int quickloadX = anchor.getX() + anchor.getWidth() + ROW_SPACING;
        int quickloadY = anchor.getY();

        Tooltip tooltip = Tooltip.create(Component.literal(
            String.format("Load:  \"%s\"", lastWorld.getLevelName())));

        this.quickloadButton = Button.builder(Component.literal(QUICK_BUTTON_TEXT),
            button -> {
                if (!isLoading.get()) {
                    isLoading.set(true);
                    button.active = false;
                    loadLastWorld(lastWorld, button);
                }
            })
            .bounds(quickloadX, quickloadY, quickloadWidth, BUTTON_HEIGHT)
            .tooltip(tooltip)
            .build();

        this.addRenderableWidget(this.quickloadButton);
    }

    /**
     * Original fallback:  create the quickload button using the row layout parameters.
     */
    private void createQuickloadButton(int y, LevelSummary lastWorld) {
        int quickloadWidth = calculateQuickloadButtonWidth();
        int[] buttonPositions = calculateButtonPositions(quickloadWidth);

        Tooltip tooltip = Tooltip.create(Component.literal(
            String. format("Load: \"%s\"", lastWorld.getLevelName())));

        this.quickloadButton = Button.builder(Component.literal(QUICK_BUTTON_TEXT),
            button -> {
                if (!isLoading.get()) {
                    isLoading. set(true);
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

    /**
     * Fallback adjustment when we couldn't find the Singleplayer button earlier.
     * This modifies the Singleplayer button in the renderables list to make room visually.
     */
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

    /**
     * Adjust the anchored singleplayer button width so the Quickload fits to its right. 
     * If reducing the width would make the singleplayer button too small, clamp it.
     */
    private void adjustAnchoredSingleplayer(Button singleplayer) {
        if (this.quickloadButton == null || singleplayer == null) return;

        int quickloadWidth = this.quickloadButton.getWidth();

        int availableWidth = singleplayer.getWidth();
        int newSingleplayerWidth = availableWidth - quickloadWidth - ROW_SPACING;

        // ensure the singleplayer button remains reasonably wide
        int minSingleplayerWidth = 80; // arbitrary reasonable minimum
        if (newSingleplayerWidth < minSingleplayerWidth) {
            // If there isn't enough width, try to shift quickload left to sit immediately after singleplayer
            int desiredQuickX = singleplayer.getX() + singleplayer.getWidth() + ROW_SPACING;
            int maxRight = this.width - quickloadWidth - 10; // keep some margin
            int finalQuickX = Math.min(desiredQuickX, maxRight);
            this.quickloadButton.setX(finalQuickX);
            // leave singleplayer width unchanged if shrinking would be too small
            singleplayer.setHeight(BUTTON_HEIGHT);
        } else {
            singleplayer.setWidth(newSingleplayerWidth);
            singleplayer.setHeight(BUTTON_HEIGHT);
            // Ensure quickload is directly to the right of the resized singleplayer
            this.quickloadButton.setX(singleplayer.getX() + singleplayer.getWidth() + ROW_SPACING);
            this.quickloadButton.setY(singleplayer.getY());
        }
    }

    private Optional<LevelSummary> getLastPlayedWorld() {
        try {
            if (this.minecraft == null) {
                return Optional.empty();
            }

            LevelStorageSource levelSource = this.minecraft.getLevelSource();
            if (levelSource == null) {
                return Optional. empty();
            }

            LevelStorageSource.LevelCandidates candidates = levelSource.findLevelCandidates();
            if (candidates == null) {
                return Optional.empty();
            }

            return levelSource.loadLevelSummaries(candidates)
                .join()
                .stream()
                .filter(summary -> ! summary.isLocked()) // Only consider unlocked worlds
                .max(Comparator.comparingLong(LevelSummary:: getLastPlayed));
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
                LOGGER.error("World is locked: {}", levelSummary. getLevelId());
                minecraft.setScreen(new SelectWorldScreen(this));
                resetLoadingState(button);
                return;
            }

            // Show loading screen with proper rendering
            Screen loadingScreen = new Screen(Component.literal("Reading world data...")) {
                @Override
                protected void init() {
                    super.init();
                }

                @Override
                public void render(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                    this.renderBackground(guiGraphics);
                    
                    // Draw the loading text in the center of the screen
                    int textWidth = this.font.width(this.title);
                    guiGraphics.drawCenteredString(this.font, this.title, 
                        this.width / 2, this. height / 4 - 10, 0xFFFFFF);
                }

                @Override
                public boolean shouldCloseOnEsc() {
                    return false; // Prevent closing with ESC during loading
                }
            };
            minecraft.setScreen(loadingScreen);

            // Load the world in the background
            new Thread(() -> {
                try {
                    minecraft.createWorldOpenFlows().loadLevel(this, levelSummary. getLevelId());
                } catch (Exception e) {
                    LOGGER.error("Failed to load world: {}", levelSummary.getLevelId(), e);
                    minecraft.execute(() -> {
                        minecraft.setScreen(new SelectWorldScreen(this));
                        resetLoadingState(button);
                    });
                }
            }).start();
        } catch (Exception e) {
            LOGGER.error("Critical error loading world:  {}", levelSummary.getLevelId(), e);
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
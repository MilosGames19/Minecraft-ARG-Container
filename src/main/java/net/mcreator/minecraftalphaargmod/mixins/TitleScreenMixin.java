package net.mcreator.minecraftalphaargmod.mixins;

import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    private Button quickloadButton;

    protected TitleScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "createNormalMenuOptions", at = @At("TAIL"))
    private void addQuickloadButton(int y, int spacingY, CallbackInfo ci) {
        if (this.minecraft == null || this.font == null || !McconfigConfiguration.QUICKLOAD_BUTTON.get()) {
            return;
        }

        LevelSummary lastWorld = getLastPlayedWorld();
        boolean hasLastWorld = lastWorld != null;

        // Standard vanilla row: 200px centered, with typical 4px spacing when split
        final int totalRowWidth = 200;
        final int rowSpacing = 4;

        // Make Quickload compact; compute from text with padding and clamp to a small range
        final String quickText = "Quickload";
        final int quickPadding = 12;
        final int quickTextWidth = this.font.width(quickText) + quickPadding;
        final int quickMin = 60;
        final int quickMax = 90;
        final int quickWidth = Math.max(quickMin, Math.min(quickMax, quickTextWidth));

        // If we have a last world, split the row; otherwise let Singleplayer have the full 200px
        int singleWidth = hasLastWorld ? (totalRowWidth - rowSpacing - quickWidth) : totalRowWidth;

        int centerX = this.width / 2;
        int rowLeft = centerX - (totalRowWidth / 2);

        int singleX = rowLeft;
        int quickX = rowLeft + singleWidth + rowSpacing;
        int height = 20;

        // Adjust the existing Singleplayer button to the new bounds
        modifySingleplayerButton(singleX, y, singleWidth, height);

        // Only add the Quickload button when a last world exists; otherwise keep the row clean and full-width
        if (hasLastWorld) {
            Tooltip tooltip = Tooltip.create(Component.literal("Load: \"" + lastWorld.getLevelName() + "\""));

            this.quickloadButton = Button.builder(Component.literal(quickText), button -> {
                    loadLastWorld(Objects.requireNonNull(lastWorld).getLevelId());
                })
                .bounds(quickX, y, quickWidth, height)
                .tooltip(tooltip)
                .build();

            this.quickloadButton.active = true;
            this.addRenderableWidget(this.quickloadButton);
        }
    }

    private void modifySingleplayerButton(int x, int y, int width, int height) {
        // Find and modify the Singleplayer button bounds
        this.renderables.forEach(widget -> {
            if (widget instanceof Button button) {
                Component message = button.getMessage();
                if (message != null && message.getString().contains("Singleplayer")) {
                    button.setX(x);
                    button.setY(y);
                    button.setWidth(width);
                    button.setHeight(height);
                }
            }
        });
    }

    private LevelSummary getLastPlayedWorld() {
        try {
            LevelStorageSource levelSource = this.minecraft.getLevelSource();
            LevelStorageSource.LevelCandidates candidates = levelSource.findLevelCandidates();
            List<LevelSummary> worlds = levelSource.loadLevelSummaries(candidates).join();

            LevelSummary lastPlayed = null;
            long lastPlayTime = Long.MIN_VALUE;

            for (LevelSummary world : worlds) {
                if (world.getLastPlayed() > lastPlayTime) {
                    lastPlayTime = world.getLastPlayed();
                    lastPlayed = world;
                }
            }
            return lastPlayed;
        } catch (Exception e) {
            return null;
        }
    }

    private void loadLastWorld(String worldId) {
        try {
            this.minecraft.createWorldOpenFlows().loadLevel(this, worldId);
        } catch (Exception e) {
            this.minecraft.setScreen(new SelectWorldScreen(this));
        }
    }
}

package net.mcreator.minecraftalphaargmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PlayersNearbyScreen extends Screen {
    
    private final Screen parent;
    private final List<PlayerEntry> playerEntries = new ArrayList<>();
    private int scrollOffset = 0;
    private static final int ENTRY_HEIGHT = 24;
    private static final int VISIBLE_ENTRIES = 8;

    public PlayersNearbyScreen(Screen parent) {
        super(Component.literal("Players Nearby"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        
        this.addRenderableWidget(Button.builder(Component.literal("Back"), 
            button -> {
                if (this.minecraft != null) {
                    this.minecraft.setScreen(parent);
                }
            })
            .bounds(this.width / 2 - 100, this.height - 30, 200, 20)
            .build());
        
        updatePlayerList();
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.minecraft != null && this.minecraft.level != null && this.minecraft.level.getGameTime() % 20 == 0) {
            updatePlayerList();
        }
    }

    private void updatePlayerList() {
        playerEntries.clear();
        Minecraft mc = Minecraft.getInstance();
        
        if (mc.level == null || mc.player == null) return;
        
        // Iterate over loaded players (nearby) instead of all online players
        for (Player player : mc.level.players()) {
            UUID uuid = player.getUUID();
            String username = player.getGameProfile().getName();
            
            double distance = mc.player.distanceTo(player);
            boolean isAdmin = player.hasPermissions(4);
            boolean isLocal = player.getUUID().equals(mc.player.getUUID());
            
            playerEntries.add(new PlayerEntry(username, uuid, distance, isLocal, isAdmin));
        }
        
        playerEntries.sort(Comparator.comparingDouble(e -> e.distance));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        
        int listWidth = 300;
        int listHeight = VISIBLE_ENTRIES * ENTRY_HEIGHT;
        int listX = this.width / 2 - listWidth / 2;
        int startY = 40;
        
        guiGraphics.fill(listX - 2, startY - 2, listX + listWidth + 2, 
            startY + listHeight + 2, 0x80000000);
        guiGraphics.renderOutline(listX - 2, startY - 2, listWidth + 4, listHeight + 4, 0xFFFFFFFF);
        
        for (int i = 0; i < Math.min(VISIBLE_ENTRIES, playerEntries.size()); i++) {
            int index = i + scrollOffset;
            if (index >= playerEntries.size()) break;
            
            PlayerEntry entry = playerEntries.get(index);
            int y = startY + (i * ENTRY_HEIGHT);
            int x = listX;
            
            if (entry.isLocal) {
                guiGraphics.fill(x, y, x + listWidth, y + ENTRY_HEIGHT, 0x20FFFFFF);
            }
            
            ResourceLocation skin = DefaultPlayerSkin.getDefaultSkin(entry.uuid);
            if (this.minecraft != null && this.minecraft.getConnection() != null) {
                PlayerInfo info = this.minecraft.getConnection().getPlayerInfo(entry.uuid);
                if (info != null) skin = info.getSkinLocation();
            }
            
            guiGraphics.blit(skin, x + 4, y + 4, 16, 16, 8, 8, 8, 8, 64, 64);
            guiGraphics.blit(skin, x + 4, y + 4, 16, 16, 40, 8, 8, 8, 64, 64);
            
            int color = entry.isLocal ? 0x55FF55 : 0xFFFFFF;
            
            String displayText = entry.username;

            if (entry.isAdmin) {
                displayText += " \u00A76[Admin]"; 
            }

            guiGraphics.drawString(this.font, displayText, x + 25, y + 8, color);

            if (!entry.isLocal && entry.distance >= 0) {
                String distStr = String.format("%.1f m", entry.distance);
                int distWidth = this.font.width(distStr);
                guiGraphics.drawString(this.font, distStr, x + listWidth - distWidth - 5, y + 8, 0xAAAAAA);
            }
        }
        
        if (scrollOffset > 0) {
            guiGraphics.drawCenteredString(this.font, "▲", this.width / 2, startY - 12, 0xFFFFFF);
        }
        if (scrollOffset + VISIBLE_ENTRIES < playerEntries.size()) {
            guiGraphics.drawCenteredString(this.font, "▼", this.width / 2, 
                startY + listHeight + 5, 0xFFFFFF);
        }
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int maxScroll = Math.max(0, playerEntries.size() - VISIBLE_ENTRIES);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int) delta));
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof PauseScreen pauseScreen) {
            Button feedbackButton = null;
            for (var widget : pauseScreen.renderables) {
                if (widget instanceof Button btn) {
                    if (btn.getMessage().getString().equals("Give Feedback")) {
                        feedbackButton = btn;
                        break;
                    }
                }
            }
            
            if (feedbackButton != null) {
                int x = feedbackButton.getX();
                int y = feedbackButton.getY();
                int width = feedbackButton.getWidth();
                int height = feedbackButton.getHeight();
                
                pauseScreen.renderables.remove(feedbackButton);
                pauseScreen.children().remove(feedbackButton);
                
                event.addListener(Button.builder(Component.literal("Players Nearby"), 
                    button -> Minecraft.getInstance().setScreen(new PlayersNearbyScreen(pauseScreen)))
                    .bounds(x, y, width, height)
                    .build());
            }
        }
    }

    private static class PlayerEntry {
        String username;
        UUID uuid;
        double distance;
        boolean isLocal;
        boolean isAdmin;

        PlayerEntry(String username, UUID uuid, double distance, boolean isLocal, boolean isAdmin) {
            this.username = username;
            this.uuid = uuid;
            this.distance = distance;
            this.isLocal = isLocal;
            this.isAdmin = isAdmin;
        }
    }
}
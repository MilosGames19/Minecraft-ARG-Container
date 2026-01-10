package net.mcreator.minecraftalphaargmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PlayersNearbyScreen extends Screen {
    
    private final Screen parent;
    private List<PlayerEntry> playerEntries = new ArrayList<>();
    private int scrollOffset = 0;
    private static final int ENTRY_HEIGHT = 12;
    private static final int VISIBLE_ENTRIES = 10;

    public PlayersNearbyScreen(Screen parent) {
        super(Component.literal("Players Nearby"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        
        this.addRenderableWidget(Button.builder(Component.literal("Back"), 
            button -> this.minecraft.setScreen(parent))
            .bounds(this.width / 2 - 100, this.height - 30, 200, 20)
            .build());
        
        updatePlayerList();
    }

    private void updatePlayerList() {
        playerEntries.clear();
        Minecraft mc = Minecraft.getInstance();
        
        if (mc.level == null || mc.player == null) return;
        
        if (mc.getConnection() != null) {
            for (PlayerInfo info : mc.getConnection().getOnlinePlayers()) {
                String username = info.getProfile().getName();
                Player player = mc.level.getPlayerByUUID(info.getProfile().getId());
                
                double distance = -1;
                boolean isAdmin = false;

                if (player != null && mc.player != null) {
                    distance = mc.player.distanceTo(player);
                    if (player.hasPermissions(4)) {
                        isAdmin = true;
                    }
                }
                
                boolean isLocal = username.equals(mc.player.getName().getString());
                
                playerEntries.add(new PlayerEntry(username, distance, isLocal, isAdmin));
            }
        }
        
        playerEntries.sort(Comparator.comparingDouble(e -> e.distance < 0 ? Double.MAX_VALUE : e.distance));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        
        updatePlayerList();
        
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        
        int startY = 50;
        int listWidth = 300;
        int listX = this.width / 2 - listWidth / 2;
        
        guiGraphics.fill(listX - 2, startY - 2, listX + listWidth + 2, 
            startY + (VISIBLE_ENTRIES * ENTRY_HEIGHT) + 2, 0x80000000);
        
        for (int i = 0; i < Math.min(VISIBLE_ENTRIES, playerEntries.size()); i++) {
            int index = i + scrollOffset;
            if (index >= playerEntries.size()) break;
            
            PlayerEntry entry = playerEntries.get(index);
            int y = startY + (i * ENTRY_HEIGHT);
            
            int color = entry.isLocal ? 0x55FF55 : 0xFFFFFF;
            
            String displayText = entry.username;

            if (entry.isAdmin) {
                displayText += "§6 👑"; 
            }

            if (!entry.isLocal && entry.distance >= 0) {
                displayText += " - " + String.format("%.1f", entry.distance) + "m";
            }
            
            guiGraphics.drawString(this.font, displayText, listX + 5, y + 2, color);
        }
        
        
        if (scrollOffset > 0) {
            guiGraphics.drawCenteredString(this.font, "▲", this.width / 2, startY - 15, 0xFFFFFF);
        }
        if (scrollOffset + VISIBLE_ENTRIES < playerEntries.size()) {
            guiGraphics.drawCenteredString(this.font, "▼", this.width / 2, 
                startY + (VISIBLE_ENTRIES * ENTRY_HEIGHT) + 5, 0xFFFFFF);
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
        double distance;
        boolean isLocal;
        boolean isAdmin;

        PlayerEntry(String username, double distance, boolean isLocal, boolean isAdmin) {
            this.username = username;
            this.distance = distance;
            this.isLocal = isLocal;
            this.isAdmin = isAdmin;
        }
    }
}
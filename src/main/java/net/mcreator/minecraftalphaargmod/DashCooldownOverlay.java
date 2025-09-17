package net.mcreator.minecraftalphaargmod.client;

// Alphaver code

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mcreator.minecraftalphaargmod.TheArgContainerMod;
import net.mcreator.minecraftalphaargmod.network.TheArgContainerModVariables;

@Mod.EventBusSubscriber(modid = TheArgContainerMod.MODID,
                        bus = Mod.EventBusSubscriber.Bus.FORGE,
                        value = Dist.CLIENT)
public class DashCooldownOverlay {
    
    private static final int MAX_DASH = 60;

    @SubscribeEvent
    public static void renderDashOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.level == null || !(mc.player instanceof LocalPlayer)) {
            return;
        }

        LocalPlayer player = (LocalPlayer) mc.player;
       
        int dashTimer = Math.max(
            player.getPersistentData().getInt("dashTimer"),
            (int) Math.round(
                player.getCapability(TheArgContainerModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                      .map(cap -> cap.Cooldown)
                      .orElse(0.0))
        );
        if (dashTimer <= 0) {
            return;
        }

        GuiGraphics gui = event.getGuiGraphics();
        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();

       
        int bw = 100;      // width
        int bh = 5;        // height
        int x  = sw / 2 - bw / 2;
        int y  = sh - 90;

       
        gui.fillGradient(
            x - 1, y - 1, x + bw + 1, y + bh + 1,
            /* start */ 0xFF202020,
            /* end   */ 0xFF000000
        );

       
        int startColor = dashTimer < 15 ? -16735489 : -3584;
        int endColor   = dashTimer < 15 ? -16759637 : -13893888;

        
        float pct   = 1.0f - (dashTimer / (float) MAX_DASH);
        int fillW   = (int) (bw * pct);

        if (fillW > 0) {
            gui.fillGradient(
                x, y, x + fillW, y + bh,
                /* start */ startColor,
                /* end   */ endColor
            );
        }
    }
}

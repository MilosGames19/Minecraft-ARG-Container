package net.mcreator.minecraftalphaargmod.client;

// Alphaver code

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mcreator.minecraftalphaargmod.TheArgContainerMod;
import net.mcreator.minecraftalphaargmod.network.TheArgContainerModVariables;

@Mod.EventBusSubscriber(modid = TheArgContainerMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class DashClientTicker {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        Player player = mc.player;

       
        int cap = (int) Math.round(player.getCapability(TheArgContainerModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .map(capVar -> capVar.Cooldown).orElse(0.0));
        int nbt = player.getPersistentData().getInt("dashTimer");

        
        if (cap > nbt) {
            player.getPersistentData().putInt("dashTimer", cap);
            return; 
        }

        
        if (nbt > 0) {
            player.getPersistentData().putInt("dashTimer", nbt - 1);
        }
    }
}

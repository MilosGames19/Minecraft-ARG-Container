package net.mcreator.minecraftalphaargmod.capes;

import net.mcreator.minecraftalphaargmod.capes.network.CapeNetwork;
import net.mcreator.minecraftalphaargmod.capes.network.CapeUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = "the_arg_container")
public class CapeSyncEvents {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        broadcastCapeUpdate(player);
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (!(event.getTarget() instanceof ServerPlayer target)) return;
        if (!(event.getEntity() instanceof ServerPlayer viewer)) return;

        String capeId = PlayerCapeData.getSelectedCape(target);
        // if (capeId == null) return;
        CapeNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), CapeUpdatePacket.of(target, capeId));
    }

    public static void broadcastCapeUpdate(ServerPlayer player) {
        String capeId = PlayerCapeData.getSelectedCape(player);
        // if (capeId == null) return;
        CapeNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), CapeUpdatePacket.of(player, capeId));
    }
}
package net.mcreator.minecraftalphaargmod.capes.client;

import net.mcreator.minecraftalphaargmod.capes.CapeDefinition;
import net.mcreator.minecraftalphaargmod.capes.CapeRegistry;
import net.mcreator.minecraftalphaargmod.capes.network.OpenCapeSelectorPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@OnlyIn(Dist.CLIENT)
public class ClientCapeCache {

    // private static final Map<UUID, ResourceLocation> ACTIVE_CAPES = new HashMap<>();
    private static final Map<UUID, ResourceLocation> ACTIVE_CAPES = new ConcurrentHashMap<>();

    private static UUID previewOwner;
    private static ResourceLocation previewTexture;

    public static ResourceLocation getCape(UUID playerUuid) {
        if (playerUuid.equals(previewOwner)) {
            return previewTexture;
        }
        return ACTIVE_CAPES.get(playerUuid);
    }

    public static void updateCape(UUID playerUuid, String capeId) {
        if (capeId == null) {
            ACTIVE_CAPES.remove(playerUuid);
            return;
        }

        CapeDefinition definition = CapeRegistry.getById(capeId);
        if (definition == null) {
            ACTIVE_CAPES.remove(playerUuid);
            return;
        }

        ACTIVE_CAPES.put(playerUuid, definition.getTexture());
    }

    public static void setPreview(UUID playerUuid, ResourceLocation texture) {
        previewOwner = playerUuid;
        previewTexture = texture;
    }

    public static void clearPreview() {
        previewOwner = null;
        previewTexture = null;
    }

    public static void openSelector(OpenCapeSelectorPacket packet) {
        Minecraft.getInstance().setScreen(new CapeSelectorScreen(packet));
    }
}
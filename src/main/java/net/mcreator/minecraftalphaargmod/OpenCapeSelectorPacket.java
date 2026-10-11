package net.mcreator.minecraftalphaargmod.capes.network;

import net.mcreator.minecraftalphaargmod.capes.CapeCategory;
import net.mcreator.minecraftalphaargmod.capes.CapeDefinition;
import net.mcreator.minecraftalphaargmod.capes.client.ClientCapeCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class OpenCapeSelectorPacket {

    private static final int MAX_CAPES = 1024;

    private final List<Entry> capes;
    private final String selectedCapeId;
    private final boolean testMode;

    public OpenCapeSelectorPacket(List<Entry> capes, String selectedCapeId, boolean testMode) {
        this.capes = capes;
        this.selectedCapeId = selectedCapeId;
        this.testMode = testMode;
    }

    public static OpenCapeSelectorPacket of(List<CapeDefinition> definitions, String selectedCapeId, boolean testMode) {
        List<Entry> entries = new ArrayList<>();
        for (CapeDefinition definition : definitions) {
            entries.add(new Entry(definition.getId(), definition.getTexture(), definition.getDisplayName(), definition.getCategory()));
        }
        return new OpenCapeSelectorPacket(entries, selectedCapeId, testMode);
    }

    public static void encode(OpenCapeSelectorPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.capes.size());
        for (Entry entry : packet.capes) {
            buf.writeUtf(entry.getId());
            buf.writeResourceLocation(entry.getTexture());
            buf.writeUtf(entry.getDisplayName());
            buf.writeEnum(entry.getCategory());
        }
        buf.writeBoolean(packet.selectedCapeId != null);
        if (packet.selectedCapeId != null) {
            buf.writeUtf(packet.selectedCapeId);
        }
        buf.writeBoolean(packet.testMode);
    }

    public static OpenCapeSelectorPacket decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        if (size < 0 || size > MAX_CAPES) {
            throw new IllegalStateException("OpenCapeSelectorPacket: invalid cape count " + size);
        }
        List<Entry> entries = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String id = buf.readUtf();
            ResourceLocation texture = buf.readResourceLocation();
            String displayName = buf.readUtf();
            CapeCategory category = buf.readEnum(CapeCategory.class);
            entries.add(new Entry(id, texture, displayName, category));
        }
        String selectedCapeId = buf.readBoolean() ? buf.readUtf() : null;
        boolean testMode = buf.readBoolean();
        return new OpenCapeSelectorPacket(entries, selectedCapeId, testMode);
    }

    public static void handle(OpenCapeSelectorPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientCapeCache.openSelector(packet)));
        ctx.get().setPacketHandled(true);
    }

    public List<Entry> getCapes() {
        return capes;
    }

    public String getSelectedCapeId() {
        return selectedCapeId;
    }

    public boolean isTestMode() {
        return testMode;
    }

    public static class Entry {
        private final String id;
        private final ResourceLocation texture;
        private final String displayName;
        private final CapeCategory category;

        public Entry(String id, ResourceLocation texture, String displayName, CapeCategory category) {
            this.id = id;
            this.texture = texture;
            this.displayName = displayName;
            this.category = category;
        }

        public String getId() {
            return id;
        }

        public ResourceLocation getTexture() {
            return texture;
        }

        public String getDisplayName() {
            return displayName;
        }

        public CapeCategory getCategory() {
            return category;
        }
    }
}
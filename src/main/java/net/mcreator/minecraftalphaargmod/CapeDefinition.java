package net.mcreator.minecraftalphaargmod.capes;

import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class CapeDefinition {

    private final String id;
    private final ResourceLocation texture;
    private final String displayName;
    private final CapeCategory category;
    private final UUID ownerUuid;

    public CapeDefinition(String id, ResourceLocation texture, String displayName, CapeCategory category, UUID ownerUuid) {
        this.id = id;
        this.texture = texture;
        this.displayName = displayName;
        this.category = category;
        this.ownerUuid = ownerUuid;
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

    public UUID getOwnerUuid() {
        return ownerUuid;
    }
}
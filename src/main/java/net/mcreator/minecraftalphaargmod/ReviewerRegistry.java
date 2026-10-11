package net.mcreator.minecraftalphaargmod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ReviewerRegistry {

    private static final Set<UUID> REVIEWER_UUIDS = new HashSet<>();
    private static final Set<String> REVIEWER_NAMES = new HashSet<>();
    private static boolean locked = false;

    static {
    	REVIEWER_UUIDS.add(UUID.fromString("5b038e68-8f03-42b8-907e-f2e576c49917")); //KidneyStoneMan (@GravelLad)
		REVIEWER_UUIDS.add(UUID.fromString("8bee4c13-033a-4bf0-b07c-070c659ec764")); //TheSquareDude 
		REVIEWER_UUIDS.add(UUID.fromString("dcd6cb45-0c14-4db0-b2db-850613bc4564")); //Deshy The Wise
        // REVIEWER_UUIDS.add(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    public static void addReviewer(UUID uuid) {
        if (locked) throw new IllegalStateException("The registry is locked.");
        REVIEWER_UUIDS.add(uuid);
    }

    public static void addReviewer(String username) {
        if (locked) throw new IllegalStateException("The registry is locked.");
        REVIEWER_NAMES.add(username);
    }

    public static void lock() {
        locked = true;
    }

    public static boolean isReviewer(ServerPlayer player) {
        if (REVIEWER_UUIDS.contains(player.getUUID())) return true;
        if (!FMLEnvironment.production && REVIEWER_NAMES.contains(player.getName().getString())) return true;
        return false;
    }
}
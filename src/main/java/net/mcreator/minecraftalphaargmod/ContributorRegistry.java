package net.mcreator.minecraftalphaargmod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ContributorRegistry {

    private static final Set<UUID> CONTRIBUTOR_UUIDS = new HashSet<>();
    private static final Set<String> CONTRIBUTOR_NAMES = new HashSet<>();
    private static boolean locked = false;

    static {
		CONTRIBUTOR_UUIDS.add(UUID.fromString("b7c088fc-4232-4ead-a6fc-0fd06ecd0476")); //Mark101
		CONTRIBUTOR_UUIDS.add(UUID.fromString("b7c088fc-4232-4ead-a6fc-0fd06ecd0476"));
        // CONTRIBUTOR_UUIDS.add(UUID.fromString("00000000-0000-0000-0000-000000000000")); //Username
    }

    public static void addContributor(UUID uuid) {
        if (locked) throw new IllegalStateException("The registry is locked.");
        CONTRIBUTOR_UUIDS.add(uuid);
    }

    public static void addContributor(String username) {
        if (locked) throw new IllegalStateException("The registry is locked.");
        CONTRIBUTOR_NAMES.add(username);
    }

    public static void lock() {
        locked = true;
    }

    public static boolean isContributor(ServerPlayer player) {
        if (CONTRIBUTOR_UUIDS.contains(player.getUUID())) return true;
        if (!FMLEnvironment.production && CONTRIBUTOR_NAMES.contains(player.getName().getString())) return true;
        return false;
    }
}
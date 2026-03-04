package net.mcreator.minecraftalphaargmod;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AdminCache {
    private static final Set<UUID> adminUUIDs = new HashSet<>();

    public static void setAdmins(Set<UUID> uuids) {
        adminUUIDs.clear();
        adminUUIDs.addAll(uuids);
    }

    public static boolean isAdmin(UUID uuid) {
        return adminUUIDs.contains(uuid);
    }

    public static void clear() {
        adminUUIDs.clear();
    }
}
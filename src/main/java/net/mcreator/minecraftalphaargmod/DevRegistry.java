package net.mcreator.minecraftalphaargmod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DevRegistry {

    private static final Set<UUID> DEV_UUIDS = new HashSet<>();
    private static final Set<String> DEV_NAMES = new HashSet<>();
    private static boolean locked = false;

    static {
        DEV_UUIDS.add(UUID.fromString("2d10b449-51f2-4d96-b2e3-c3bdc0332a81")); //Glichy_606
        DEV_UUIDS.add(UUID.fromString("2d92b5cf-d158-4b15-894a-f78d9e66e8a7")); //Nebula
        DEV_UUIDS.add(UUID.fromString("2f8aed5f-c430-4bd2-8ad6-cb29dd02648c")); //20_NickName_20
        DEV_UUIDS.add(UUID.fromString("e770e1d4-8b72-43cd-a354-d9a6658d7804")); //TheDivided
        //DEV_UUIDS.add(UUID.fromString("8966c3d7-f430-49a1-8e6e-5961ae51a2fc")); //Atom
        DEV_NAMES.add("Dev");
    }

    public static void addDev(UUID uuid) {
        if (locked) throw new IllegalStateException("DevRegistry is locked.");
        DEV_UUIDS.add(uuid);
    }

    public static void addDev(String username) {
        if (locked) throw new IllegalStateException("DevRegistry is locked.");
        DEV_NAMES.add(username);
    }

    public static void lock() {
        locked = true;
    }

    public static boolean isDev(ServerPlayer player) {
        if (DEV_UUIDS.contains(player.getUUID())) return true;
        if (!FMLEnvironment.production && DEV_NAMES.contains(player.getName().getString())) return true;
        return false;
    }
}
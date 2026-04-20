package net.mcreator.minecraftalphaargmod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;


public final class LootboxItemTier {

    private LootboxItemTier() {}


    private static final Set<String> LEGENDARY_MIN = Set.of(
            // Netherite materials
            "minecraft:netherite_ingot",
            "minecraft:netherite_scrap",
            "minecraft:ancient_debris",
            // Netherite tools
            "minecraft:netherite_sword",
            "minecraft:netherite_pickaxe",
            "minecraft:netherite_axe",
            "minecraft:netherite_shovel",
            "minecraft:netherite_hoe",
            // Netherite armour
            "minecraft:netherite_helmet",
            "minecraft:netherite_chestplate",
            "minecraft:netherite_leggings",
            "minecraft:netherite_boots",
            // Unique / ultra-rare
            "minecraft:beacon",
            "minecraft:dragon_egg",
            "minecraft:dragon_head",
            "minecraft:nether_star",
            "minecraft:elytra",
            "minecraft:enchanted_golden_apple"
    );

    private static final Set<String> EPIC_MIN = Set.of(
            // Diamond tools
            "minecraft:diamond_sword",
            "minecraft:diamond_pickaxe",
            "minecraft:diamond_axe",
            "minecraft:diamond_shovel",
            "minecraft:diamond_hoe",
            // Diamond armour
            "minecraft:diamond_helmet",
            "minecraft:diamond_chestplate",
            "minecraft:diamond_leggings",
            "minecraft:diamond_boots",
            // Special drops
            "minecraft:trident",
            "minecraft:totem_of_undying",
            "minecraft:shulker_shell",
            "minecraft:end_crystal",
            "minecraft:conduit",
            "minecraft:heart_of_the_sea",
            "minecraft:lodestone",
            // Music discs (rare drops)
            "minecraft:music_disc_13",
            "minecraft:music_disc_cat",
            "minecraft:music_disc_blocks",
            "minecraft:music_disc_chirp",
            "minecraft:music_disc_far",
            "minecraft:music_disc_mall",
            "minecraft:music_disc_mellohi",
            "minecraft:music_disc_stal",
            "minecraft:music_disc_strad",
            "minecraft:music_disc_ward",
            "minecraft:music_disc_11",
            "minecraft:music_disc_wait",
            "minecraft:music_disc_otherside",
            "minecraft:music_disc_5",
            "minecraft:music_disc_pigstep",
            "minecraft:music_disc_relic"
    );

    private static final Set<String> RARE_MIN = Set.of(
            // Gems and precious materials
            "minecraft:diamond",
            "minecraft:emerald",
            "minecraft:amethyst_shard",
            // Nether mid-tier
            "minecraft:blaze_rod",
            "minecraft:ghast_tear",
            "minecraft:magma_cream",
            // End material
            "minecraft:ender_pearl",
            "minecraft:eye_of_ender",
            "minecraft:chorus_fruit",
            "minecraft:popped_chorus_fruit",
            "minecraft:end_stone",
            "minecraft:purpur_block",
            // Rare mob drops
            "minecraft:wither_skeleton_skull",
            "minecraft:skeleton_skull",
            "minecraft:zombie_head",
            "minecraft:creeper_head",
            "minecraft:piglin_head",
            // Useful mid-tier
            "minecraft:golden_apple",
            "minecraft:golden_carrot",
            "minecraft:spyglass",
            "minecraft:echo_shard",
            "minecraft:recovery_compass",
            "minecraft:disc_fragment_5"
    );

    private static final Set<String> UNCOMMON_MIN = Set.of(
            // Metal ingots and minerals
            "minecraft:iron_ingot",
            "minecraft:gold_ingot",
            "minecraft:copper_ingot",
            "minecraft:lapis_lazuli",
            "minecraft:redstone",
            "minecraft:quartz",
            "minecraft:prismarine_shard",
            "minecraft:prismarine_crystals",
            // Iron tools and armour
            "minecraft:iron_sword",
            "minecraft:iron_pickaxe",
            "minecraft:iron_axe",
            "minecraft:iron_shovel",
            "minecraft:iron_hoe",
            "minecraft:iron_helmet",
            "minecraft:iron_chestplate",
            "minecraft:iron_leggings",
            "minecraft:iron_boots",
            // Gold tools and armour
            "minecraft:golden_sword",
            "minecraft:golden_pickaxe",
            "minecraft:golden_axe",
            "minecraft:golden_shovel",
            "minecraft:golden_hoe",
            "minecraft:golden_helmet",
            "minecraft:golden_chestplate",
            "minecraft:golden_leggings",
            "minecraft:golden_boots",
            // Useful early-mid items
            "minecraft:bow",
            "minecraft:crossbow",
            "minecraft:flint_and_steel",
            "minecraft:saddle",
            "minecraft:name_tag",
            "minecraft:lead",
            "minecraft:book",
            "minecraft:enchanted_book",
            "minecraft:experience_bottle",
            "minecraft:nether_brick",
            "minecraft:blaze_powder",
            "minecraft:spider_eye",
            "minecraft:fermented_spider_eye",
            "minecraft:slime_ball",
            "minecraft:honey_bottle",
            "minecraft:honeycomb",
            "minecraft:turtle_helmet",
            "minecraft:scute"
    );


    public static LootboxRarity getMinimumRarity(Item item) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        if (key == null) return LootboxRarity.COMMON;

        String id = key.toString(); 

        if (LEGENDARY_MIN.contains(id)) return LootboxRarity.LEGENDARY;
        if (EPIC_MIN.contains(id))      return LootboxRarity.EPIC;
        if (RARE_MIN.contains(id))      return LootboxRarity.RARE;
        if (UNCOMMON_MIN.contains(id))  return LootboxRarity.UNCOMMON;
        return LootboxRarity.COMMON;
    }


    public static boolean isAllowedForRarity(Item item, LootboxRarity rarity) {
        LootboxRarity minRarity = getMinimumRarity(item);
        return minRarity.ordinal() <= rarity.ordinal();
    }
}
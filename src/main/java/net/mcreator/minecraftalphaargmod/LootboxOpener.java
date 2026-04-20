package net.mcreator.minecraftalphaargmod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.ChatFormatting;

import java.util.stream.Collectors;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public final class LootboxOpener {
	private LootboxOpener() {
	}

	private static final Random RNG = new Random();
	private static final ResourceLocation ESSENCE_ID = new ResourceLocation("the_arg_container", "essence");

	public static void openForRarity(LootboxRarity rarity) {
		if (!canAfford(rarity))
			return;
		consumeEssence(rarity);
		ItemStack winner = pickWinner(rarity);
		Minecraft.getInstance().setScreen(new LootboxScreen(winner, rarity));
	}

	public static void openRandom() {
		LootboxRarity rarity = pickRarity();
		if (!canAfford(rarity)) {
			Minecraft mc = Minecraft.getInstance();
			if (mc.player != null) {
				mc.player.displayClientMessage(Component.literal("Not enough Essence! Need ").append(Component.literal(String.valueOf(rarity.price)).withStyle(ChatFormatting.YELLOW))
						.append(Component.literal(" for a " + rarity.displayName + " lootbox.").withStyle(rarity.color)), true);
			}
			return;
		}
		consumeEssence(rarity);
		ItemStack winner = pickWinner(rarity);
		Minecraft.getInstance().setScreen(new LootboxScreen(winner, rarity));
	}

	public static LootboxRarity pickRarity() {
		int total = LootboxRarity.totalWeight();
		int roll = RNG.nextInt(total);
		int cumulative = 0;
		for (LootboxRarity r : LootboxRarity.values()) {
			cumulative += r.weight;
			if (roll < cumulative)
				return r;
		}
		return LootboxRarity.COMMON;
	}

	public static ItemStack pickWinner(LootboxRarity rarity) {
		List<Item> pool = buildItemPoolForRarity(rarity);
		if (pool.isEmpty())
			pool = buildFullItemPool();
		Item item = pool.get(RNG.nextInt(pool.size()));
		int rawMax = new ItemStack(item).getMaxStackSize();
		int lo = Math.min(rarity.minAmount, rawMax);
		int hi = Math.min(rarity.maxAmount, rawMax);
		if (lo > hi)
			lo = hi;
		int count = (lo == hi) ? lo : lo + RNG.nextInt(hi - lo + 1);
		return new ItemStack(item, count);
	}

	public static List<Item> buildItemPoolForRarity(LootboxRarity rarity) {
		return buildFullItemPool().stream().filter(item -> LootboxItemTier.isAllowedForRarity(item, rarity)).collect(Collectors.toList());
	}

	public static List<Item> buildFullItemPool() {
		List<Item> pool = new ArrayList<>(ForgeRegistries.ITEMS.getValues());
		pool.removeIf(item -> item == Items.AIR);
		return pool;
	}

	private static Item getEssenceItem() {
		Item item = ForgeRegistries.ITEMS.getValue(ESSENCE_ID);
		return (item == null || item == Items.AIR) ? null : item;
	}

	public static boolean canAfford(LootboxRarity rarity) {
		Item essence = getEssenceItem();
		if (essence == null)
			return true;
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null)
			return false;
		return countEssenceInInventory(essence) >= rarity.price;
	}

	public static void consumeEssence(LootboxRarity rarity) {
		Item essence = getEssenceItem();
		if (essence == null)
			return;
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null)
			return;
		int remaining = rarity.price;
		for (ItemStack stack : mc.player.getInventory().items) {
			if (stack.getItem() == essence && remaining > 0) {
				int take = Math.min(remaining, stack.getCount());
				stack.shrink(take);
				remaining -= take;
				if (remaining <= 0)
					break;
			}
		}
	}

	private static int countEssenceInInventory(Item essence) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null)
			return 0;
		int total = 0;
		for (ItemStack stack : mc.player.getInventory().items) {
			if (stack.getItem() == essence)
				total += stack.getCount();
		}
		return total;
	}
}

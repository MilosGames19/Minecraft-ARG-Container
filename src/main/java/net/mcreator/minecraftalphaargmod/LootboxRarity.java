package net.mcreator.minecraftalphaargmod;

import net.minecraft.ChatFormatting;

public enum LootboxRarity {

	COMMON("Common", ChatFormatting.WHITE, 60, 1, 1, 8, 18, 28), 
	UNCOMMON("Uncommon", ChatFormatting.GREEN, 25, 3, 4, 16, 28, 42), 
	RARE("Rare", ChatFormatting.AQUA, 10, 8, 8, 32, 42, 62), 
	EPIC("Epic", ChatFormatting.DARK_PURPLE, 4, 20, 16, 48, 62, 90), 
	LEGENDARY("Legendary", ChatFormatting.GOLD, 1, 50, 32, 64, 90, 130);

	public final String displayName;
	public final ChatFormatting color;
	public final int weight;
	public final int price;
	public final int minAmount;
	public final int maxAmount;
	public final int minSpinSlots;
	public final int maxSpinSlots;

	LootboxRarity(String displayName, ChatFormatting color, int weight, int price, int minAmount, int maxAmount, int minSpinSlots, int maxSpinSlots) {
		this.displayName = displayName;
		this.color = color;
		this.weight = weight;
		this.price = price;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.minSpinSlots = minSpinSlots;
		this.maxSpinSlots = maxSpinSlots;
	}

	public static int totalWeight() {
		int t = 0;
		for (LootboxRarity r : values())
			t += r.weight;
		return t;
	}
}

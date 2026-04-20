package net.mcreator.minecraftalphaargmod;

import org.checkerframework.checker.units.qual.h;
import org.checkerframework.checker.units.qual.g;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;

@OnlyIn(Dist.CLIENT)
public class LootboxShopScreen extends Screen {
	private static final int CARD_W = 68;
	private static final int CARD_H = 122;
	private static final int CARD_GAP = 8;
	private static final int PAD_X = 14;
	private static final int HEADER_H = 44;
	private static final int FOOTER_H = 28;
	private static final int BUY_H = 22;
	private static final Item[] CARD_ICONS = {Items.COBBLESTONE, // Common
			Items.IRON_INGOT, // Uncommon
			Items.DIAMOND, // Rare
			Items.TOTEM_OF_UNDYING, // Epic
			Items.BEACON, // Legendary
	};
	//Currency
	private static final ResourceLocation ESSENCE_ID = new ResourceLocation("the_arg_container", "essence");
	private static final float OPEN_TICKS = 7f;
	private static final float HOVER_SPEED = 0.18f;
	private static final float CLICK_DECAY = 0.14f;
	private static final float ERR_DECAY = 0.07f;

	private float openAnim = 0f;
	private final float[] hoverAnim;
	private final float[] clickAnim;
	private final float[] errAnim;
	private final LootboxRarity[] rarities = LootboxRarity.values();
	private int panelX, panelY, panelW, panelH;
	private int[] cardLeftX;
	private int cardsTopY;

	public LootboxShopScreen() {
		super(Component.literal("Lootbox Shop"));
		int n = rarities.length;
		hoverAnim = new float[n];
		clickAnim = new float[n];
		errAnim = new float[n];
	}

	@Override
	protected void init() {
		int n = rarities.length;
		int cardsW = n * CARD_W + (n - 1) * CARD_GAP;
		panelW = cardsW + PAD_X * 2;
		panelH = HEADER_H + CARD_H + FOOTER_H;
		panelX = (width - panelW) / 2;
		panelY = (height - panelH) / 2;
		cardsTopY = panelY + HEADER_H;
		cardLeftX = new int[n];
		int startX = panelX + PAD_X;
		for (int i = 0; i < n; i++) {
			cardLeftX[i] = startX + i * (CARD_W + CARD_GAP);
		}

		addRenderableWidget(Button.builder(Component.literal("Close"), btn -> onClose()).pos(width / 2 - 40, panelY + HEADER_H + CARD_H + 4).size(80, 18).build());
	}

	@Override
	public void tick() {
		if (openAnim < 1f)
			openAnim = Math.min(1f, openAnim + 1f / OPEN_TICKS);
		for (int i = 0; i < rarities.length; i++) {
			if (clickAnim[i] > 0)
				clickAnim[i] = Math.max(0f, clickAnim[i] - CLICK_DECAY);
			if (errAnim[i] > 0)
				errAnim[i] = Math.max(0f, errAnim[i] - ERR_DECAY);
		}
	}

	@Override
	public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {

		float raw = Math.min(1f, openAnim + partialTick / OPEN_TICKS);
		float ease = easeOutCubic(raw);

		int slideY = (int) ((1f - ease) * 20);

		int overlayA = (int) (ease * 0x88);
		gfx.fill(0, 0, width, height, overlayA << 24);

		gfx.pose().pushPose();
		gfx.pose().translate(0, slideY, 0);

		int adjMouseY = mouseY - slideY;
		renderPanel(gfx);
		renderHeader(gfx, ease);
		for (int i = 0; i < rarities.length; i++) {
			renderCard(gfx, mouseX, adjMouseY, i, partialTick);
		}
		gfx.pose().popPose();

		super.render(gfx, mouseX, mouseY, partialTick);
	}

	private void renderPanel(GuiGraphics gfx) {
		int x = panelX, y = panelY, w = panelW, h = panelH;
		gfx.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF000000);

		gfx.fill(x, y, x + w, y + h, 0xF0080B14);

		gfx.fill(x, y, x + w, y + 2, 0x33FFFFFF);

		int sepY = panelY + HEADER_H - 2;
		gfx.fill(x + 4, sepY, x + w - 4, sepY + 1, 0x44FFFFFF);
	}

	private void renderHeader(GuiGraphics gfx, float fadeAlpha) {
		int alpha = (int) (fadeAlpha * 0xFF);
		int titleCol = blendAlpha(0xFFFFFFFF, alpha);
		int goldCol = blendAlpha(0xFFFFD700, alpha);

		gfx.drawString(font, Component.literal("Shop"), panelX + PAD_X, panelY + 8, titleCol, true);

		int essenceCount = countEssence();
		String essenceStr = String.valueOf(essenceCount);
		Item essenceItem = ForgeRegistries.ITEMS.getValue(ESSENCE_ID);
		int counterRight = panelX + panelW - PAD_X;
		int counterY = panelY + 7;
		if (essenceItem != null && essenceItem != Items.AIR) {

			int iconX = counterRight - 16;
			gfx.renderItem(new ItemStack(essenceItem), iconX, counterY);
			gfx.drawString(font, essenceStr, iconX - font.width(essenceStr) - 2, counterY + 4, goldCol, true);
		} else {

			String label = essenceStr + " Essence";
			gfx.drawString(font, label, counterRight - font.width(label), counterY + 4, goldCol, true);
		}
	}

	private void renderCard(GuiGraphics gfx, int mouseX, int mouseY, int idx, float partialTick) {
		LootboxRarity rarity = rarities[idx];
		int cx = cardLeftX[idx];
		int cy = cardsTopY;

		boolean isHover = mouseX >= cx && mouseX < cx + CARD_W && mouseY >= cy && mouseY < cy + CARD_H;

		hoverAnim[idx] += ((isHover ? 1f : 0f) - hoverAnim[idx]) * HOVER_SPEED;
		float hover = hoverAnim[idx];
		float click = clickAnim[idx];
		float err = errAnim[idx];

		float scale = 1f + hover * 0.035f - click * 0.03f;
		float centerCX = cx + CARD_W * 0.5f;
		float centerCY = cy + CARD_H * 0.5f;
		gfx.pose().pushPose();
		gfx.pose().translate(centerCX, centerCY, 0);
		gfx.pose().scale(scale, scale, 1f);
		gfx.pose().translate(-centerCX, -centerCY, 0);

		gfx.fill(cx, cy, cx + CARD_W, cy + CARD_H, 0xE8080C18);

		if (hover > 0.01f) {
			int rarityRgb = resolveRGB(rarity);
			int hoverAlpha = (int) (hover * 0x18);
			gfx.fill(cx, cy, cx + CARD_W, cy + CARD_H, (hoverAlpha << 24) | rarityRgb);
		}

		if (err > 0.01f) {
			int errAlpha = (int) (err * 0x44);
			gfx.fill(cx, cy, cx + CARD_W, cy + CARD_H, (errAlpha << 24) | 0xFF2222);
		}
		int rarityRgb = resolveRGB(rarity);
		int borderAlpha = (int) (0x99 + hover * 0x66);
		int borderColor = (borderAlpha << 24) | rarityRgb;

		gfx.fill(cx, cy, cx + CARD_W, cy + 3, borderColor);
		gfx.fill(cx, cy + 3, cx + 1, cy + CARD_H, borderColor);
		gfx.fill(cx + CARD_W - 1, cy + 3, cx + CARD_W, cy + CARD_H, borderColor);
		gfx.fill(cx, cy + CARD_H - 1, cx + CARD_W, cy + CARD_H, borderColor);

		int nameCol = resolveColourARGB(rarity);
		gfx.drawCenteredString(font, Component.literal(rarity.displayName).withStyle(rarity.color), cx + CARD_W / 2, cy + 6, nameCol);

		Item icon = CARD_ICONS[idx];
		int iconCX = cx + CARD_W / 2 - 16;
		int iconCY = cy + 22;
		gfx.pose().pushPose();
		gfx.pose().translate(iconCX, iconCY, 50f);
		gfx.pose().scale(2f, 2f, 2f);
		gfx.renderItem(new ItemStack(icon), 0, 0);
		gfx.pose().popPose();

		int sepY = cy + 60;
		gfx.fill(cx + 4, sepY, cx + CARD_W - 4, sepY + 1, 0x44FFFFFF);
		int priceY = sepY + 5;
		Item ess = ForgeRegistries.ITEMS.getValue(ESSENCE_ID);
		String pStr = "×" + rarity.price;
		int pTextW = font.width(pStr);
		if (ess != null && ess != Items.AIR) {
			int totalW = 16 + 2 + pTextW;
			int startPX = cx + (CARD_W - totalW) / 2;
			gfx.renderItem(new ItemStack(ess), startPX, priceY);
			gfx.drawString(font, pStr, startPX + 18, priceY + 4, 0xFFFFD700, true);
		} else {
			gfx.drawCenteredString(font, pStr, cx + CARD_W / 2, priceY + 4, 0xFFFFD700);
		}

		int buyY = cy + CARD_H - BUY_H - 3;
		boolean buyHover = isHover && mouseY >= buyY && mouseY < buyY + BUY_H;
		int buyBg = buyHover ? blendColor(0xFF1A3A1A, rarityRgb, 0.35f) : 0xFF0E1E0E;

		gfx.fill(cx + 3, buyY, cx + CARD_W - 3, buyY + BUY_H, buyBg);

		gfx.fill(cx + 3, buyY, cx + CARD_W - 3, buyY + 1, borderColor);
		gfx.fill(cx + 3, buyY + BUY_H - 1, cx + CARD_W - 3, buyY + BUY_H, borderColor);

		boolean canAfford = LootboxOpener.canAfford(rarity);
		int buyTextCol = canAfford ? (buyHover ? 0xFFFFFFFF : 0xFFCCFFCC) : 0xFF885555;
		gfx.drawCenteredString(font, canAfford ? "BUY" : "Need " + rarity.price, cx + CARD_W / 2, buyY + BUY_H / 2 - font.lineHeight / 2, buyTextCol);
		gfx.pose().popPose();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button != 0)
			return super.mouseClicked(mouseX, mouseY, button);
		for (int i = 0; i < rarities.length; i++) {
			int cx = cardLeftX[i];
			int cy = cardsTopY;
			int buyY = cy + CARD_H - BUY_H - 3;
			boolean inBuy = mouseX >= cx + 3 && mouseX < cx + CARD_W - 3 && mouseY >= buyY && mouseY < buyY + BUY_H;
			if (inBuy) {
				onBuyClicked(i);
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	private void onBuyClicked(int idx) {
		LootboxRarity rarity = rarities[idx];
		clickAnim[idx] = 1f;
		if (!LootboxOpener.canAfford(rarity)) {
			errAnim[idx] = 1f;

			Minecraft mc = Minecraft.getInstance();
			if (mc.player != null) {
				mc.player.displayClientMessage(Component.literal("Not enough Essence! Need " + rarity.price + "."), true);
			}
			return;
		}

		LootboxOpener.openForRarity(rarity);
	}

	private static float easeOutCubic(float t) {
		float f = 1f - t;
		return 1f - f * f * f;
	}

	private static int resolveColourARGB(LootboxRarity rarity) {
		Integer c = rarity.color.getColor();
		return c != null ? (0xFF000000 | c) : 0xFFFFFFFF;
	}

	private static int resolveRGB(LootboxRarity rarity) {
		Integer c = rarity.color.getColor();
		return c != null ? (c & 0xFFFFFF) : 0xFFFFFF;
	}

	private static int blendAlpha(int argb, int alpha) {
		return (argb & 0x00FFFFFF) | (alpha << 24);
	}

	private static int blendColor(int a, int b, float t) {
		int rA = (a >> 16) & 0xFF, gA = (a >> 8) & 0xFF, bA = a & 0xFF;
		int rB = (b >> 16) & 0xFF, gB = (b >> 8) & 0xFF, bB = b & 0xFF;
		int r = rA + (int) ((rB - rA) * t);
		int g = gA + (int) ((gB - gA) * t);
		int bl = bA + (int) ((bB - bA) * t);
		return 0xFF000000 | (r << 16) | (g << 8) | bl;
	}

	private static int countEssence() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null)
			return 0;
		Item ess = ForgeRegistries.ITEMS.getValue(ESSENCE_ID);
		if (ess == null || ess == Items.AIR)
			return 0;
		int total = 0;
		for (ItemStack stack : mc.player.getInventory().items) {
			if (stack.getItem() == ess)
				total += stack.getCount();
		}
		return total;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}

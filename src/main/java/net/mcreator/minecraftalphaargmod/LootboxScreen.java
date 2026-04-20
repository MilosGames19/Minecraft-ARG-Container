package net.mcreator.minecraftalphaargmod;

import org.checkerframework.checker.units.qual.s;

import net.minecraftforge.network.PacketDistributor;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;

import net.mcreator.minecraftalphaargmod.network.LootboxNetwork;
import net.mcreator.minecraftalphaargmod.network.GiveLootboxItemPacket;

import java.util.Random;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class LootboxScreen extends Screen {
	private static final int SLOT_SIZE = 48;
	private static final int VISIBLE_SLOTS = 9;
	private static final int DECEL_ZONE = 6;
	private static final int REEL_H = SLOT_SIZE + 14;
	private static final int CLOSE_DELAY = 80;
	private static final float OPEN_TICKS = 8f;
	private static final float REVEAL_TICKS = 20f;
	private static final float RESULT_TICKS = 12f;
	private static final int PANEL_BG = 0x060912;
	private static final int SLOT_BG = 0x0A0F1C;
	private final List<ItemStack> reelItems = new ArrayList<>();
	private final ItemStack winnerStack;
	private final LootboxRarity rarity;
	private final int rarityRgb;
	private int dynamicWinnerIndex;
	private float targetOffset;
	private float scrollOffset = 0f;
	private float scrollSpeed = 0f;
	private boolean spinning = false;
	private boolean finished = false;
	private int closeTimer = -1;
	private float openAnim = 0f;
	private float revealAnim = 0f;
	private float resultAnim = 0f;
	private float decelGlow = 0f;
	private int tickCount = 0;
	private final List<float[]> sparkles = new ArrayList<>();
	private static final Random SPARK_RNG = new Random();
	private int lastTickSlot = -1;
	private int autoStartDelay = 6;
	private float tickPitch = 0.5f;
	private int reelX, reelY, reelW, cx;

	public LootboxScreen(ItemStack winnerStack, LootboxRarity rarity) {
		super(Component.literal(rarity.displayName + " Lootbox"));
		this.winnerStack = winnerStack;
		this.rarity = rarity;
		Integer c = rarity.color.getColor();
		this.rarityRgb = c != null ? (c & 0xFFFFFF) : 0xFFFFFF;
	}

	@Override
	protected void init() {
		cx = width / 2;
		reelW = VISIBLE_SLOTS * SLOT_SIZE;
		reelX = cx - reelW / 2;
		reelY = height / 2 - 84;
		Random rng = new Random();
		int spinRange = rarity.maxSpinSlots - rarity.minSpinSlots;
		int extra = rarity.minSpinSlots + (spinRange > 0 ? rng.nextInt(spinRange + 1) : 0);
		dynamicWinnerIndex = extra + (VISIBLE_SLOTS / 2);
		targetOffset = (dynamicWinnerIndex - VISIBLE_SLOTS / 2) * (float) SLOT_SIZE;
		generateReel(rng, dynamicWinnerIndex, dynamicWinnerIndex + VISIBLE_SLOTS + 4);
	}

	private void generateReel(Random rng, int winnerIndex, int reelSize) {
		reelItems.clear();
		List<ItemStack> pool = buildVisualPool(rng);
		for (int i = 0; i < reelSize; i++) {
			if (i == winnerIndex) {
				reelItems.add(winnerStack.copy());
			} else {
				ItemStack pick = pool.get(rng.nextInt(pool.size())).copy();
				int ms = pick.getMaxStackSize();
				pick.setCount(1 + rng.nextInt(Math.max(1, ms / 4)));
				reelItems.add(pick);
			}
		}
	}

	private List<ItemStack> buildVisualPool(Random rng) {
		List<Item> all = LootboxOpener.buildItemPoolForRarity(rarity);
		Collections.shuffle(all, rng);
		int cap = Math.min(60, all.size());
		List<ItemStack> pool = new ArrayList<>(cap);
		for (int i = 0; i < cap; i++)
			pool.add(new ItemStack(all.get(i)));
		return pool;
	}

	private void startSpin() {
		if (spinning || finished)
			return;
		spinning = true;
		scrollOffset = 0f;
		scrollSpeed = 38f;
		tickPitch = 0.5f;
		lastTickSlot = -1;
	}

	@Override
	public void tick() {
		tickCount++;
		if (openAnim < 1f)
			openAnim = Math.min(1f, openAnim + 1f / OPEN_TICKS);
		if (!spinning && !finished && autoStartDelay > 0) {
			autoStartDelay--;
			if (autoStartDelay == 0)
				startSpin();
		}
		if (spinning) {
			scrollOffset += scrollSpeed;
			float remaining = targetOffset - scrollOffset;
			if (remaining < SLOT_SIZE * DECEL_ZONE) {
				scrollSpeed = Math.max(0.9f, scrollSpeed * 0.905f);
				decelGlow = Math.min(1f, decelGlow + 0.06f);
				tickPitch = Math.min(2.0f, tickPitch + 0.04f);
			}
			int currentSlot = (int) (scrollOffset / SLOT_SIZE);
			if (currentSlot != lastTickSlot) {
				lastTickSlot = currentSlot;
				playTickSound();
			}
			if (scrollOffset >= targetOffset) {
				scrollOffset = targetOffset;
				spinning = false;
				onSpinFinished();
			}
		}
		if (finished) {
			revealAnim = Math.min(1f, revealAnim + 1f / REVEAL_TICKS);
			resultAnim = Math.min(1f, resultAnim + 1f / RESULT_TICKS);
		}
		sparkles.removeIf(s -> s[4] <= 0f);
		for (float[] s : sparkles) {
			s[0] += s[2];
			s[1] += s[3];
			s[3] += 0.22f;
			s[4] -= 0.022f;
		}
		if (closeTimer > 0)
			closeTimer--;
		else if (closeTimer == 0)
			Minecraft.getInstance().setScreen(null);
	}

	private void onSpinFinished() {
		finished = true;
		closeTimer = CLOSE_DELAY;
		LootboxNetwork.CHANNEL.send(PacketDistributor.SERVER.noArg(), new GiveLootboxItemPacket(winnerStack.copy()));
		playStopSound();
		playWinSound();
		emitSparkles(cx, reelY + REEL_H + 62f);
	}

	private void playTickSound() {
		playUiSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.22f, tickPitch);
	}

	private void playStopSound() {
		playUiSound(SoundEvents.ANVIL_LAND, 0.15f, 1.8f);
	}

	private void playWinSound() {
		switch (rarity) {
			case COMMON -> playUiSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.6f, 1.0f);
			case UNCOMMON -> playUiSound(SoundEvents.PLAYER_LEVELUP, 0.5f, 0.9f);
			case RARE -> playUiSound(SoundEvents.PLAYER_LEVELUP, 0.7f, 1.2f);
			case EPIC -> playUiSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.0f);
			case LEGENDARY -> {
				playUiSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.3f);
				playUiSound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 0.6f, 0.8f);
			}
		}
	}

	private void playUiSound(SoundEvent event, float volume, float pitch) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null)
			return;
		mc.getSoundManager().play(SimpleSoundInstance.forUI(event, pitch, volume));
	}

	@Override
	public void render(GuiGraphics gfx, int mouseX, int mouseY, float partial) {
		float rawOpen = Math.min(1f, openAnim + partial / OPEN_TICKS);
		float easeOpen = easeOutCubic(rawOpen);
		int slideY = (int) ((1f - easeOpen) * 20);
		gfx.fill(0, 0, width, height, alpha(0x000000, (int) (easeOpen * 0xB0)));
		gfx.pose().pushPose();
		gfx.pose().translate(0, slideY, 0);
		renderBackground(gfx, easeOpen);
		renderTitle(gfx, easeOpen);
		renderReelTrack(gfx);
		gfx.enableScissor(reelX, reelY, reelX + reelW, reelY + REEL_H);
		renderReelItems(gfx, partial);
		gfx.disableScissor();
		renderReelEdgeFades(gfx);
		renderCenterHighlight(gfx);
		renderPointer(gfx);
		renderBigItem(gfx, partial);
		renderSparkles(gfx);
		if (finished)
			renderResult(gfx);
		gfx.pose().popPose();
		super.render(gfx, mouseX, mouseY, partial);
	}

	private void renderBackground(GuiGraphics gfx, float a) {
		int pt = reelY - 28, pb = reelY + REEL_H + 138;
		int pl = reelX - 10, pr = reelX + reelW + 10;
		gfx.fill(pl, pt, pr, pb, alpha(PANEL_BG, (int) (a * 0xE8)));
		gfx.fill(pl, pt, pr, pt + 3, alpha(rarityRgb, (int) (a * 0xCC)));
		gfx.fill(pl, pt, pr, pt + 1, alpha(rarityRgb, (int) (a * 0x66)));
		gfx.fill(pl, pb - 1, pr, pb, alpha(rarityRgb, (int) (a * 0x66)));
		gfx.fill(pl, pt, pl + 1, pb, alpha(rarityRgb, (int) (a * 0x66)));
		gfx.fill(pr - 1, pt, pr, pb, alpha(rarityRgb, (int) (a * 0x66)));
		gfx.fill(pl, pt + 3, pr, pt + 8, alpha(rarityRgb, (int) (a * 0x18)));
	}

	private void renderTitle(GuiGraphics gfx, float a) {
		int titleY = reelY - 20;
		String title = rarity.displayName.toUpperCase() + " LOOTBOX";
		int titleW = font.width(title);
		int lineY = titleY + font.lineHeight / 2;
		int lineLen = (reelW / 2 - titleW / 2 - 6 - 10);
		gfx.fill(cx - titleW / 2 - 6 - lineLen, lineY, cx - titleW / 2 - 6, lineY + 1, alpha(rarityRgb, (int) (a * 0x99)));
		gfx.fill(cx + titleW / 2 + 6, lineY, cx + titleW / 2 + 6 + lineLen, lineY + 1, alpha(rarityRgb, (int) (a * 0x99)));
		gfx.drawCenteredString(font, Component.literal(title).withStyle(rarity.color), cx, titleY, alpha(rarityRgb, (int) (a * 0xFF)));
	}

	private void renderReelTrack(GuiGraphics gfx) {
		gfx.fill(reelX, reelY, reelX + reelW, reelY + REEL_H, alpha(SLOT_BG, 0xFF));
		gfx.fill(reelX, reelY, reelX + reelW, reelY + 2, 0x44000000);
		gfx.fill(reelX, reelY + REEL_H - 2, reelX + reelW, reelY + REEL_H, 0x44000000);
		gfx.fill(reelX, reelY, reelX + reelW, reelY + 1, alpha(rarityRgb, 0x55));
		gfx.fill(reelX, reelY + REEL_H - 1, reelX + reelW, reelY + REEL_H, alpha(rarityRgb, 0x55));
	}

	private void renderReelItems(GuiGraphics gfx, float partial) {
		float offset = spinning ? Math.min(targetOffset, scrollOffset + partial * scrollSpeed) : scrollOffset;
		int firstIdx = (int) (offset / SLOT_SIZE);
		float pixelShift = offset % SLOT_SIZE;
		for (int i = 0; i <= VISIBLE_SLOTS + 1; i++) {
			int idx = firstIdx + i;
			if (idx < 0 || idx >= reelItems.size())
				continue;
			ItemStack stack = reelItems.get(idx);
			int slotX = reelX + (int) (i * SLOT_SIZE - pixelShift);
			gfx.fill(slotX + 2, reelY + 2, slotX + SLOT_SIZE - 2, reelY + REEL_H - 2, 0x660A0A18);
			gfx.pose().pushPose();
			gfx.pose().translate(slotX + SLOT_SIZE / 2 - 16, reelY + REEL_H / 2 - 16, 100f);
			gfx.pose().scale(2f, 2f, 2f);
			gfx.renderItem(stack, 0, 0);
			gfx.pose().popPose();
			if (stack.getCount() > 1) {
				String cnt = String.valueOf(stack.getCount());
				gfx.drawString(font, cnt, slotX + SLOT_SIZE - 3 - font.width(cnt), reelY + REEL_H - 11, 0xFFFFFFFF, true);
			}
			gfx.fill(slotX + SLOT_SIZE - 1, reelY + 4, slotX + SLOT_SIZE, reelY + REEL_H - 4, 0x22FFFFFF);
		}
	}

	private void renderReelEdgeFades(GuiGraphics gfx) {
		int fadeW = SLOT_SIZE + 8;
		for (int i = 0; i < fadeW; i++) {
			float t = 1f - (float) i / fadeW;
			int a = (int) (t * t * t * 0xF0);
			gfx.fill(reelX + i, reelY, reelX + i + 1, reelY + REEL_H, alpha(SLOT_BG, a));
			gfx.fill(reelX + reelW - i - 1, reelY, reelX + reelW - i, reelY + REEL_H, alpha(SLOT_BG, a));
		}
	}

	private void renderCenterHighlight(GuiGraphics gfx) {
		if (decelGlow <= 0.01f)
			return;
		int hw = SLOT_SIZE / 2 + 2;
		gfx.fill(cx - hw, reelY, cx + hw, reelY + REEL_H, alpha(rarityRgb, (int) (decelGlow * 0x28)));
	}

	private void renderPointer(GuiGraphics gfx) {
		float pulse = (float) Math.sin(tickCount * 0.18f) * 0.25f + 0.75f;
		int ba = spinning ? (int) (pulse * 0xFF) : 0xFF;
		gfx.fill(cx - 5, reelY - 6, cx + 5, reelY + REEL_H + 6, alpha(rarityRgb, (int) (ba * 0.15f)));
		gfx.fill(cx - 3, reelY - 6, cx + 3, reelY + REEL_H + 6, alpha(rarityRgb, (int) (ba * 0.30f)));
		gfx.fill(cx - 1, reelY - 8, cx + 1, reelY + REEL_H + 8, alpha(rarityRgb, ba));
		gfx.fill(cx - 3, reelY - 8, cx + 3, reelY - 7, alpha(rarityRgb, ba));
		gfx.fill(cx - 2, reelY - 7, cx + 2, reelY - 6, alpha(rarityRgb, ba));
		gfx.fill(cx - 3, reelY + REEL_H + 7, cx + 3, reelY + REEL_H + 8, alpha(rarityRgb, ba));
		gfx.fill(cx - 2, reelY + REEL_H + 6, cx + 2, reelY + REEL_H + 7, alpha(rarityRgb, ba));
	}

	private void renderBigItem(GuiGraphics gfx, float partial) {
		int itemCY = reelY + REEL_H + 62;
		if (!finished) {
			float bob = (float) Math.sin(tickCount * 0.15f) * 2f;
			int half = (16 * 3) / 2;
			gfx.pose().pushPose();
			gfx.pose().translate(cx - half, itemCY - half + (int) bob, 200f);
			gfx.pose().scale(3f, 3f, 3f);
			gfx.renderItem(new ItemStack(Items.CHEST), 0, 0);
			gfx.pose().popPose();
			return;
		}
		float t = Math.min(1f, revealAnim + (1f / REVEAL_TICKS) * partial);
		float scale = easeOutBack(t) * 4f;
		if (scale < 0.05f)
			return;
		int half = (int) (16f * scale / 2f);
		float gPulse = (float) (Math.sin(tickCount * 0.12f) * 0.2f + 0.8f);
		int gSize = half + 10;
		int gA = (int) (gPulse * 0x40);
		gfx.fill(cx - gSize, itemCY - gSize, cx + gSize, itemCY + gSize, alpha(rarityRgb, gA));
		gfx.fill(cx - gSize + 4, itemCY - gSize + 4, cx + gSize - 4, itemCY + gSize - 4, alpha(rarityRgb, gA / 2));
		gfx.pose().pushPose();
		gfx.pose().translate(cx - half, itemCY - half, 200f);
		gfx.pose().scale(scale, scale, scale);
		gfx.renderItem(winnerStack, 0, 0);
		gfx.pose().popPose();
	}

	private void emitSparkles(float ex, float ey) {
		for (int i = 0; i < 32; i++) {
			float angle = (float) (SPARK_RNG.nextFloat() * Math.PI * 2);
			float speed = 1.0f + SPARK_RNG.nextFloat() * 4.0f;
			sparkles.add(new float[]{ex, ey, (float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed - 1.5f, 1f + SPARK_RNG.nextFloat() * 0.6f});
		}
	}

	private void renderSparkles(GuiGraphics gfx) {
		for (int i = 0; i < sparkles.size(); i++) {
			float[] s = sparkles.get(i);
			if (s[4] <= 0f || s[4] > 1f)
				continue;
			int rgb = (i % 2 == 0) ? rarityRgb : 0xFFFFFF;
			int a = (int) (Math.min(1f, s[4] * 2f) * 0xFF);
			int px = (int) s[0], py = (int) s[1];
			gfx.fill(px, py, px + 2, py + 2, alpha(rgb, a));
		}
	}

	private void renderResult(GuiGraphics gfx) {
		float slideT = easeOutCubic(resultAnim);
		int baseY = reelY + REEL_H + 8;
		int textY = baseY + (int) ((1f - slideT) * 10f);
		String result = "You got:  " + winnerStack.getHoverName().getString() + "  ×" + winnerStack.getCount();
		int textW = font.width(result);
		gfx.fill(cx - textW / 2 - 6, textY - 2, cx + textW / 2 + 6, textY + font.lineHeight + 3, alpha(rarityRgb, (int) (slideT * 0x33)));
		gfx.drawCenteredString(font, Component.literal(result).withStyle(rarity.color), cx, textY, alpha(rarityRgb, (int) (slideT * 0xFF)));
		if (closeTimer > 0) {
			int secs = (int) Math.ceil(closeTimer / 20.0);
		}
	}

	private static float easeOutCubic(float t) {
		float f = 1f - Math.min(t, 1f);
		return 1f - f * f * f;
	}

	private static float easeOutBack(float t) {
		t = Math.min(t, 1f);
		float c1 = 1.70158f, c3 = c1 + 1f;
		return 1f + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
	}

	private static int alpha(int rgb, int a) {
		return (Math.max(0, Math.min(255, a)) << 24) | (rgb & 0xFFFFFF);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean keyPressed(int key, int scan, int mods) {
		return true;
	}
}

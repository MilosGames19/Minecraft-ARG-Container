package net.mcreator.minecraftalphaargmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.Minecraft;

import java.time.MonthDay;
import java.time.LocalDate;

@Mod.EventBusSubscriber(modid = "the_arg_container", value = Dist.CLIENT)
public final class LootboxPauseMenuHandler {
	private static final MonthDay WINDOW_START = MonthDay.of(4, 1);
	private static final MonthDay WINDOW_END = MonthDay.of(4, 7);
	private static final int BTN_WIDTH = 90;
	private static final int BTN_HEIGHT = 16;
	private static final int MARGIN = 6;

	private LootboxPauseMenuHandler() {
	}

	@SubscribeEvent
	public static void onScreenInit(ScreenEvent.Init.Post event) {
		if (!(event.getScreen() instanceof PauseScreen pauseScreen))
			return;
		if (!isAprilWindow())
			return;
		int screenW = pauseScreen.width;
		int screenH = pauseScreen.height;
		Button lootboxButton = Button.builder(Component.literal("Shop"), btn -> Minecraft.getInstance().setScreen(new LootboxShopScreen())).pos(screenW - BTN_WIDTH - MARGIN, screenH - BTN_HEIGHT - MARGIN).size(BTN_WIDTH, BTN_HEIGHT).build();
		event.addListener(lootboxButton);
	}

	private static boolean isAprilWindow() {
		MonthDay today = MonthDay.from(LocalDate.now());
		return !today.isBefore(WINDOW_START) && !today.isAfter(WINDOW_END);
	}
}

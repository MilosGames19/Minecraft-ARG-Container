package net.mcreator.minecraftalphaargmod.init;

import software.bernie.geckolib.animatable.GeoItem;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.Minecraft;

import net.mcreator.minecraftalphaargmod.item.ScytheItem;

@Mod.EventBusSubscriber
public class ItemAnimationFactory {
	public static void disableUseAnim(String hand) {
		ItemInHandRenderer renderer = Minecraft.getInstance().gameRenderer.itemInHandRenderer;
		if (renderer != null) {
			if (hand.equals("right")) {
				renderer.mainHandHeight = 1F;
				renderer.oMainHandHeight = 1F;
			}
			if (hand.equals("left")) {
				renderer.offHandHeight = 1F;
				renderer.oOffHandHeight = 1F;
			}
		}
	}

	@SubscribeEvent
	public static void animatedItems(TickEvent.PlayerTickEvent event) {
		String animation = "";
		ItemStack mainhandItem = event.player.getMainHandItem().copy();
		ItemStack offhandItem = event.player.getOffhandItem().copy();
		if (event.phase == TickEvent.Phase.START && (mainhandItem.getItem() instanceof GeoItem || offhandItem.getItem() instanceof GeoItem)) {
			if (mainhandItem.getItem() instanceof ScytheItem animatable) {
				animation = mainhandItem.getOrCreateTag().getString("geckoAnim");
				if (!animation.isEmpty()) {
					event.player.getMainHandItem().getOrCreateTag().putString("geckoAnim", "");
					if (event.player.level().isClientSide()) {
						((ScytheItem) event.player.getMainHandItem().getItem()).animationprocedure = animation;
						disableUseAnim("right");
					}
				}
			}
			if (offhandItem.getItem() instanceof ScytheItem animatable) {
				animation = offhandItem.getOrCreateTag().getString("geckoAnim");
				if (!animation.isEmpty()) {
					event.player.getOffhandItem().getOrCreateTag().putString("geckoAnim", "");
					if (event.player.level().isClientSide()) {
						((ScytheItem) event.player.getOffhandItem().getItem()).animationprocedure = animation;
						disableUseAnim("left");
					}
				}
			}
		}
	}
}

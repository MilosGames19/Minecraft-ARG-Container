package net.mcreator.minecraftalphaargmod.network;

import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.resources.ResourceLocation;

@Mod.EventBusSubscriber(modid = "the_arg_container", bus = Mod.EventBusSubscriber.Bus.MOD)
public final class LootboxNetwork {
	private LootboxNetwork() {
	}

	private static final String PROTOCOL = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation("the_arg_container", "lootbox"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			CHANNEL.registerMessage(0, GiveLootboxItemPacket.class, GiveLootboxItemPacket::encode, GiveLootboxItemPacket::decode, GiveLootboxItemPacket::handle);
		});
	}
}

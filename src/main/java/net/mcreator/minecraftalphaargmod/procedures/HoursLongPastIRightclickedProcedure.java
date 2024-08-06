package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.network.chat.Component;

public class HoursLongPastIRightclickedProcedure {
	public static void execute(LevelAccessor world) {
		if (!world.isClientSide() && world.getServer() != null)
			world.getServer().getPlayerList()
					.broadcastSystemMessage(Component.literal("Centuries upon Centuries ago, the world was nothing but shallow tidal pools and rock. Simple beings dwelled within - And were content in their ever so temperate Paradise."), false);
	}
}

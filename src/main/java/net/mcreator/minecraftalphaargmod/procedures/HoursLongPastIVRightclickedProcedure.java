package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.network.chat.Component;

public class HoursLongPastIVRightclickedProcedure {
	public static void execute(LevelAccessor world) {
		if (!world.isClientSide() && world.getServer() != null)
			world.getServer().getPlayerList().broadcastSystemMessage(
					Component.literal("But far down below, the Old World still lives. Flowers bloom with a light stolen from them so long ago Moss and fungi grow as trees - just like they would before. And they will, too, be seen again one day."),
					false);
	}
}

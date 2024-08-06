package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.network.chat.Component;

public class TheOneTrueBookRightclickedProcedure {
	public static void execute(LevelAccessor world) {
		if (!world.isClientSide() && world.getServer() != null)
			world.getServer().getPlayerList().broadcastSystemMessage(Component.literal(
					"In the end, this world was for you, and when you shall turn away It will remember. Its past. Your present. And both will rest, knowing that they will inspire each other carrying pieces further forth. So that all shall be well, and all matter of thing shall be well, in the end. Against all odds, just you wait."),
					false);
	}
}

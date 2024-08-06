package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.network.chat.Component;

public class HoursLongPastIIIRightclickedProcedure {
	public static void execute(LevelAccessor world) {
		if (!world.isClientSide() && world.getServer() != null)
			world.getServer().getPlayerList().broadcastSystemMessage(Component.literal(
					"Cities of stone and glass wrought as a mindless fascimile of an unknowable land. Cubes of steel forged from ancient beasts littering seas. Under the sun - and the newborn blood-stars - a new world rests, pristine, without history."),
					false);
	}
}

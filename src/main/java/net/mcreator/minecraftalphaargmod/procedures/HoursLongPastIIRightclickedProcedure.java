package net.mcreator.minecraftalphaargmod.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.network.chat.Component;

public class HoursLongPastIIRightclickedProcedure {
	public static void execute(LevelAccessor world) {
		if (!world.isClientSide() && world.getServer() != null)
			world.getServer().getPlayerList().broadcastSystemMessage(Component.literal(
					"Then, in a darkness so bright it blinded all who saw it - the world changed. What once was its surface now lay deep below smothered by ever-young stone and with skies bleeding light far above. The world changed in an instant. A vast new unknown."),
					false);
	}
}

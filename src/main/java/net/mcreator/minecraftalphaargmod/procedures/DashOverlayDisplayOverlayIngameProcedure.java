package net.mcreator.minecraftalphaargmod.procedures;

import net.mcreator.minecraftalphaargmod.configuration.McconfigConfiguration;

public class DashOverlayDisplayOverlayIngameProcedure {
	public static boolean execute() {
		if (McconfigConfiguration.DASH_OVERLAY.get() == true) {
			return true;
		}
		return false;
	}
}

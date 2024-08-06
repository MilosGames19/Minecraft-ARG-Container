/**
 * The code of this mod element is always locked.
 *
 * You can register new events in this class too.
 *
 * If you want to make a plain independent class, create it using
 * Project Browser -> New... and make sure to make the class
 * outside net.mcreator.minecraftalphaargmod as this package is managed by MCreator.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
 *
 * This class will be added in the mod root package.
*/
package net.mcreator.minecraftalphaargmod;

import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;

public class MarvCraftingTable extends CraftingMenu {
	public MarvCraftingTable(int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
		super(i, inventory, containerLevelAccess);
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}
}

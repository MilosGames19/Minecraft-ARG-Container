
package net.mcreator.minecraftalphaargmod.block;

import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.FenceGateBlock;

public class OstrutenePlanksFenceGateBlock extends FenceGateBlock {
	public OstrutenePlanksFenceGateBlock() {
		super(BlockBehaviour.Properties.of().ignitedByLava().instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).strength(1f, 10f).dynamicShape().forceSolidOn(), WoodType.OAK);
	}
}

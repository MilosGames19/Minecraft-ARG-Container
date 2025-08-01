
package net.mcreator.minecraftalphaargmod.block;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.util.ForgeSoundType;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

public class DebugBlock extends Block {
	public DebugBlock() {
		super(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM)
				.sound(new ForgeSoundType(1.0f, 1.0f, () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("the_arg_container:safe")), () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("the_arg_container:safe")),
						() -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("the_arg_container:safe")), () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("the_arg_container:safe")),
						() -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("the_arg_container:safe"))))
				.strength(0.2f, 10f));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}
}

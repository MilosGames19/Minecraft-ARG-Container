
package net.mcreator.minecraftalphaargmod.block;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;

public class JljljljljlPlushieBlock extends Block {
	public JljljljljlPlushieBlock() {
		super(BlockBehaviour.Properties.of().ignitedByLava().sound(SoundType.WOOL).strength(1f, 10f).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 0;
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.or(box(5, 1, 9.9, 11, 9, 14.2), box(5, 1, 10, 11, 9, 14), box(11, 3, 7, 14, 6, 14), box(1.8, 2.8, 6.8, 5.2, 6.2, 14.2), box(10.8, 2.8, 6.8, 14.2, 6.2, 14.2), box(5, 9, 9, 11, 15, 15), box(4.8, 8.8, 8.9, 11.2, 15.2, 15.3),
				box(2, 3, 7, 5, 6, 14), box(11, 0, 5, 14, 3, 13), box(1.8, -0.2, 5, 5.2, 3.2, 13.1), box(10.8, -0.2, 5, 14.2, 3.2, 13.1), box(2, 0, 5, 5, 3, 13));
	}
}

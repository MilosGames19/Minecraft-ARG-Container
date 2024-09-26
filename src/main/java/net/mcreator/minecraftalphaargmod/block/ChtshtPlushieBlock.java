
package net.mcreator.minecraftalphaargmod.block;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public class ChtshtPlushieBlock extends Block {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public ChtshtPlushieBlock() {
		super(BlockBehaviour.Properties.of().ignitedByLava().sound(SoundType.WOOL).strength(1f, 10f).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
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
		return switch (state.getValue(FACING)) {
			default -> Shapes.or(box(5, 1, 1.8, 11, 9, 6.1), box(5, 1, 2, 11, 9, 6), box(2, 3, 2, 5, 6, 9), box(10.8, 2.8, 1.8, 14.2, 6.2, 9.2), box(1.8, 2.8, 1.8, 5.2, 6.2, 9.2), box(5, 9, 1, 11, 15, 7), box(4.8, 8.8, 0.7, 11.2, 15.2, 7.1),
					box(11, 3, 2, 14, 6, 9), box(2, 0, 3, 5, 3, 11), box(10.8, -0.2, 2.9, 14.2, 3.2, 11), box(1.8, -0.2, 2.9, 5.2, 3.2, 11), box(11, 0, 3, 14, 3, 11));
			case NORTH -> Shapes.or(box(5, 1, 9.9, 11, 9, 14.2), box(5, 1, 10, 11, 9, 14), box(11, 3, 7, 14, 6, 14), box(1.8, 2.8, 6.8, 5.2, 6.2, 14.2), box(10.8, 2.8, 6.8, 14.2, 6.2, 14.2), box(5, 9, 9, 11, 15, 15),
					box(4.8, 8.8, 8.9, 11.2, 15.2, 15.3), box(2, 3, 7, 5, 6, 14), box(11, 0, 5, 14, 3, 13), box(1.8, -0.2, 5, 5.2, 3.2, 13.1), box(10.8, -0.2, 5, 14.2, 3.2, 13.1), box(2, 0, 5, 5, 3, 13));
			case EAST -> Shapes.or(box(1.8, 1, 5, 6.1, 9, 11), box(2, 1, 5, 6, 9, 11), box(2, 3, 11, 9, 6, 14), box(1.8, 2.8, 1.8, 9.2, 6.2, 5.2), box(1.8, 2.8, 10.8, 9.2, 6.2, 14.2), box(1, 9, 5, 7, 15, 11), box(0.7, 8.8, 4.8, 7.1, 15.2, 11.2),
					box(2, 3, 2, 9, 6, 5), box(3, 0, 11, 11, 3, 14), box(2.9, -0.2, 1.8, 11, 3.2, 5.2), box(2.9, -0.2, 10.8, 11, 3.2, 14.2), box(3, 0, 2, 11, 3, 5));
			case WEST -> Shapes.or(box(9.9, 1, 5, 14.2, 9, 11), box(10, 1, 5, 14, 9, 11), box(7, 3, 2, 14, 6, 5), box(6.8, 2.8, 10.8, 14.2, 6.2, 14.2), box(6.8, 2.8, 1.8, 14.2, 6.2, 5.2), box(9, 9, 5, 15, 15, 11),
					box(8.9, 8.8, 4.8, 15.3, 15.2, 11.2), box(7, 3, 11, 14, 6, 14), box(5, 0, 2, 13, 3, 5), box(5, -0.2, 10.8, 13.1, 3.2, 14.2), box(5, -0.2, 1.8, 13.1, 3.2, 5.2), box(5, 0, 11, 13, 3, 14));
		};
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}
}


package net.mcreator.minecraftalphaargmod.block;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
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

public class AlphaverLogoBlock extends Block {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public AlphaverLogoBlock() {
		super(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(1f, 10f).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
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
			default -> Shapes.or(box(3, 0, 3, 13, 1, 13), box(4, 1, 4, 6, 5, 6), box(6, 1, 4, 8, 5, 6), box(4, 5, 4, 8, 9, 6), box(8, 5, 4, 10, 9, 6), box(2, 5, 4, 4, 9, 6), box(4, 9, 3, 8, 13, 7), box(8, 1, 10, 10, 5, 12), box(10, 1, 10, 12, 5, 12),
					box(8, 5, 10, 12, 9, 12), box(12, 5, 10, 14, 9, 12), box(6, 5, 10, 8, 9, 12), box(8, 9, 9, 12, 13, 13));
			case NORTH -> Shapes.or(box(3, 0, 3, 13, 1, 13), box(10, 1, 10, 12, 5, 12), box(8, 1, 10, 10, 5, 12), box(8, 5, 10, 12, 9, 12), box(6, 5, 10, 8, 9, 12), box(12, 5, 10, 14, 9, 12), box(8, 9, 9, 12, 13, 13), box(6, 1, 4, 8, 5, 6),
					box(4, 1, 4, 6, 5, 6), box(4, 5, 4, 8, 9, 6), box(2, 5, 4, 4, 9, 6), box(8, 5, 4, 10, 9, 6), box(4, 9, 3, 8, 13, 7));
			case EAST -> Shapes.or(box(3, 0, 3, 13, 1, 13), box(4, 1, 10, 6, 5, 12), box(4, 1, 8, 6, 5, 10), box(4, 5, 8, 6, 9, 12), box(4, 5, 6, 6, 9, 8), box(4, 5, 12, 6, 9, 14), box(3, 9, 8, 7, 13, 12), box(10, 1, 6, 12, 5, 8),
					box(10, 1, 4, 12, 5, 6), box(10, 5, 4, 12, 9, 8), box(10, 5, 2, 12, 9, 4), box(10, 5, 8, 12, 9, 10), box(9, 9, 4, 13, 13, 8));
			case WEST -> Shapes.or(box(3, 0, 3, 13, 1, 13), box(10, 1, 4, 12, 5, 6), box(10, 1, 6, 12, 5, 8), box(10, 5, 4, 12, 9, 8), box(10, 5, 8, 12, 9, 10), box(10, 5, 2, 12, 9, 4), box(9, 9, 4, 13, 13, 8), box(4, 1, 8, 6, 5, 10),
					box(4, 1, 10, 6, 5, 12), box(4, 5, 8, 6, 9, 12), box(4, 5, 12, 6, 9, 14), box(4, 5, 6, 6, 9, 8), box(3, 9, 8, 7, 13, 12));
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

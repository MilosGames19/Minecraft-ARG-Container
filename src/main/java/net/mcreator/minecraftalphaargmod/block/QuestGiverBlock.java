
package net.mcreator.minecraftalphaargmod.block;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.BlockHitResult;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.minecraftalphaargmod.procedures.QuestGiverOnBlockRightClickedProcedure;

public class QuestGiverBlock extends Block {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public QuestGiverBlock() {
		super(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.GRAVEL).strength(-1, 3600000).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
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
			default -> Shapes.or(box(4, 23.5, 4, 12, 31.5, 12), box(4, 11.5, 6, 12, 23.5, 10), box(0, 11.5, 6, 4, 23.5, 10), box(12, 11.5, 6, 16, 23.5, 10), box(4, -0.5, 6, 8, 11.5, 10), box(8, -0.5, 6, 12, 11.5, 10),
					box(3.5, 23, 3.5, 12.5, 32, 12.5), box(4, 11.02, 5.5, 12, 24.03, 10.5), box(-0.49, 11, 5.5, 4.5, 24, 10.5), box(11.5, 11, 5.5, 16.5, 24, 10.5), box(3.5, -1, 5.5, 8.5, 12, 10.5), box(7.5, -1, 5.5, 12.5, 12, 10.5));
			case NORTH -> Shapes.or(box(4, 23.5, 4, 12, 31.5, 12), box(4, 11.5, 6, 12, 23.5, 10), box(12, 11.5, 6, 16, 23.5, 10), box(0, 11.5, 6, 4, 23.5, 10), box(8, -0.5, 6, 12, 11.5, 10), box(4, -0.5, 6, 8, 11.5, 10),
					box(3.5, 23, 3.5, 12.5, 32, 12.5), box(4, 11.02, 5.5, 12, 24.03, 10.5), box(11.5, 11, 5.5, 16.49, 24, 10.5), box(-0.5, 11, 5.5, 4.5, 24, 10.5), box(7.5, -1, 5.5, 12.5, 12, 10.5), box(3.5, -1, 5.5, 8.5, 12, 10.5));
			case EAST -> Shapes.or(box(4, 23.5, 4, 12, 31.5, 12), box(6, 11.5, 4, 10, 23.5, 12), box(6, 11.5, 12, 10, 23.5, 16), box(6, 11.5, 0, 10, 23.5, 4), box(6, -0.5, 8, 10, 11.5, 12), box(6, -0.5, 4, 10, 11.5, 8),
					box(3.5, 23, 3.5, 12.5, 32, 12.5), box(5.5, 11.02, 4, 10.5, 24.03, 12), box(5.5, 11, 11.5, 10.5, 24, 16.49), box(5.5, 11, -0.5, 10.5, 24, 4.5), box(5.5, -1, 7.5, 10.5, 12, 12.5), box(5.5, -1, 3.5, 10.5, 12, 8.5));
			case WEST -> Shapes.or(box(4, 23.5, 4, 12, 31.5, 12), box(6, 11.5, 4, 10, 23.5, 12), box(6, 11.5, 0, 10, 23.5, 4), box(6, 11.5, 12, 10, 23.5, 16), box(6, -0.5, 4, 10, 11.5, 8), box(6, -0.5, 8, 10, 11.5, 12),
					box(3.5, 23, 3.5, 12.5, 32, 12.5), box(5.5, 11.02, 4, 10.5, 24.03, 12), box(5.5, 11, -0.49, 10.5, 24, 4.5), box(5.5, 11, 11.5, 10.5, 24, 16.5), box(5.5, -1, 3.5, 10.5, 12, 8.5), box(5.5, -1, 7.5, 10.5, 12, 12.5));
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

	@Override
	public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		super.use(blockstate, world, pos, entity, hand, hit);
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		double hitX = hit.getLocation().x;
		double hitY = hit.getLocation().y;
		double hitZ = hit.getLocation().z;
		Direction direction = hit.getDirection();
		QuestGiverOnBlockRightClickedProcedure.execute(entity);
		return InteractionResult.SUCCESS;
	}
}
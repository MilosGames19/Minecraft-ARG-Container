
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.minecraftalphaargmod.procedures.DevProcedure;

public class Dev1PlushyBlock extends Block {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public Dev1PlushyBlock() {
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
			default -> Shapes.or(box(5, 1, 2, 11, 9, 6), box(11, 0, 2, 14, 3, 10), box(2, 0, 2, 5, 3, 10), box(11, 3, 2, 14, 6, 9), box(2, 3, 2, 5, 6, 10), box(5, 9, 1, 11, 15, 7));
			case NORTH -> Shapes.or(box(5, 1, 10, 11, 9, 14), box(2, 0, 6, 5, 3, 14), box(11, 0, 6, 14, 3, 14), box(2, 3, 7, 5, 6, 14), box(11, 3, 6, 14, 6, 14), box(5, 9, 9, 11, 15, 15));
			case EAST -> Shapes.or(box(2, 1, 5, 6, 9, 11), box(2, 0, 2, 10, 3, 5), box(2, 0, 11, 10, 3, 14), box(2, 3, 2, 9, 6, 5), box(2, 3, 11, 10, 6, 14), box(1, 9, 5, 7, 15, 11));
			case WEST -> Shapes.or(box(10, 1, 5, 14, 9, 11), box(6, 0, 11, 14, 3, 14), box(6, 0, 2, 14, 3, 5), box(7, 3, 11, 14, 6, 14), box(6, 3, 2, 14, 6, 5), box(9, 9, 5, 15, 15, 11));
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
	public void setPlacedBy(Level world, BlockPos pos, BlockState blockstate, LivingEntity entity, ItemStack itemstack) {
		super.setPlacedBy(world, pos, blockstate, entity, itemstack);
		DevProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ(), entity);
	}
}

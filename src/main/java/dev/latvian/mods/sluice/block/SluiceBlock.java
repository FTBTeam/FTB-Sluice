package dev.latvian.mods.sluice.block;

import dev.latvian.mods.sluice.item.MeshItem;
import dev.latvian.mods.sluice.recipe.SluiceModRecipeSerializers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class SluiceBlock extends Block
{
	public static final VoxelShape SHAPE = makeCuboidShape(0, 0, 0, 16, 16, 16);

	public static final EnumProperty<MeshType> MESH = EnumProperty.create("mesh", MeshType.class);
	public static final BooleanProperty WATER = BooleanProperty.create("water");

	public SluiceBlock()
	{
		super(Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(0.9F).notSolid());
		setDefaultState(stateContainer.getBaseState()
				.with(MESH, MeshType.NONE)
				.with(WATER, false)
				.with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new SluiceBlockEntity();
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return SHAPE;
	}

	@Override
	@Deprecated
	@OnlyIn(Dist.CLIENT)
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		return 1.0F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos)
	{
		return true;
	}

	@Override
	@Deprecated
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		ItemStack itemStack = player.getHeldItem(hand);
		TileEntity tileEntity = world.getTileEntity(pos);

		if (!(tileEntity instanceof SluiceBlockEntity))
		{
			return ActionResultType.SUCCESS;
		}

		SluiceBlockEntity sluice = (SluiceBlockEntity) tileEntity;

		if (player.isSneaking())
		{
			if (state.get(MESH) != MeshType.NONE && itemStack.isEmpty())
			{
				ItemStack current = state.get(MESH).meshItem.get();
				world.setBlockState(pos, state.with(MESH, MeshType.NONE), 3);

				if (!world.isRemote())
				{
					ItemHandlerHelper.giveItemToPlayer(player, current);
				}

				sluice.updateContainingBlockInfo();
			}

			return ActionResultType.SUCCESS;
		}
		else if (itemStack.getItem() instanceof MeshItem)
		{
			if (state.get(MESH) != ((MeshItem) itemStack.getItem()).mesh)
			{
				ItemStack current = state.get(MESH).meshItem.get();
				world.setBlockState(pos, state.with(MESH, ((MeshItem) itemStack.getItem()).mesh), 3);
				itemStack.shrink(1);

				if (!world.isRemote())
				{
					ItemHandlerHelper.giveItemToPlayer(player, current);
				}

				sluice.updateContainingBlockInfo();
			}

			return ActionResultType.SUCCESS;
		}
		else if (SluiceModRecipeSerializers.getProperties(world, state.get(MESH), itemStack) != null)
		{
			if (!world.isRemote())
			{
				sluice.updateContainingBlockInfo();
				itemStack.setCount(ItemHandlerHelper.insertItem(sluice.inventory, itemStack.copy(), false).getCount());
			}

			return ActionResultType.SUCCESS;
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(MESH, WATER, BlockStateProperties.HORIZONTAL_FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		BlockState facingState = context.getWorld().getBlockState(context.getPos().up());
		return getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing()).with(WATER, facingState.getBlock() == Blocks.WATER || facingState.getBlock() == this && facingState.get(WATER));
	}

	@Override
	@Deprecated
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos)
	{
		return facing == Direction.UP ? state.with(WATER, facingState.getBlock() == Blocks.WATER || facingState.getBlock() == this && facingState.get(WATER)) : state;
	}

	@Override
	@Deprecated
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if (!state.isIn(newState.getBlock()))
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity instanceof SluiceBlockEntity)
			{
				spawnAsEntity(world, pos, ((SluiceBlockEntity) tileEntity).inventory.getStackInSlot(0));
				world.updateComparatorOutputLevel(pos, this);
			}

			spawnAsEntity(world, pos, state.get(MESH).meshItem.get());

			super.onReplaced(state, world, pos, newState, isMoving);
		}
	}
}

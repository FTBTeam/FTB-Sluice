package dev.ftb.mods.sluice.block;

import dev.ftb.mods.sluice.item.MeshItem;
import dev.ftb.mods.sluice.recipe.SluiceModRecipeSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class SluiceBlock extends Block {
    public static final VoxelShape SHAPE = box(0, 0, 0, 16, 10, 16);

    public static final EnumProperty<MeshType> MESH = EnumProperty.create("mesh", MeshType.class);
    public static final BooleanProperty WATER = BooleanProperty.create("water");

    public SluiceBlock() {
        super(Properties.of(Material.METAL).sound(SoundType.METAL).strength(0.9F).noOcclusion());
		this.registerDefaultState(this.getStateDefinition().any()
                .setValue(MESH, MeshType.NONE)
                .setValue(WATER, false)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        if (state.getBlock() == SluiceModBlocks.OAK_SLUICE.get()) {
            return SluiceModBlockEntities.OAK_SLUICE.get().create();
        } else if (state.getBlock() == SluiceModBlocks.IRON_SLUICE.get()) {
            return SluiceModBlockEntities.IRON_SLUICE.get().create();
        } else if (state.getBlock() == SluiceModBlocks.DIAMOND_SLUICE.get()) {
            return SluiceModBlockEntities.DIAMOND_SLUICE.get().create();
        } else {
            return SluiceModBlockEntities.NETHERITE_SLUICE.get().create();
        }
    }

    @Override
    @Deprecated
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    @Deprecated
    @OnlyIn(Dist.CLIENT)
    public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 1F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);
        BlockEntity tileEntity = world.getBlockEntity(pos);

        if (!(tileEntity instanceof SluiceBlockEntity)) {
            return InteractionResult.SUCCESS;
        }

        SluiceBlockEntity sluice = (SluiceBlockEntity) tileEntity;

        if (player.isCrouching()) {
            if (state.getValue(MESH) != MeshType.NONE && itemStack.isEmpty()) {
                ItemStack current = state.getValue(MESH).getItemStack();
                world.setBlock(pos, state.setValue(MESH, MeshType.NONE), 3);

                if (!world.isClientSide()) {
                    ItemHandlerHelper.giveItemToPlayer(player, current);
                }

                sluice.clearCache();
            }

            return InteractionResult.SUCCESS;
        } else if (itemStack.getItem() instanceof MeshItem) {
            if (state.getValue(MESH) != ((MeshItem) itemStack.getItem()).mesh) {
                ItemStack current = state.getValue(MESH).getItemStack();
                world.setBlock(pos, state.setValue(MESH, ((MeshItem) itemStack.getItem()).mesh), 3);
                itemStack.shrink(1);

                if (!world.isClientSide()) {
                    ItemHandlerHelper.giveItemToPlayer(player, current);
                }

                sluice.clearCache();
            }

            return InteractionResult.SUCCESS;
        } else if (itemStack.getItem() instanceof BucketItem && ((BucketItem) itemStack.getItem()).getFluid() == Fluids.WATER) {
            if (!world.isClientSide()) {
                boolean fluidInserted = FluidUtil.interactWithFluidHandler(player, hand, sluice.tank);
                if (fluidInserted) {
                    sluice.clearCache();
                    world.setBlock(pos, state.setValue(WATER, true), Constants.BlockFlags.DEFAULT);
                }
            }
        } else if (SluiceModRecipeSerializers.itemHasSluiceResults(world, state.getValue(MESH), itemStack)) {
            if (!world.isClientSide()) {
                if (sluice.inventory.getStackInSlot(0).isEmpty()) {
                    sluice.clearCache();
                    ItemStack copy = itemStack.copy();
                    copy.setCount(1);
                    ItemHandlerHelper.insertItem(sluice.inventory, copy, false);
                    itemStack.shrink(1);
                }
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MESH, WATER, BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState facingState = context.getLevel().getBlockState(context.getClickedPos().above());
        return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection()).setValue(WATER, facingState.getBlock() == Blocks.WATER || facingState.getBlock() == this && facingState.getValue(WATER));
    }

    @Override
    @Deprecated
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        return facing == Direction.UP ? state.setValue(WATER, facingState.getBlock() == Blocks.WATER || facingState.getBlock() == this && facingState.getValue(WATER)) : state;
    }

    @Override
    @Deprecated
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity tileEntity = world.getBlockEntity(pos);

            if (tileEntity instanceof SluiceBlockEntity) {
                popResource(world, pos, ((SluiceBlockEntity) tileEntity).inventory.getStackInSlot(0));
                world.updateNeighbourForOutputSignal(pos, this);
            }

            popResource(world, pos, state.getValue(MESH).getItemStack());

            super.onRemove(state, world, pos, newState, isMoving);
        }
    }
}

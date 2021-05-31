package dev.ftb.mods.sluice.block;

import dev.ftb.mods.sluice.item.MeshItem;
import dev.ftb.mods.sluice.item.SluiceModItems;
import dev.ftb.mods.sluice.recipe.SluiceModRecipeSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.stream.Stream;

public class SluiceBlock extends Block {
    public static final EnumProperty<MeshType> MESH = EnumProperty.create("mesh", MeshType.class);
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    public static final BooleanProperty WATER = BooleanProperty.create("water");

    private static final VoxelShape NORTH_BODY_SHAPE = Stream.of(Block.box(12.5, 0, 0, 14.5, 1, 1), Block.box(1.5, 0, 13.5, 3.5, 1, 15.5), Block.box(12.5, 0, 13.5, 14.5, 1, 15.5), Block.box(1.5, 0, 0, 3.5, 1, 1), Block.box(1, 1, 0, 15, 2, 16), Block.box(14, 2, 0, 15, 8, 16), Block.box(1, 2, 0, 2, 8, 16), Block.box(2, 5, 0, 14, 8, 1), Block.box(2, 2, 15, 14, 8, 16), Block.box(2, 2, 0, 14, 2.5, 1), Block.box(2, 7, 1, 14, 12, 2), Block.box(2, 7, 14, 14, 12, 15), Block.box(13, 7, 2, 14, 12, 14), Block.box(2, 7, 2, 3, 12, 14)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape EAST_BODY_SHAPE = Stream.of(Block.box(15, 0, 12.5, 16, 1, 14.5), Block.box(0.5, 0, 1.5, 2.5, 1, 3.5), Block.box(0.5, 0, 12.5, 2.5, 1, 14.5), Block.box(15, 0, 1.5, 16, 1, 3.5), Block.box(0, 1, 1, 16, 2, 15), Block.box(0, 2, 14, 16, 8, 15), Block.box(0, 2, 1, 16, 8, 2), Block.box(15, 5, 2, 16, 8, 14), Block.box(0, 2, 2, 1, 8, 14), Block.box(15, 2, 2, 16, 2.5, 14), Block.box(14, 7, 2, 15, 12, 14), Block.box(1, 7, 2, 2, 12, 14), Block.box(2, 7, 13, 14, 12, 14), Block.box(2, 7, 2, 14, 12, 3)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape SOUTH_BODY_SHAPE = Stream.of(Block.box(1.5, 0, 15, 3.5, 1, 16), Block.box(12.5, 0, 0.5, 14.5, 1, 2.5), Block.box(1.5, 0, 0.5, 3.5, 1, 2.5), Block.box(12.5, 0, 15, 14.5, 1, 16), Block.box(1, 1, 0, 15, 2, 16), Block.box(1, 2, 0, 2, 8, 16), Block.box(14, 2, 0, 15, 8, 16), Block.box(2, 5, 15, 14, 8, 16), Block.box(2, 2, 0, 14, 8, 1), Block.box(2, 2, 15, 14, 2.5, 16), Block.box(2, 7, 14, 14, 12, 15), Block.box(2, 7, 1, 14, 12, 2), Block.box(2, 7, 2, 3, 12, 14), Block.box(13, 7, 2, 14, 12, 14)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape WEST_BODY_SHAPE = Stream.of(Block.box(0, 0, 1.5, 1, 1, 3.5), Block.box(13.5, 0, 12.5, 15.5, 1, 14.5), Block.box(13.5, 0, 1.5, 15.5, 1, 3.5), Block.box(0, 0, 12.5, 1, 1, 14.5), Block.box(0, 1, 1, 16, 2, 15), Block.box(0, 2, 1, 16, 8, 2), Block.box(0, 2, 14, 16, 8, 15), Block.box(0, 5, 2, 1, 8, 14), Block.box(15, 2, 2, 16, 8, 14), Block.box(0, 2, 2, 1, 2.5, 14), Block.box(1, 7, 2, 2, 12, 14), Block.box(14, 7, 2, 15, 12, 14), Block.box(2, 7, 2, 14, 12, 3), Block.box(2, 7, 13, 14, 12, 14)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape NORTH_FRONT_SHAPE = Stream.of(Block.box(2, 1.5, 12, 14, 2.5, 13), Block.box(1, 2, 0, 2, 4, 16), Block.box(2, 1.5, 8, 14, 2.5, 9), Block.box(2, 1.5, 4, 14, 2.5, 5), Block.box(1, 1, 0, 15, 2, 16), Block.box(14, 2, 0, 15, 4, 16), Block.box(12.5, 0, 0.5, 14.5, 1, 2.5), Block.box(1.5, 0, 0.5, 3.5, 1, 2.5), Block.box(1.5, 0, 15, 3.5, 1, 16), Block.box(12.5, 0, 15, 14.5, 1, 16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape EAST_FRONT_SHAPE = Stream.of(Block.box(3, 1.5, 2, 4, 2.5, 14), Block.box(0, 2, 1, 16, 4, 2), Block.box(7, 1.5, 2, 8, 2.5, 14), Block.box(11, 1.5, 2, 12, 2.5, 14), Block.box(0, 1, 1, 16, 2, 15), Block.box(0, 2, 14, 16, 4, 15), Block.box(13.5, 0, 12.5, 15.5, 1, 14.5), Block.box(13.5, 0, 1.5, 15.5, 1, 3.5), Block.box(0, 0, 1.5, 1, 1, 3.5), Block.box(0, 0, 12.5, 1, 1, 14.5)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape SOUTH_FRONT_SHAPE = Stream.of(Block.box(2, 1.5, 3, 14, 2.5, 4), Block.box(14, 2, 0, 15, 4, 16), Block.box(2, 1.5, 7, 14, 2.5, 8), Block.box(2, 1.5, 11, 14, 2.5, 12), Block.box(1, 1, 0, 15, 2, 16), Block.box(1, 2, 0, 2, 4, 16), Block.box(1.5, 0, 13.5, 3.5, 1, 15.5), Block.box(12.5, 0, 13.5, 14.5, 1, 15.5), Block.box(12.5, 0, 0, 14.5, 1, 1), Block.box(1.5, 0, 0, 3.5, 1, 1)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape WEST_FRONT_SHAPE = Stream.of(Block.box(12, 1.5, 2, 13, 2.5, 14), Block.box(0, 2, 14, 16, 4, 15), Block.box(8, 1.5, 2, 9, 2.5, 14), Block.box(4, 1.5, 2, 5, 2.5, 14), Block.box(0, 1, 1, 16, 2, 15), Block.box(0, 2, 1, 16, 4, 2), Block.box(0.5, 0, 1.5, 2.5, 1, 3.5), Block.box(0.5, 0, 12.5, 2.5, 1, 14.5), Block.box(15, 0, 12.5, 16, 1, 14.5), Block.box(15, 0, 1.5, 16, 1, 3.5)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final HashMap<Direction, Pair<VoxelShape, VoxelShape>> SHAPES;

    static {
        SHAPES = new HashMap<>();
        SHAPES.put(Direction.NORTH, Pair.of(NORTH_BODY_SHAPE, NORTH_FRONT_SHAPE));
        SHAPES.put(Direction.EAST, Pair.of(EAST_BODY_SHAPE, EAST_FRONT_SHAPE));
        SHAPES.put(Direction.SOUTH, Pair.of(SOUTH_BODY_SHAPE, SOUTH_FRONT_SHAPE));
        SHAPES.put(Direction.WEST, Pair.of(WEST_BODY_SHAPE, WEST_FRONT_SHAPE));
    }

    public SluiceBlock() {
        super(Properties.of(Material.METAL).sound(SoundType.METAL).strength(0.9F));
        this.registerDefaultState(this.getStateDefinition().any()
            .setValue(MESH, MeshType.NONE)
            .setValue(WATER, false)
            .setValue(PART, Part.MAIN)
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.getValue(PART) != Part.FUNNEL;
    }

    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        if (state.getValue(PART) == Part.FUNNEL) {
            return null;
        }

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
        Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (!SHAPES.containsKey(direction)) {
            // HOW?!
            return Shapes.empty();
        }

        Pair<VoxelShape, VoxelShape> bodyFrontShapes = SHAPES.get(direction);
        return state.getValue(PART) == Part.MAIN
            ? bodyFrontShapes.getKey()
            : bodyFrontShapes.getValue();
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
        if (state.getValue(PART) == Part.FUNNEL) {
            return InteractionResult.PASS;
        }

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
        } else if (itemStack.getItem() instanceof BucketItem && ((BucketItem) itemStack.getItem()).getFluid() == Fluids.WATER && state.getBlock() != SluiceModBlocks.NETHERITE_SLUICE.get()) {
            if (!world.isClientSide()) {
                int transfer = sluice.tank.internalFill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.SIMULATE);
                if (transfer > 0 && transfer <= 1000) {
                    SoundEvent soundevent = ((BucketItem) itemStack.getItem()).getFluid().getAttributes().getFillSound();
                    if (soundevent == null) {
                        soundevent = SoundEvents.BUCKET_FILL;
                    }

                    player.playSound(soundevent, 1.0F, 1.0F);
                    sluice.tank.internalFill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);

                    ItemStack output = itemStack.getItem() == Items.WATER_BUCKET
                        ? new ItemStack(Items.BUCKET)
                        : new ItemStack(SluiceModItems.CLAY_BUCKET.get());

                    itemStack.shrink(1);
                    ItemHandlerHelper.insertItemStacked(new InvWrapper(player.inventory), output, false);
                }
            }
        } else if (SluiceModRecipeSerializers.itemHasSluiceResults(world, state.getValue(MESH), itemStack)) {
            if (!world.isClientSide()) {
                if (sluice.inventory.getStackInSlot(0).isEmpty()) {
                    sluice.clearCache();
                    ItemStack copy = itemStack.copy();
                    copy.setCount(1);
                    sluice.inventory.internalInsert(0, copy, false);
                    itemStack.shrink(1);
                }
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MESH, WATER, PART, BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState facingState = context.getLevel().getBlockState(context.getClickedPos().above());

        BlockPos offsetPos = context.getClickedPos().relative(context.getHorizontalDirection().getOpposite());
        return context.getLevel().getBlockState(offsetPos).canBeReplaced(context)
            ? this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite()).setValue(WATER, facingState.getBlock() == Blocks.WATER || facingState.getBlock() == this && facingState.getValue(WATER)).setValue(PART, Part.MAIN)
            : null;
    }

    @Override
    @Deprecated
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        return super.updateShape(state, facing, facingState, world, pos, facingPos); //facing == Direction.UP ? state.setValue(WATER, facingState.getBlock() == Blocks.WATER || facingState.getBlock() == this && facingState.getValue(WATER)) : state;
    }

    @Override
    @Deprecated
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            BlockPos endPos = pos.relative(state.getValue(PART) == Part.FUNNEL
                ? direction.getOpposite()
                : direction);
            BlockState endState = world.getBlockState(endPos);

            if (state.getValue(PART) == Part.FUNNEL) {
                if (endState.getBlock() instanceof SluiceBlock && endState.getValue(PART) == Part.MAIN) {
                    world.removeBlock(endPos, false);
                }
            } else {
                BlockEntity tileEntity = world.getBlockEntity(pos);

                if (tileEntity instanceof SluiceBlockEntity) {
                    popResource(world, pos, ((SluiceBlockEntity) tileEntity).inventory.getStackInSlot(0));
                    world.updateNeighbourForOutputSignal(pos, this);
                }

                world.removeBlock(endPos, false);
                popResource(world, pos, state.getValue(MESH).getItemStack());

                super.onRemove(state, world, pos, newState, isMoving);
            }
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack item) {
        super.setPlacedBy(level, pos, state, entity, item);
        if (!level.isClientSide) {
            System.out.println("Placed down");
            BlockPos lv = pos.relative(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
            level.setBlock(lv, state.setValue(PART, Part.FUNNEL), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    public enum Part implements StringRepresentable {
        MAIN("main"),
        FUNNEL("funnel");

        String name;

        Part(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}

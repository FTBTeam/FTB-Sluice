package dev.ftb.mods.sluice.block.pump;

import dev.ftb.mods.sluice.block.SluiceBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class PumpBlock extends Block {
    public enum Progress implements StringRepresentable {
        ZERO(0),
        TWENTY(20),
        FORTY(40),
        SIXTY(60),
        EIGHTY(80),
        HUNDRED(100);

        int percentage;
        Progress(int percentage) {
            this.percentage = percentage;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }

    private static final VoxelShape NORTH = Stream.of(Block.box(0, 0, 0, 16, 10, 16), Block.box(3, 10, 6, 13, 13, 9), Block.box(6, 10, 0, 10, 12, 2), Block.box(0, 15, 8, 16, 16, 16), Block.box(1, 10, 9, 15, 15, 15), Block.box(0, 10, 15, 1, 15, 16), Block.box(0, 10, 8, 1, 15, 9), Block.box(15, 10, 8, 16, 15, 9), Block.box(15, 10, 15, 16, 15, 16), Block.box(2.5, 4, 3, 13.5, 15, 5), Block.box(7, 9, 2, 9, 11, 6)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape EAST = Stream.of(Block.box(0, 0, 0, 16, 10, 16), Block.box(7, 10, 3, 10, 13, 13), Block.box(14, 10, 6, 16, 12, 10), Block.box(0, 15, 0, 8, 16, 16), Block.box(1, 10, 1, 7, 15, 15), Block.box(0, 10, 0, 1, 15, 1), Block.box(7, 10, 0, 8, 15, 1), Block.box(7, 10, 15, 8, 15, 16), Block.box(0, 10, 15, 1, 15, 16), Block.box(11, 4, 2.5, 13, 15, 13.5), Block.box(10, 9, 7, 14, 11, 9)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape SOUTH = Stream.of(Block.box(0, 0, 0, 16, 10, 16), Block.box(3, 10, 7, 13, 13, 10), Block.box(6, 10, 14, 10, 12, 16), Block.box(0, 15, 0, 16, 16, 8), Block.box(1, 10, 1, 15, 15, 7), Block.box(15, 10, 0, 16, 15, 1), Block.box(15, 10, 7, 16, 15, 8), Block.box(0, 10, 7, 1, 15, 8), Block.box(0, 10, 0, 1, 15, 1), Block.box(2.5, 4, 11, 13.5, 15, 13), Block.box(7, 9, 10, 9, 11, 14)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape WEST = Stream.of(Block.box(0, 0, 0, 16, 10, 16), Block.box(6, 10, 3, 9, 13, 13), Block.box(0, 10, 6, 2, 12, 10), Block.box(8, 15, 0, 16, 16, 16), Block.box(9, 10, 1, 15, 15, 15), Block.box(15, 10, 15, 16, 15, 16), Block.box(8, 10, 15, 9, 15, 16), Block.box(8, 10, 0, 9, 15, 1), Block.box(15, 10, 0, 16, 15, 1), Block.box(3, 4, 2.5, 5, 15, 13.5), Block.box(2, 9, 7, 6, 11, 9)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final DamageSource STATIC_ELECTRIC = new DamageSource("static_electric").bypassArmor().bypassMagic();

    public static final EnumProperty<Progress> PROGRESS = EnumProperty.create("progress", Progress.class);
    public static final BooleanProperty ON_OFF = BooleanProperty.create("on_off");

    public PumpBlock() {
        super(Properties.of(Material.STONE).strength(1f, 1f));

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(PROGRESS, Progress.ZERO)
                .setValue(ON_OFF, false));
    }


    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof PumpBlockEntity)) {
            return InteractionResult.PASS;
        }

        PumpBlockEntity pump = ((PumpBlockEntity) blockEntity);

        if (pump.creative) {
            ItemStack itemInHand = player.getItemInHand(hand);

            InteractionResult result = InteractionResult.PASS;
            // Try a normal bucket
            if (!itemInHand.isEmpty()) {
                if (itemInHand.getItem() instanceof BucketItem) {
                    BucketItem bucketItem = (BucketItem) itemInHand.getItem();
                    pump.creativeFluid = bucketItem.getFluid();
                    sendTileUpdate(level, pos, state, pump);
                    result = InteractionResult.SUCCESS;
                } else {
                    result = itemInHand.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(e -> {
                        // Take the first one
                        pump.creativeFluid = e.getFluidInTank(0).getFluid();
                        sendTileUpdate(level, pos, state, pump);
                        return InteractionResult.SUCCESS;
                    }).orElse(InteractionResult.FAIL);
                }
            }

            if (result.consumesAction()) {
                return result;
            }

            pump.creativeItem = itemInHand.getItem();
            if (!level.isClientSide) {
                sendTileUpdate(level, pos, state, pump);
            }

            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide) {
            if (pump.timeLeft < 6000) {
                pump.timeLeft += 14;
                if (pump.timeLeft > 6000) {
                    pump.timeLeft = 6000;
                }

                computeStateForProgress(state, pos, level, pump.timeLeft);
                sendTileUpdate(level, pos, state, pump);
            } else {
                player.hurt(STATIC_ELECTRIC, 2);
                if (player.getHealth() - 2 < 0) {
                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
                    if (lightning != null) {
                        lightning.moveTo(Vec3.atBottomCenterOf(player.blockPosition()));
                        lightning.setVisualOnly(true);
                        level.addFreshEntity(lightning);
                    }
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    private void sendTileUpdate(Level level, BlockPos pos, BlockState state, PumpBlockEntity tile) {
        tile.setChanged();
        level.sendBlockUpdated(pos, state, state, Constants.BlockFlags.DEFAULT_AND_RERENDER);
    }

    public static void computeStateForProgress(BlockState state, BlockPos pos, Level level, int timeLeft) {
        if (!state.getValue(ON_OFF) && timeLeft > 0) {
            level.setBlock(pos, state.setValue(PumpBlock.ON_OFF, true).setValue(PumpBlock.PROGRESS, Progress.ZERO), 3);
        } else {
            Progress value = state.getValue(PROGRESS);
            if (timeLeft < 1200 && value != Progress.TWENTY) level.setBlock(pos, state.setValue(PumpBlock.ON_OFF, true).setValue(PumpBlock.PROGRESS, Progress.TWENTY), 3);
            else if (timeLeft > 1200 && timeLeft < 2400 && value != Progress.FORTY) level.setBlock(pos, state.setValue(PumpBlock.ON_OFF, true).setValue(PumpBlock.PROGRESS, Progress.FORTY), 3);
            else if (timeLeft > 2400 && timeLeft < 3600 && value != Progress.SIXTY) level.setBlock(pos, state.setValue(PumpBlock.ON_OFF, true).setValue(PumpBlock.PROGRESS, Progress.SIXTY), 3);
            else if (timeLeft > 3600 && timeLeft < 4800 && value != Progress.EIGHTY) level.setBlock(pos, state.setValue(PumpBlock.ON_OFF, true).setValue(PumpBlock.PROGRESS, Progress.EIGHTY), 3);
            else if (timeLeft > 4800 && timeLeft < 5500 && value != Progress.HUNDRED) level.setBlock(pos, state.setValue(PumpBlock.ON_OFF, true).setValue(PumpBlock.PROGRESS, Progress.HUNDRED), 3);
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return SluiceBlockEntities.PUMP.get().create();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PROGRESS, ON_OFF, BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    public VoxelShape getVisualShape(BlockState arg, BlockGetter arg2, BlockPos arg3, CollisionContext arg4) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (dir == Direction.NORTH) return NORTH;
        if (dir == Direction.EAST) return EAST;
        if (dir == Direction.SOUTH) return SOUTH;
        return WEST;
    }
}

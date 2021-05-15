package dev.ftb.mods.sluice.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidUtil;
import org.jetbrains.annotations.Nullable;

public class Tank extends Block {
    public static final BooleanProperty CREATIVE = BooleanProperty.create("creative");

    public Tank(boolean isCreative) {
        super(Properties.of(Material.GLASS));
        this.registerDefaultState(this.getStateDefinition().any()
            .setValue(CREATIVE, isCreative));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        if (level.isClientSide()) {
            return super.use(state, level, pos, player, hand, trace);
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof TankBlockEntity)) {
            return InteractionResult.PASS;
        }

        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof BucketItem) {
            TankBlockEntity tank = (TankBlockEntity) blockEntity;
            boolean fluidInserted = FluidUtil.interactWithFluidHandler(player, hand, tank.tank);
            return InteractionResult.SUCCESS;
        }

        return super.use(state, level, pos, player, hand, trace);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return state.getValue(CREATIVE) ? SluiceModBlockEntities.CREATIVE_TANK.get().create() : SluiceModBlockEntities.TANK.get().create();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CREATIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(CREATIVE, false);
    }
}

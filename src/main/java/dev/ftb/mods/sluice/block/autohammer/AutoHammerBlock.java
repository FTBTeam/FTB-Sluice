package dev.ftb.mods.sluice.block.autohammer;

import dev.ftb.mods.sluice.block.SluiceBlockEntities;
import dev.ftb.mods.sluice.block.SluiceBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static net.minecraft.world.phys.shapes.BooleanOp.OR;

public class AutoHammerBlock extends Block {
    private final Supplier<Item> baseHammerItem;
    private final AutoHammerProperties props;

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public static final VoxelShape EAST_WEST = Stream.of(Block.box(0, 4, 0, 2, 14, 2), Block.box(0, 4, 14, 2, 14, 16), Block.box(14, 4, 0, 16, 14, 2), Block.box(14, 4, 14, 16, 14, 16), Block.box(0, 0, 0, 16, 4, 16), Block.box(0, 14, 0, 16, 16, 16), Block.box(3, 4, 3, 13, 5, 13), Block.box(4, 13, 4, 12, 14, 12), Block.box(4, 4, 0, 12, 12, 2), Block.box(4, 4, 14, 12, 12, 16), Block.box(2, 4, 15, 14, 14, 15), Block.box(2, 4, 1, 14, 14, 1), Block.box(1, 4, 2, 1, 14, 14), Block.box(15, 4, 2, 15, 14, 14)).reduce((v1, v2) -> Shapes.join(v1, v2, OR)).get();
    public static final VoxelShape NORTH_SOUTH = Stream.of(Block.box(0, 4, 14, 2, 14, 16), Block.box(14, 4, 14, 16, 14, 16), Block.box(0, 4, 0, 2, 14, 2), Block.box(14, 4, 0, 16, 14, 2), Block.box(0, 0, 0, 16, 4, 16), Block.box(0, 14, 0, 16, 16, 16), Block.box(3, 4, 3, 13, 5, 13), Block.box(4, 13, 4, 12, 14, 12), Block.box(0, 4, 4, 2, 12, 12), Block.box(14, 4, 4, 16, 12, 12), Block.box(15, 4, 2, 15, 14, 14), Block.box(1, 4, 2, 1, 14, 14), Block.box(2, 4, 15, 14, 14, 15), Block.box(2, 4, 1, 14, 14, 1)).reduce((v1, v2) -> Shapes.join(v1, v2, OR)).get();

    public AutoHammerBlock(Supplier<Item> baseHammerItem, AutoHammerProperties properties) {
        super(Properties.of(Material.STONE).harvestTool(ToolType.PICKAXE).harvestLevel(1).strength(1F, 1F));

        this.props = properties;
        this.baseHammerItem = baseHammerItem;

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, ACTIVE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    public VoxelShape getVisualShape(BlockState arg, BlockGetter arg2, BlockPos arg3, CollisionContext arg4) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            return NORTH_SOUTH;
        } else {
            return EAST_WEST;
        }
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        if (this == SluiceBlocks.IRON_AUTO_HAMMER.get()) {
            return SluiceBlockEntities.IRON_AUTO_HAMMER.get().create();
        } else if (this == SluiceBlocks.GOLD_AUTO_HAMMER.get()) {
            return SluiceBlockEntities.GOLD_AUTO_HAMMER.get().create();
        } else if (this == SluiceBlocks.DIAMOND_AUTO_HAMMER.get()) {
            return SluiceBlockEntities.DIAMOND_AUTO_HAMMER.get().create();
        }
        return SluiceBlockEntities.NETHERITE_AUTO_HAMMER.get().create();
    }
}

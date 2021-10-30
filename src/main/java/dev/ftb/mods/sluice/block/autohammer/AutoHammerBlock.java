package dev.ftb.mods.sluice.block.autohammer;

import dev.ftb.mods.sluice.block.SluiceBlockEntities;
import dev.ftb.mods.sluice.block.SluiceBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class AutoHammerBlock extends Block {
    private final Supplier<Item> baseHammerItem;
    private final AutoHammerProperties props;

    public AutoHammerBlock(Supplier<Item> baseHammerItem, AutoHammerProperties properties) {
        super(Properties.of(Material.STONE).harvestTool(ToolType.PICKAXE).harvestLevel(1).strength(1F, 1F));

        this.props = properties;
        this.baseHammerItem = baseHammerItem;

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
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

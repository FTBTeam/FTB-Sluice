package dev.ftb.mods.sluice.block.pump;

import dev.ftb.mods.sluice.block.SluiceBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
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

    private static final VoxelShape SHAPE = Stream.of(
            Block.box(15, 10, 0, 16, 15, 1),
            Block.box(0, 0, 0, 16, 10, 16),
            Block.box(6, 10, 3, 9, 13, 13),
            Block.box(0, 10, 6, 2, 12, 10),
            Block.box(8, 15, 0, 16, 16, 16),
            Block.box(9, 10, 1, 15, 15, 15),
            Block.box(15, 10, 15, 16, 15, 16),
            Block.box(8, 10, 15, 9, 15, 16),
            Block.box(8, 10, 0, 9, 15, 1)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final DamageSource STATIC_ELECTRIC = new DamageSource("static_electric").bypassArmor().bypassMagic();

    public static final EnumProperty<Progress> PROGRESS = EnumProperty.create("progress", Progress.class);
    public static final BooleanProperty ON_OFF = BooleanProperty.create("on_off");

    public PumpBlock() {
        super(Properties.of(Material.STONE).strength(1f, 1f));

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(PROGRESS, Progress.ZERO)
                .setValue(ON_OFF, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof PumpBlockEntity)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            PumpBlockEntity pump = ((PumpBlockEntity) blockEntity);
            if (pump.creative) {
                return InteractionResult.PASS;
            }

            if (pump.timeLeft < 6000) {
                pump.timeLeft += 14;
                if (pump.timeLeft > 6000) {
                    pump.timeLeft = 6000;
                }

                computeStateForProgress(state, pos, level, pump.timeLeft);

                blockEntity.setChanged();
                level.sendBlockUpdated(pos, state, state, Constants.BlockFlags.DEFAULT_AND_RERENDER);
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
        builder.add(PROGRESS, ON_OFF);
    }

    public VoxelShape getVisualShape(BlockState arg, BlockGetter arg2, BlockPos arg3, CollisionContext arg4) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return SHAPE;
    }
}

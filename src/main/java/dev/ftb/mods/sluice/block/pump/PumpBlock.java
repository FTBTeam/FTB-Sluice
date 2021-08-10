package dev.ftb.mods.sluice.block.pump;

import dev.ftb.mods.sluice.block.SluiceBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

public class PumpBlock extends Block {
    public static final DamageSource STATIC_ELECTRIC = new DamageSource("static_electric").bypassArmor().bypassMagic();

    public PumpBlock() {
        super(Properties.of(Material.STONE).strength(1f, 1f));
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

                blockEntity.setChanged();
                level.sendBlockUpdated(pos, state, state, Constants.BlockFlags.DEFAULT_AND_RERENDER);
            } else {
                player.hurt(STATIC_ELECTRIC, 2);
                if (player.getHealth() - 2 < 0) {
                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
                    if (lightning != null) {
                        lightning.moveTo(Vec3.atBottomCenterOf(player.blockPosition()));
                        lightning.setVisualOnly(true);
                        lightning.setSilent(true);
                        player.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 1F, 1F);
                        player.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 1F, 1F);
                        level.addFreshEntity(lightning);
                    }
                }
            }
        }

        return InteractionResult.SUCCESS;
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
}

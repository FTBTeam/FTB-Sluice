package dev.ftb.mods.sluice.block.pump;

import dev.ftb.mods.sluice.block.SluiceBlockEntities;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
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
import org.jetbrains.annotations.Nullable;

public class PumpBlock extends Block {
    public static final DamageSource STATIC_ELECTRIC = new DamageSource("static").bypassArmor().bypassMagic();

    public PumpBlock() {
        super(Properties.of(Material.STONE).instabreak());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof PumpBlockEntity)) {
            return InteractionResult.PASS;
        }

        PumpBlockEntity pump = ((PumpBlockEntity) blockEntity);
        pump.timeLeft += 9;

        if (pump.timeLeft > 6000) {
            if (!level.isClientSide) {
                player.hurt(STATIC_ELECTRIC, 1);
                if (player.getHealth() - 1 <= 0) {
                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
                    if (lightning != null) {
                        BlockPos position = player.blockPosition();
                        lightning.moveTo(Vec3.atBottomCenterOf(new Vec3i(position.getX(), position.getY(), position.getZ())));
                        lightning.setVisualOnly(false);
                        level.addFreshEntity(lightning);
                    }
                }
            }
        }

        blockEntity.setChanged();

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

package dev.ftb.mods.sluice.item;

import dev.ftb.mods.sluice.FTBSluice;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ClayBucket extends BucketItem {
    private final Fluid content;

    public ClayBucket(Supplier<Fluid> fluid) {
        super(fluid, new Properties().tab(FTBSluice.group).stacksTo(fluid.get() == Fluids.EMPTY ? 16 : 1));
        this.content = fluid.get();
    }

    // This is just minecraft code
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        HitResult raytraceresult = getPlayerPOVHitResult(level, player, this.content == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
        InteractionResultHolder<ItemStack> ret = ForgeEventFactory.onBucketUse(player, level, itemstack, raytraceresult);
        if (ret != null) {
            return ret;
        } else if (raytraceresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        } else if (raytraceresult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemstack);
        } else {
            BlockHitResult blockraytraceresult = (BlockHitResult) raytraceresult;
            BlockPos blockpos = blockraytraceresult.getBlockPos();
            Direction direction = blockraytraceresult.getDirection();
            BlockPos blockpos1 = blockpos.relative(direction);
            if (level.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos1, direction, itemstack)) {
                BlockState blockstate1;
                if (this.content == Fluids.EMPTY) {
                    blockstate1 = level.getBlockState(blockpos);
                    if (blockstate1.getBlock() instanceof BucketPickup && blockstate1.getBlock() instanceof LiquidBlock && ((LiquidBlock) blockstate1.getBlock()).getFluid().is(FluidTags.WATER)) {
                        Fluid fluid = ((BucketPickup) blockstate1.getBlock()).takeLiquid(level, blockpos, blockstate1);
                        if (fluid != Fluids.EMPTY && fluid.is(FluidTags.WATER)) {
                            player.awardStat(Stats.ITEM_USED.get(this));
                            SoundEvent soundevent = this.content.getAttributes().getFillSound();
                            if (soundevent == null) {
                                soundevent = SoundEvents.BUCKET_FILL;
                            }

                            player.playSound(soundevent, 1.0F, 1.0F);
                            ItemStack itemstack1 = ItemUtils.createFilledResult(itemstack, player, new ItemStack(SluiceModItems.CLAY_WATER_BUCKET.get()));
                            if (!level.isClientSide) {
                                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, new ItemStack(SluiceModItems.CLAY_WATER_BUCKET.get()));
                            }

                            return InteractionResultHolder.sidedSuccess(itemstack1, level.isClientSide());
                        }
                    }

                    return InteractionResultHolder.fail(itemstack);
                } else {
                    blockstate1 = level.getBlockState(blockpos);
                    BlockPos blockpos2 = this.canBlockContainFluid(level, blockpos, blockstate1) ? blockpos : blockpos1;
                    if (this.emptyBucket(player, level, blockpos2, blockraytraceresult)) {
                        this.checkExtraContent(level, itemstack, blockpos2);
                        if (player instanceof ServerPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, blockpos2, itemstack);
                        }

                        player.awardStat(Stats.ITEM_USED.get(this));
                        return InteractionResultHolder.sidedSuccess(this.getEmptySuccessItem(itemstack, player), level.isClientSide());
                    } else {
                        return InteractionResultHolder.fail(itemstack);
                    }
                }
            } else {
                return InteractionResultHolder.fail(itemstack);
            }
        }
    }

    public boolean emptyBucket(@Nullable Player p_180616_1_, Level p_180616_2_, BlockPos p_180616_3_, @Nullable BlockHitResult p_180616_4_) {
        if (!(this.content instanceof FlowingFluid)) {
            return false;
        } else {
            BlockState blockstate = p_180616_2_.getBlockState(p_180616_3_);
            Block block = blockstate.getBlock();
            Material material = blockstate.getMaterial();
            boolean flag = blockstate.canBeReplaced(this.content);
            boolean flag1 = blockstate.isAir() || flag || block instanceof LiquidBlockContainer && ((LiquidBlockContainer)block).canPlaceLiquid(p_180616_2_, p_180616_3_, blockstate, this.content);
            if (!flag1) {
                return p_180616_4_ != null && this.emptyBucket(p_180616_1_, p_180616_2_, p_180616_4_.getBlockPos().relative(p_180616_4_.getDirection()), (BlockHitResult)null);
            } else if (p_180616_2_.dimensionType().ultraWarm() && this.content.is(FluidTags.WATER)) {
                int i = p_180616_3_.getX();
                int j = p_180616_3_.getY();
                int k = p_180616_3_.getZ();
                p_180616_2_.playSound(p_180616_1_, p_180616_3_, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (p_180616_2_.random.nextFloat() - p_180616_2_.random.nextFloat()) * 0.8F);

                for(int l = 0; l < 8; ++l) {
                    p_180616_2_.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
                }

                return true;
            } else if (block instanceof LiquidBlockContainer && ((LiquidBlockContainer)block).canPlaceLiquid(p_180616_2_, p_180616_3_, blockstate, this.content)) {
                ((LiquidBlockContainer)block).placeLiquid(p_180616_2_, p_180616_3_, blockstate, ((FlowingFluid)this.content).getSource(false));
                this.playEmptySound(p_180616_1_, p_180616_2_, p_180616_3_);
                return true;
            } else {
                if (!p_180616_2_.isClientSide && flag && !material.isLiquid()) {
                    p_180616_2_.destroyBlock(p_180616_3_, true);
                }

                if (!p_180616_2_.setBlock(p_180616_3_, this.content.defaultFluidState().createLegacyBlock(), 11) && !blockstate.getFluidState().isSource()) {
                    return false;
                } else {
                    this.playEmptySound(p_180616_1_, p_180616_2_, p_180616_3_);
                    return true;
                }
            }
        }
    }

    private boolean canBlockContainFluid(Level worldIn, BlockPos posIn, BlockState blockstate) {
        return blockstate.getBlock() instanceof LiquidBlockContainer && ((LiquidBlockContainer) blockstate.getBlock()).canPlaceLiquid(worldIn, posIn, blockstate, this.content);
    }

    @Override
    protected ItemStack getEmptySuccessItem(ItemStack stack, Player player) {
        return !player.abilities.instabuild ? new ItemStack(SluiceModItems.CLAY_BUCKET.get()) : stack;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ClayBucketFluidHandler(stack);
    }

    @Override
    public Fluid getFluid() {
        return this.content;
    }

    protected void playEmptySound(@Nullable Player p_203791_1_, LevelAccessor p_203791_2_, BlockPos p_203791_3_) {
        SoundEvent soundevent = this.content.getAttributes().getEmptySound();
        if (soundevent == null) {
            soundevent = this.content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        }

        p_203791_2_.playSound(p_203791_1_, p_203791_3_, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

}

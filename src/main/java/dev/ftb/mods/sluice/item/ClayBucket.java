package dev.ftb.mods.sluice.item;

import dev.ftb.mods.sluice.SluiceMod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;

import java.util.function.Supplier;

public class ClayBucket extends BucketItem {
    public static final Fluid CLAY_WATER_FLUID = new ClayWaterFluid.Source();

    private Fluid content;
    public ClayBucket(Supplier<Fluid> fluid) {
        super(fluid, new Properties().tab(SluiceMod.group));
        this.content = fluid.get();
    }

//    @Override
//    public InteractionResultHolder<ItemStack> use(Level p_77659_1_, Player p_77659_2_, InteractionHand p_77659_3_) {
//        ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
//        HitResult raytraceresult = getPlayerPOVHitResult(p_77659_1_, p_77659_2_, this.content == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
//        InteractionResultHolder<ItemStack> ret = ForgeEventFactory.onBucketUse(p_77659_2_, p_77659_1_, itemstack, raytraceresult);
//        if (ret != null) {
//            return ret;
//        } else if (raytraceresult.getType() == HitResult.Type.MISS) {
//            return InteractionResultHolder.pass(itemstack);
//        } else if (raytraceresult.getType() != HitResult.Type.BLOCK) {
//            return InteractionResultHolder.pass(itemstack);
//        } else {
//            BlockHitResult blockraytraceresult = (BlockHitResult)raytraceresult;
//            BlockPos blockpos = blockraytraceresult.getBlockPos();
//            Direction direction = blockraytraceresult.getDirection();
//            BlockPos blockpos1 = blockpos.relative(direction);
//            if (p_77659_1_.mayInteract(p_77659_2_, blockpos) && p_77659_2_.mayUseItemAt(blockpos1, direction, itemstack)) {
//                BlockState blockstate1;
//                if (this.content == Fluids.EMPTY) {
//                    blockstate1 = p_77659_1_.getBlockState(blockpos);
//                    if (blockstate1.getBlock() instanceof BucketPickup) {
//                        Fluid fluid = ((BucketPickup)blockstate1.getBlock()).takeLiquid(p_77659_1_, blockpos, blockstate1);
//                        if (fluid != Fluids.EMPTY) {
//                            p_77659_2_.awardStat(Stats.ITEM_USED.get(this));
//                            SoundEvent soundevent = this.content.getAttributes().getFillSound();
//                            if (soundevent == null) {
//                                soundevent = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
//                            }
//
//                            p_77659_2_.playSound(soundevent, 1.0F, 1.0F);
//                            ItemStack itemstack1 = ItemUtils.createFilledResult(itemstack, p_77659_2_, new ItemStack(fluid.getBucket()));
//                            if (!p_77659_1_.isClientSide) {
//                                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)p_77659_2_, new ItemStack(fluid.getBucket()));
//                            }
//
//                            return InteractionResultHolder.sidedSuccess(itemstack1, p_77659_1_.isClientSide());
//                        }
//                    }
//
//                    return InteractionResultHolder.fail(itemstack);
//                } else {
//                    blockstate1 = p_77659_1_.getBlockState(blockpos);
//                    BlockPos blockpos2 = this.canBlockContainFluid(p_77659_1_, blockpos, blockstate1) ? blockpos : blockpos1;
//                    if (this.emptyBucket(p_77659_2_, p_77659_1_, blockpos2, blockraytraceresult)) {
//                        this.checkExtraContent(p_77659_1_, itemstack, blockpos2);
//                        if (p_77659_2_ instanceof ServerPlayer) {
//                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)p_77659_2_, blockpos2, itemstack);
//                        }
//
//                        p_77659_2_.awardStat(Stats.ITEM_USED.get(this));
//                        return InteractionResultHolder.sidedSuccess(this.getEmptySuccessItem(itemstack, p_77659_2_), p_77659_1_.isClientSide());
//                    } else {
//                        return InteractionResultHolder.fail(itemstack);
//                    }
//                }
//            } else {
//                return InteractionResultHolder.fail(itemstack);
//            }
//        }
//    }

    @Override
    protected ItemStack getEmptySuccessItem(ItemStack p_203790_1_, Player p_203790_2_) {
        return new ItemStack(SluiceModItems.CLAY_BUCKET.get());
    }

    public static abstract class ClayWaterFluid extends WaterFluid {
        @Override
        public Item getBucket() {
            return SluiceModItems.CLAY_BUCKET.get();
        }

        public static class Source extends WaterFluid {
            @Override
            public int getAmount(FluidState p_207192_1_) {
                return 8;
            }

            @Override
            public boolean isSource(FluidState p_207193_1_) {
                return true;
            }
        }
    }
}

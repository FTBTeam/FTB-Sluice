package dev.ftb.mods.sluice.item;

import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClayBucketFluidHandler implements IFluidHandlerItem, ICapabilityProvider {
    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    @Nonnull
    protected ItemStack container;

    public ClayBucketFluidHandler(@Nonnull ItemStack container) {
        this.container = container;
    }

    @Nonnull
    public ItemStack getContainer() {
        return this.container;
    }

    public boolean canFillFluidType(FluidStack fluid) {
        if (fluid.getFluid() != Fluids.WATER && fluid.getFluid() != Fluids.LAVA) {
            return !fluid.getFluid().getAttributes().getBucket(fluid).isEmpty();
        } else {
            return true;
        }
    }

    @Nonnull
    public FluidStack getFluid() {
        Item item = this.container.getItem();
        if (item instanceof ClayBucket) {
            return new FluidStack(((ClayBucket)item).getFluid(), 1000);
        } else {
            return FluidStack.EMPTY;
        }
    }

    protected void setFluid(@Nonnull FluidStack fluidStack) {
        if (fluidStack.isEmpty()) {
            this.container = new ItemStack(SluiceModItems.CLAY_BUCKET.get());
        } else {
            this.container = new ItemStack(SluiceModItems.CLAY_WATER_BUCKET.get());
        }
    }

    public int getTanks() {
        return 1;
    }

    @Nonnull
    public FluidStack getFluidInTank(int tank) {
        return this.getFluid();
    }

    public int getTankCapacity(int tank) {
        return 1000;
    }

    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return true;
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (this.container.getCount() == 1 && resource.getAmount() >= 1000 && this.getFluid().isEmpty() && this.canFillFluidType(resource)) {
            if (action.execute()) {
                this.setFluid(resource);
            }

            return 1000;
        } else {
            return 0;
        }
    }

    @Nonnull
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (this.container.getCount() == 1 && resource.getAmount() >= 1000) {
            FluidStack fluidStack = this.getFluid();
            if (!fluidStack.isEmpty() && fluidStack.isFluidEqual(resource)) {
                if (action.execute()) {
                    this.setFluid(FluidStack.EMPTY);
                }

                return fluidStack;
            } else {
                return FluidStack.EMPTY;
            }
        } else {
            return FluidStack.EMPTY;
        }
    }

    @Nonnull
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        if (this.container.getCount() == 1 && maxDrain >= 1000) {
            FluidStack fluidStack = this.getFluid();
            if (!fluidStack.isEmpty()) {
                if (action.execute()) {
                    this.setFluid(FluidStack.EMPTY);
                }

                return fluidStack;
            } else {
                return FluidStack.EMPTY;
            }
        } else {
            return FluidStack.EMPTY;
        }
    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(capability, this.holder);
    }
}
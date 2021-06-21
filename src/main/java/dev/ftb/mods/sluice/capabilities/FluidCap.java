package dev.ftb.mods.sluice.capabilities;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * This provides two new consumers to allow us to modify the block based on the
 * fluid cap. We also by default protect the IO of the cap unless we've stated
 * that the block entity can be public.
 */
public class FluidCap extends FluidTank {
    private final boolean isProtected;

    public FluidCap(boolean isProtected, int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);

        this.isProtected = isProtected;
    }

    public int internalFill(FluidStack resource, FluidAction action) {
        return super.fill(resource, action);
    }

    public FluidStack internalDrain(int maxDrain, FluidAction action) {
        return super.drain(maxDrain, action);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
//        if (this.isProtected) {
//            return 0;
//        }

        return this.internalFill(resource, action);
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (this.isProtected) {
            return FluidStack.EMPTY;
        }

        return this.internalDrain(maxDrain, action);
    }
}

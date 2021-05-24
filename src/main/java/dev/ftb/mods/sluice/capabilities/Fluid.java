package dev.ftb.mods.sluice.capabilities;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * This provides two new consumers to allow us to modify the block based on the
 * fluid cap. We also by default protect the IO of the cap unless we've stated
 * that the block entity can be public.
 */
public class Fluid extends FluidTank {
    private final TriConsumer<Fluid, Integer, FluidAction> onFill;
    private final TriConsumer<Fluid, Integer, FluidAction> onDrain;
    private final boolean isProtected;

    public Fluid(boolean isProtected, int capacity, Predicate<FluidStack> validator, TriConsumer<Fluid, Integer, FluidAction> onFill, TriConsumer<Fluid, Integer, FluidAction> onDrain) {
        super(capacity, validator);

        this.isProtected = isProtected;
        this.onFill = onFill;
        this.onDrain = onDrain;
    }

    public int internalFill(FluidStack resource, FluidAction action) {
        int fill = super.fill(resource, action);
        this.onFill.accept(this, fill, action);
        return fill;
    }

    public FluidStack internalDrain(int maxDrain, FluidAction action) {
        FluidStack drain = super.drain(maxDrain, action);
        this.onDrain.accept(this, maxDrain, action);
        return drain;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (this.isProtected) {
            return 0;
        }

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

package dev.ftb.mods.sluice.capabilities;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class Fluid extends FluidTank {
    private final TriConsumer<Fluid, Integer, FluidAction> onFill;
    private final TriConsumer<Fluid, Integer, FluidAction> onDrain;

    public Fluid(int capacity, Predicate<FluidStack> validator, TriConsumer<Fluid, Integer, FluidAction> onFill, TriConsumer<Fluid, Integer, FluidAction> onDrain) {
        super(capacity, validator);

        this.onFill = onFill;
        this.onDrain = onDrain;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int fill = super.fill(resource, action);
        this.onFill.accept(this, fill, action);
        return fill;
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack drain = super.drain(maxDrain, action);
        this.onDrain.accept(this, maxDrain, action);
        return drain;
    }
}

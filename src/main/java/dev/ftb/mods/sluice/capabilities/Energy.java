package dev.ftb.mods.sluice.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

import java.util.function.Consumer;

public class Energy extends EnergyStorage implements INBTSerializable<CompoundTag> {
    private static final String KEY = "energy";
    private final Consumer<Energy> onEnergyChange;

    public Energy(int capacity, Consumer<Energy> onEnergyChange) {
        super(capacity);

        this.onEnergyChange = onEnergyChange;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, maxReceive));

        if (!simulate) {
            this.energy += energyReceived;
            this.onEnergyChange.accept(this);
        }

        return energyReceived;
    }

    public int consumeEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(this.energy, Math.min(this.maxExtract, maxExtract));

        if (!simulate) {
            this.energy -= energyExtracted;
            this.onEnergyChange.accept(this);
        }

        return energyExtracted;
    }

    // We don't use this method and thus we don't let other people use it either
    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY, this.energy);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.energy = nbt.getInt(KEY);
    }
}

package dev.ftb.mods.sluice.recipe;

import java.util.List;

public class SluiceRecipeInfo {
    private final List<ItemWithWeight> items;
    private final int maxDrops;
    private final int processingTime;
    private final int fluidUsed;

    public SluiceRecipeInfo(List<ItemWithWeight> items, int maxDrops, int processingTime, int fluidUsed) {
        this.items = items;
        this.maxDrops = maxDrops;
        this.processingTime = processingTime;
        this.fluidUsed = fluidUsed;
    }

    public List<ItemWithWeight> getItems() {
        return this.items;
    }

    public int getMaxDrops() {
        return this.maxDrops;
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    public int getFluidUsed() {
        return this.fluidUsed;
    }
}

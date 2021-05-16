package dev.ftb.mods.sluice.recipe;

import java.util.List;

public class InputRecipeResult {
    private final List<ItemWithWeight> items;
    private final int maxDrops;

    public InputRecipeResult(List<ItemWithWeight> items, int maxDrops) {
        this.items = items;
        this.maxDrops = maxDrops;
    }

    public List<ItemWithWeight> getItems() {
        return this.items;
    }

    public int getMaxDrops() {
        return this.maxDrops;
    }
}

package dev.ftb.mods.sluice.block.autohammer;

import dev.ftb.mods.sluice.item.SluiceModItems;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public enum AutoHammerProperties {
    IRON(SluiceModItems.IRON_HAMMER),
    GOLD(SluiceModItems.GOLD_HAMMER),
    DIAMOND(SluiceModItems.DIAMOND_HAMMER),
    NETHERITE(SluiceModItems.NETHERITE_HAMMER);

    Supplier<Item> hammerItem;

    AutoHammerProperties(Supplier<Item> hammerItem) {
        this.hammerItem = hammerItem;
    }

    public Supplier<Item> getHammerItem() {
        return hammerItem;
    }
}

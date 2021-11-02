package dev.ftb.mods.sluice.block.autohammer;

import dev.ftb.mods.sluice.FTBSluice;
import dev.ftb.mods.sluice.SluiceConfig;
import dev.ftb.mods.sluice.item.SluiceModItems;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Supplier;

public enum AutoHammerProperties {
    IRON(SluiceModItems.IRON_HAMMER, SluiceConfig.HAMMERS.speedIron),
    GOLD(SluiceModItems.GOLD_HAMMER , SluiceConfig.HAMMERS.speedGold),
    DIAMOND(SluiceModItems.DIAMOND_HAMMER, SluiceConfig.HAMMERS.speedDiamond),
    NETHERITE(SluiceModItems.NETHERITE_HAMMER, SluiceConfig.HAMMERS.speedNetherite);

    Supplier<Item> hammerItem;
    ForgeConfigSpec.IntValue hammerSpeed;

    AutoHammerProperties(Supplier<Item> hammerItem, ForgeConfigSpec.IntValue hammerSpeed) {
        this.hammerItem = hammerItem;
        this.hammerSpeed = hammerSpeed;
    }

    public Supplier<Item> getHammerItem() {
        return hammerItem;
    }

    public ForgeConfigSpec.IntValue getHammerSpeed() {
        return hammerSpeed;
    }
}

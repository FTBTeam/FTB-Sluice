package dev.ftb.mods.sluice.block.sluice;

import dev.ftb.mods.sluice.SluiceConfig.CategorySluice;

import static dev.ftb.mods.sluice.SluiceConfig.SLUICES;

public enum SluiceProperties {
    OAK(false, false, false, SLUICES.OAK),
    IRON(true, false, false, SLUICES.IRON),
    DIAMOND(true, true, false, SLUICES.DIAMOND),
    NETHERITE(true, true, true, SLUICES.NETHERITE);

    final boolean allowsIO;
    final boolean allowsTank;
    final boolean upgradeable;
    final CategorySluice config;


    SluiceProperties(boolean allowsIO, boolean allowsTank, boolean upgradeable, CategorySluice config) {
        this.allowsIO = allowsIO;
        this.allowsTank = allowsTank;
        this.upgradeable = upgradeable;
        this.config = config;
    }
}

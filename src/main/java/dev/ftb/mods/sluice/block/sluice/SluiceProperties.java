package dev.ftb.mods.sluice.block.sluice;

import dev.ftb.mods.sluice.SluiceConfig.CategorySluice;

import static dev.ftb.mods.sluice.SluiceConfig.SLUICES;

public enum SluiceProperties {
    OAK(SLUICES.OAK),
    IRON(SLUICES.IRON),
    DIAMOND(SLUICES.DIAMOND),
    NETHERITE(SLUICES.NETHERITE),
    EMPOWERED(SLUICES.EMPOWERED),;

    public final CategorySluice config;

    SluiceProperties(CategorySluice config) {
        this.config = config;
    }
}

package dev.ftb.mods.sluice.block.sluice;

import dev.ftb.mods.sluice.block.SluiceBlocks;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.function.Supplier;

import static dev.ftb.mods.sluice.SluiceConfig.SLUICES;

public enum SluiceProperties {
    OAK(false, false, false, SLUICES.oakTimeMod::get, SLUICES.oakFluidMod::get, SLUICES.oakTank::get),
    IRON(true, false, false, SLUICES.ironTimeMod::get, SLUICES.ironFluidMod::get, SLUICES.ironTank::get),
    DIAMOND(true, true, false, SLUICES.diamondTimeMod::get, SLUICES.diamondFluidMod::get, SLUICES.diamondTank::get),
    NETHERITE(true, true, true, SLUICES.netheriteTimeMod::get, SLUICES.netheriteFluidMod::get, SLUICES.netheriteTank::get);

    boolean allowsIO;
    boolean allowsTank;
    boolean upgradeable;
    Supplier<Double> processingTime;
    Supplier<Double> fluidUsage;
    Supplier<Integer> tankCap;

    SluiceProperties(boolean allowsIO, boolean allowsTank, boolean upgradeable, Supplier<Double> processingTime, Supplier<Double> fluidUsage, Supplier<Integer> tankCap) {
        this.processingTime = processingTime;
        this.upgradeable = upgradeable;
        this.fluidUsage = fluidUsage;
        this.allowsIO = allowsIO;
        this.allowsTank = allowsTank;
        this.tankCap = tankCap;
    }
}

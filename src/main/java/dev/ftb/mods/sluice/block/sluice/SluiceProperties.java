package dev.ftb.mods.sluice.block.sluice;

import dev.ftb.mods.sluice.block.SluiceBlocks;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.function.Supplier;

import static dev.ftb.mods.sluice.SluiceConfig.SLUICES;

public enum SluiceProperties {
    OAK(false, false, SluiceBlocks.OAK_SLUICE.get(), SLUICES.oakTimeMod::get, SLUICES.oakFluidMod::get),
    IRON(true, false, SluiceBlocks.IRON_SLUICE.get(), SLUICES.ironTimeMod::get, SLUICES.ironFluidMod::get),
    DIAMOND(true, true, SluiceBlocks.DIAMOND_SLUICE.get(), SLUICES.diamondTimeMod::get, SLUICES.diamondFluidMod::get),
    NETHERITE(true, true, SluiceBlocks.NETHERITE_SLUICE.get(), SLUICES.netheriteTimeMod::get, SLUICES.netheriteFluidMod::get);

    Block relatedBlock;
    boolean allowsIO;
    boolean allowsTank;
    Supplier<Double> processingTime;
    Supplier<Double> fluidUsage;

    SluiceProperties(boolean allowsIO, boolean allowsTank, Block relatedBlock, Supplier<Double> processingTime, Supplier<Double> fluidUsage) {
        this.relatedBlock = relatedBlock;
        this.processingTime = processingTime;
        this.fluidUsage = fluidUsage;
        this.allowsIO = allowsIO;
        this.allowsTank = allowsTank;
    }

    public static SluiceProperties getFromBlock(Block block) {
        return Arrays.stream(SluiceProperties.values()).filter(e -> e.relatedBlock == block).findFirst().orElse(SluiceProperties.OAK);
    }
}

package dev.ftb.mods.sluice.block;

import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.function.Supplier;

import static dev.ftb.mods.sluice.SluiceConfig.SLUICES;

public enum SluiceProperties {
    OAK(false, false, SluiceBlocks.OAK_SLUICE.get(), SLUICES.oakSluiceProcessing::get, SLUICES.oakSluiceFluid::get),
    IRON(true, false, SluiceBlocks.IRON_SLUICE.get(), SLUICES.ironSluiceProcessing::get, SLUICES.ironSluiceFluid::get),
    DIAMOND(true, true, SluiceBlocks.DIAMOND_SLUICE.get(), SLUICES.diamondSluiceProcessing::get, SLUICES.diamondSluiceFluid::get),
    NETHERITE(true, true, SluiceBlocks.NETHERITE_SLUICE.get(), SLUICES.netheriteSluiceProcessing::get, SLUICES.netheriteSluiceFluid::get);

    Block relatedBlock;
    boolean allowsIO;
    boolean allowsTank;
    Supplier<Integer> processingTime;
    Supplier<Integer> fluidUsage;

    SluiceProperties(boolean allowsIO, boolean allowsTank, Block relatedBlock, Supplier<Integer> processingTime, Supplier<Integer> fluidUsage) {
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

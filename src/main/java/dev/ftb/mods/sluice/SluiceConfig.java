package dev.ftb.mods.sluice;

import net.minecraftforge.common.ForgeConfigSpec;

public class SluiceConfig {
    public static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec COMMON_CONFIG;

    public static final CategoryGeneral GENERAL = new CategoryGeneral();
    public static final CategorySluices SLUICES = new CategorySluices();

    static {
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static class CategoryGeneral {
        public final ForgeConfigSpec.IntValue maxUpgradeStackSize;
        public final ForgeConfigSpec.DoubleValue exponentialCostBaseN;

        public CategoryGeneral() {
            COMMON_BUILDER.push("general");

            this.maxUpgradeStackSize = COMMON_BUILDER
                    .comment("Allows you to increase the amount of upgrades that can be put within a single stack. This is not something you should change as it can mess with math but if you opt too, good luck.")
                    .defineInRange("Max upgrade stack size", 18, 1, 64);

            this.exponentialCostBaseN = COMMON_BUILDER
                    .comment("Exponential cost N amount. We start with 1.35 as it's a good max number at around 10M RF per operation")
                    .defineInRange("Exponential cost base N value", 1.35D, 1D, 2D);

            COMMON_BUILDER.pop();
        }
    }

    public static class CategorySluices {

        public final CategorySluice OAK, IRON, DIAMOND;
        public final CategoryNetheriteSluice NETHERITE;
        public final CategoryNetheriteSluice EMPOWERED;

        public CategorySluices() {
            COMMON_BUILDER.push("sluices");

            OAK = new CategorySluice("oak", 1, 1, 12000);
            IRON = new CategorySluice("iron", .8, .6, 12000);
            DIAMOND = new CategorySluice("diamond", .6, .75, 12000);
            NETHERITE = new CategoryNetheriteSluice("netherite", 40,.4, .5, 12000);
            EMPOWERED = new CategoryNetheriteSluice("empowered", 80,.2, .2, 25000);
        }
    }

    public static class CategorySluice {

        public final ForgeConfigSpec.DoubleValue timeMod;
        public final ForgeConfigSpec.DoubleValue fluidMod;
        public final ForgeConfigSpec.IntValue tankCap;

        public CategorySluice(String name, double timeMod, double fluidMod, int tankCap) {
            COMMON_BUILDER.push(name);

            this.timeMod = COMMON_BUILDER
                    .comment("Defines how long it takes to process a resource in this Sluice (This is multiplied by the recipe base tick time)")
                    .defineInRange("processing time multiplier", timeMod, 0.0, 1000.0);

            this.fluidMod = COMMON_BUILDER
                    .comment("Sets how much fluid is used per processed recipe (This is multiplied by the recipe's fluid consumption rate)")
                    .defineInRange("fluid multiplier", fluidMod, 0.0, 1000.0);

            this.tankCap = COMMON_BUILDER
                    .comment("Sets how much fluid this sluice's tank can carry (in mB)")
                    .defineInRange("tank capacity", tankCap, 0, 1000000);

            addOtherValues();

            COMMON_BUILDER.pop();
        }

        public void addOtherValues() {
        }
    }

    public static class CategoryNetheriteSluice extends CategorySluice {
        public ForgeConfigSpec.IntValue costPerUse;
        public int energyCost;

        public CategoryNetheriteSluice(String name, int energyCost, double timeMod, double fluidMod, int tankCap) {
            super(name, timeMod, fluidMod, tankCap);
            this.energyCost = energyCost;
        }

        @Override
        public void addOtherValues() {
            this.costPerUse = COMMON_BUILDER
                    .comment("FE cost per use")
                    .defineInRange("fe cost per use", energyCost, 0, Integer.MAX_VALUE);
        }
    }
}

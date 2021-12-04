package dev.ftb.mods.sluice;

import net.minecraftforge.common.ForgeConfigSpec;

public class SluiceConfig {
    public static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec COMMON_CONFIG;

    public static final CategoryGeneral GENERAL = new CategoryGeneral();
    public static final CategorySluices SLUICES = new CategorySluices();
    public static final CategoryHammers HAMMERS = new CategoryHammers();

    static {
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static class CategoryGeneral {
        public final ForgeConfigSpec.IntValue maxUpgradeStackSize;
        public final ForgeConfigSpec.DoubleValue exponentialCostBaseN;

        public CategoryGeneral() {
            COMMON_BUILDER.push("general");

            this.maxUpgradeStackSize = COMMON_BUILDER.comment(
                    "Allows you to increase the amount of upgrades that can be put within a single stack. This is not something you should change as it can mess with math but if you opt too, good luck.")
                    .defineInRange("Max upgrade stack size", 18, 1, 64);

            this.exponentialCostBaseN = COMMON_BUILDER.comment(
                    "Exponential cost N amount. We start with 1.35 as it's a good max number at around 10M RF per operation")
                    .defineInRange("Exponential cost base N value", 1.35D, 1D, 2D);

            COMMON_BUILDER.pop();
        }
    }

    public static class CategorySluices {

        public final CategorySluice OAK, IRON, DIAMOND, NETHERITE, EMPOWERED;

        public CategorySluices() {
            COMMON_BUILDER.push("sluices");

            OAK = new CategorySluice("oak", 1, 1, 12000, false, false, false, 0);
            IRON = new CategorySluice("iron", .8, .6, 12000, true, false, false, 0);
            DIAMOND = new CategorySluice("diamond", .6, .75, 12000, true, true, false, 0);
            NETHERITE = new CategorySluice("netherite", .4, .5, 12000, true, true, true, 40);
            EMPOWERED = new CategorySluice("empowered", .2, .2, 25000, true, true, true, 80);

            COMMON_BUILDER.pop();
        }
    }

    public static class CategorySluice {

        public final ForgeConfigSpec.DoubleValue timeMod;
        public final ForgeConfigSpec.DoubleValue fluidMod;
        public final ForgeConfigSpec.IntValue tankCap;
        public final ForgeConfigSpec.BooleanValue allowsIO;
        public final ForgeConfigSpec.BooleanValue allowsTank;
        public final ForgeConfigSpec.BooleanValue upgradeable;
        public ForgeConfigSpec.IntValue costPerUse;

        public CategorySluice(String name, double timeMod, double fluidMod, int tankCap, boolean allowsIO, boolean allowsTank, boolean upgradeable, int energyCost) {
            COMMON_BUILDER.push(name);

            this.timeMod = COMMON_BUILDER.comment(
                    "Defines how long it takes to process a resource in this Sluice (This is multiplied by the recipe base tick time)")
                    .defineInRange("processing time multiplier", timeMod, 0.0, 1000.0);

            this.fluidMod = COMMON_BUILDER.comment(
                    "Sets how much fluid is used per processed recipe (This is multiplied by the recipe's fluid consumption rate)")
                    .defineInRange("fluid multiplier", fluidMod, 0.0, 1000.0);

            this.tankCap = COMMON_BUILDER
                    .comment("Sets how much fluid this sluice's tank can carry (in mB)")
                    .defineInRange("tank capacity", tankCap, 0, 1000000);

            this.allowsIO = COMMON_BUILDER.comment("Allows this sluice to be used for item IO")
                    .define("allowsIO", allowsIO);

            this.allowsTank = COMMON_BUILDER.comment("Allows this sluice to be used for fluid IO")
                    .define("allowsTank", allowsTank);

            this.upgradeable = COMMON_BUILDER.comment("Allows this sluice to be upgraded")
                    .define("upgradeable", upgradeable);

            this.costPerUse = COMMON_BUILDER.comment("FE cost per use")
                    .defineInRange("fe cost per use", energyCost, 0, Integer.MAX_VALUE);

            addOtherValues();

            COMMON_BUILDER.pop();
        }

        public void addOtherValues() {}
    }
    public static class CategoryHammers {
        public final ForgeConfigSpec.IntValue speedIron;
        public final ForgeConfigSpec.IntValue speedGold;
        public final ForgeConfigSpec.IntValue speedDiamond;
        public final ForgeConfigSpec.IntValue speedNetherite;

        private CategoryHammers() {
            COMMON_BUILDER.push("hammers");

            this.speedIron = COMMON_BUILDER
                    .comment("Speed of the iron auto-hammer as ticks taken to process the block")
                    .defineInRange("ironSpeed", 50, 1, 100000);

            this.speedGold = COMMON_BUILDER
                    .comment("Speed of the gold auto-hammer as ticks taken to process the block")
                    .defineInRange("goldSpeed", 40, 1, 100000);

            this.speedDiamond = COMMON_BUILDER
                    .comment("Speed of the diamond auto-hammer as ticks taken to process the block")
                    .defineInRange("diamondSpeed", 30, 1, 100000);

            this.speedNetherite = COMMON_BUILDER.comment(
                    "Speed of the netherite auto-hammer as ticks taken to process the block")
                    .defineInRange("netheriteSpeed", 15, 1, 100000);

            COMMON_BUILDER.pop();
        }
    }
}

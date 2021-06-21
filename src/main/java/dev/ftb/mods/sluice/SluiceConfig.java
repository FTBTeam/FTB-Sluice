package dev.ftb.mods.sluice;

import net.minecraftforge.common.ForgeConfigSpec;

public class SluiceConfig {
    public static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec COMMON_CONFIG;

    public static final CategoryGeneral GENERAL = new CategoryGeneral();
    public static final CategorySluice SLUICES = new CategorySluice();
    public static final CategoryNetheriteSluice NETHERITE_SLUICE = new CategoryNetheriteSluice();

    static {
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static class CategoryGeneral {
        public final ForgeConfigSpec.IntValue maxUpgradeStackSize;
        public final ForgeConfigSpec.DoubleValue percentageCostPerUpgrade;

        public CategoryGeneral() {
            COMMON_BUILDER.push("general");

            this.maxUpgradeStackSize = COMMON_BUILDER
                    .comment("Allows you to increase the amount of upgrades that can be put within a single stack. This is not something you should change as it can mess with math but if you opt too, good luck.")
                    .defineInRange("Max upgrade stack size", 18, 1, 64);

            this.percentageCostPerUpgrade = COMMON_BUILDER
                    .comment("The amount of power an upgrade will consume. This uses the base power as a base and uses the percentage to calculate how much power to consume.", "For example, If the Sluice uses 100FE per operation and your sluice had a single upgrade with this option set to 5.0. Your sluice now uses 5 extra FE per operation")
                    .defineInRange("Percentage cost per upgrade", 2.0D, 0D, 100D);

            COMMON_BUILDER.pop();
        }
    }

    public static class CategorySluice {
        public final ForgeConfigSpec.IntValue tankStorage;

        public final ForgeConfigSpec.DoubleValue oakTimeMod;
        public final ForgeConfigSpec.DoubleValue ironTimeMod;
        public final ForgeConfigSpec.DoubleValue diamondTimeMod;
        public final ForgeConfigSpec.DoubleValue netheriteTimeMod;

        public final ForgeConfigSpec.DoubleValue oakFluidMod;
        public final ForgeConfigSpec.DoubleValue ironFluidMod;
        public final ForgeConfigSpec.DoubleValue diamondFluidMod;
        public final ForgeConfigSpec.DoubleValue netheriteFluidMod;

        public CategorySluice() {
            COMMON_BUILDER.push("sluices");

            this.tankStorage = COMMON_BUILDER
                    .comment("How much fluid can be put into the sluices tank")
                    .defineInRange("tank storage", 10000, 2000, 100000);

            this.oakTimeMod = COMMON_BUILDER
                    .comment("Defines how long it takes to process a resource in this Sluice (This is multiplied by the recipe base tick time)")
                    .defineInRange("oak processing time multiplier", 1, 0.0, 1000.0);
            this.ironTimeMod = COMMON_BUILDER
                    .comment("Defines how long it takes to process a resource in this Sluice (This is multiplied by the recipe base tick time)")
                    .defineInRange("iron processing time multiplier", .8, 0.0, 1000.0);
            this.diamondTimeMod = COMMON_BUILDER
                    .comment("Defines how long it takes to process a resource in this Sluice (This is multiplied by the recipe base tick time)")
                    .defineInRange("diamond processing time multiplier", .6, 0.0, 1000.0);
            this.netheriteTimeMod = COMMON_BUILDER
                    .comment("Defines how long it takes to process a resource in this Sluice (This is multiplied by the recipe base tick time)")
                    .defineInRange("netherite processing time multiplier", .4, 0.0, 1000.0);

            this.oakFluidMod = COMMON_BUILDER
                    .comment("Sets how much fluid is used per processed recipe (This is multiplied by the recipe's fluid consumption rate)")
                    .defineInRange("oak fluid multiplier", 1, 0.0, 1000.0);
            this.ironFluidMod = COMMON_BUILDER
                    .comment("Sets how much fluid is used per processed recipe (This is multiplied by the recipe's fluid consumption rate)")
                    .defineInRange("iron fluid multiplier", .8, 0.0, 1000.0);
            this.diamondFluidMod = COMMON_BUILDER
                    .comment("Sets how much fluid is used per processed recipe (This is multiplied by the recipe's fluid consumption rate)")
                    .defineInRange("diamond fluid multiplier", .6, 0.0, 1000.0);
            this.netheriteFluidMod = COMMON_BUILDER
                    .comment("Sets how much fluid is used per processed recipe (This is multiplied by the recipe's fluid consumption rate)")
                    .defineInRange("netherite fluid multiplier", .4, 0.0, 1000.0);

            COMMON_BUILDER.pop();
        }
    }

    public static class CategoryNetheriteSluice {
        public ForgeConfigSpec.IntValue energyStorage;
        public ForgeConfigSpec.IntValue costPerUse;

        public CategoryNetheriteSluice() {
            COMMON_BUILDER.push("netherite sluice");

            this.energyStorage = COMMON_BUILDER
                    .comment("How much FE the netherite sluice can store")
                    .defineInRange("fe storage", 10000, 0, 5000000);

            this.costPerUse = COMMON_BUILDER
                    .comment("FE cost per use")
                    .defineInRange("fe cost per use", 40, 0, 1000);

            COMMON_BUILDER.pop();
        }
    }
}

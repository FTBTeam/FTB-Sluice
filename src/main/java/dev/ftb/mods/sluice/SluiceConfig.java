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

        public final ForgeConfigSpec.IntValue oakSluiceProcessing;
        public final ForgeConfigSpec.IntValue ironSluiceProcessing;
        public final ForgeConfigSpec.IntValue diamondSluiceProcessing;
        public final ForgeConfigSpec.IntValue netheriteSluiceProcessing;

        public final ForgeConfigSpec.IntValue oakSluiceFluid;
        public final ForgeConfigSpec.IntValue ironSluiceFluid;
        public final ForgeConfigSpec.IntValue diamondSluiceFluid;
        public final ForgeConfigSpec.IntValue netheriteSluiceFluid;

        public CategorySluice() {
            COMMON_BUILDER.push("sluices");

            this.tankStorage = COMMON_BUILDER
                    .comment("How much fluid can be put into the sluices tank")
                    .defineInRange("tank storage", 10000, 2000, 100000);

            this.oakSluiceProcessing = COMMON_BUILDER
                    .comment("Defines how long it takes to process a resource in this sluice (in ticks)")
                    .defineInRange("oak sluice processing time", 100, 0, 1000);
            this.ironSluiceProcessing = COMMON_BUILDER
                    .comment("Defines how long it takes to process a resource in this sluice (in ticks)")
                    .defineInRange("iron sluice processing time", 80, 0, 1000);
            this.diamondSluiceProcessing = COMMON_BUILDER
                    .comment("Defines how long it takes to process a resource in this sluice (in ticks)")
                    .defineInRange("diamond sluice processing time", 60, 0, 1000);
            this.netheriteSluiceProcessing = COMMON_BUILDER
                    .comment("Defines how long it takes to process a resource in this sluice (in ticks)")
                    .defineInRange("netherite sluice processing time", 40, 0, 1000);

            this.oakSluiceFluid = COMMON_BUILDER
                    .comment("Sets how much fluid the sluice uses per use")
                    .defineInRange("oak sluice fluid consumption", 1000, 0, 10000);
            this.ironSluiceFluid = COMMON_BUILDER
                    .comment("Sets how much fluid the sluice uses per use")
                    .defineInRange("iron sluice fluid consumption", 800, 0, 10000);
            this.diamondSluiceFluid = COMMON_BUILDER
                    .comment("Sets how much fluid the sluice uses per use")
                    .defineInRange("diamond sluice fluid consumption", 600, 0, 10000);
            this.netheriteSluiceFluid = COMMON_BUILDER
                    .comment("Sets how much fluid the sluice uses per use")
                    .defineInRange("netherite sluice fluid consumption", 400, 0, 10000);

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

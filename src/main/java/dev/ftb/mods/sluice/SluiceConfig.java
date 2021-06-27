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

        public final ForgeConfigSpec.IntValue oakTank;
        public final ForgeConfigSpec.IntValue ironTank;
        public final ForgeConfigSpec.IntValue diamondTank;
        public final ForgeConfigSpec.IntValue netheriteTank;

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

            this.oakTank = COMMON_BUILDER
                    .comment("Sets how much fluid this sluice's tank can carry (in mB)")
                    .defineInRange("oak tank capacity", 10000, 0, 1000000);
            this.ironTank = COMMON_BUILDER
                    .comment("Sets how much fluid this sluice's tank can carry (in mB)")
                    .defineInRange("iron tank capacity", 10000, 0, 1000000);
            this.diamondTank = COMMON_BUILDER
                    .comment("Sets how much fluid this sluice's tank can carry (in mB)")
                    .defineInRange("diamond tank capacity", 10000, 0, 1000000);
            this.netheriteTank = COMMON_BUILDER
                    .comment("Sets how much fluid this sluice's tank can carry (in mB)")
                    .defineInRange("netherite tank capacity", 10000, 0, 1000000);

            COMMON_BUILDER.pop();
        }
    }

    public static class CategoryNetheriteSluice {
        public ForgeConfigSpec.IntValue costPerUse;

        public CategoryNetheriteSluice() {
            COMMON_BUILDER.push("netherite sluice");

            this.costPerUse = COMMON_BUILDER
                    .comment("FE cost per use")
                    .defineInRange("fe cost per use", 40, 0, 1000);

            COMMON_BUILDER.pop();
        }
    }
}

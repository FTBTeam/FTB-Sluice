package dev.ftb.mods.sluice;

import net.minecraftforge.common.ForgeConfigSpec;

public class SluiceConfig {
    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    public static final CategoryGeneral GENERAL = new CategoryGeneral();
    public static final CategorySluice SLUICES = new CategorySluice();
    public static final CategoryNetheriteSluice NETHERITE_SLUICE = new CategoryNetheriteSluice();

    public static class CategoryGeneral {
        public CategoryGeneral() {
            COMMON_BUILDER.push("general");


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

            tankStorage = COMMON_BUILDER
                .comment("How much fluid can be put into the sluices tank")
                .defineInRange("tank storage", 2000, 1000, 10000);

            oakSluiceProcessing = COMMON_BUILDER
                .comment("Defines how long it takes to process a resource in this sluice (in ticks)")
                .defineInRange("oak sluice processing time", 100, 0, 1000);
            ironSluiceProcessing = COMMON_BUILDER
                .comment("Defines how long it takes to process a resource in this sluice (in ticks)")
                .defineInRange("iron sluice processing time", 80, 0, 1000);
            diamondSluiceProcessing = COMMON_BUILDER
                .comment("Defines how long it takes to process a resource in this sluice (in ticks)")
                .defineInRange("diamond sluice processing time", 60, 0, 1000);
            netheriteSluiceProcessing = COMMON_BUILDER
                .comment("Defines how long it takes to process a resource in this sluice (in ticks)")
                .defineInRange("netherite sluice processing time", 40, 0, 1000);

            oakSluiceFluid = COMMON_BUILDER
                .comment("Sets how much fluid the sluice uses per use")
                .defineInRange("oak sluice fluid consumption", 1000, 0, 10000);
            ironSluiceFluid = COMMON_BUILDER
                .comment("Sets how much fluid the sluice uses per use")
                .defineInRange("iron sluice fluid consumption", 800, 0, 10000);
            diamondSluiceFluid = COMMON_BUILDER
                .comment("Sets how much fluid the sluice uses per use")
                .defineInRange("diamond sluice fluid consumption", 600, 0, 10000);
            netheriteSluiceFluid = COMMON_BUILDER
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

            COMMON_BUILDER.pop();
        }
    }

    public static final ForgeConfigSpec COMMON_CONFIG = COMMON_BUILDER.build();
}

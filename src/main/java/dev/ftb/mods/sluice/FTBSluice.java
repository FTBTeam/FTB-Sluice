package dev.ftb.mods.sluice;

import dev.ftb.mods.sluice.block.SluiceBlockEntities;
import dev.ftb.mods.sluice.block.SluiceBlocks;
import dev.ftb.mods.sluice.integration.TheOneProbeProvider;
import dev.ftb.mods.sluice.integration.kubejs.KubeJSIntegration;
import dev.ftb.mods.sluice.item.SluiceModItems;
import dev.ftb.mods.sluice.loot.HammerModifier;
import dev.ftb.mods.sluice.recipe.FTBSluiceRecipes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.stream.Collectors;


@Mod(FTBSluice.MOD_ID)
public class FTBSluice {
    public static final String MOD_ID = "ftbsluice";
    public static final CreativeModeTab group = new CreativeModeTab(MOD_ID) {
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(SluiceModItems.IRON_SLUICE.get());
        }
    };

    public static final DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, MOD_ID);
    public static final RegistryObject<GlobalLootModifierSerializer<HammerModifier>> HAMMER_LOOT_MODIFIER = LOOT_MODIFIERS.register("hammer", HammerModifier.Serializer::new);

    public static FTBSluice instance;

    public FTBSluice() {
        instance = this;

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SluiceConfig.COMMON_CONFIG);

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        SluiceBlocks.REGISTRY.register(bus);
        SluiceModItems.REGISTRY.register(bus);
        SluiceBlockEntities.REGISTRY.register(bus);
        FTBSluiceRecipes.REGISTRY.register(bus);
        LOOT_MODIFIERS.register(bus);

        bus.addListener(this::clientSetup);
        bus.addListener(this::sendIMC);
        bus.addListener(this::loadComplete);

        MinecraftForge.EVENT_BUS.register(this);

        if (ModList.get().isLoaded("kubejs")) {
            KubeJSIntegration.init();
        }
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        FTBSluiceRecipes.createSluiceCaches();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        SluiceClient.init();
    }

    @SubscribeEvent
    public void recipesSetup(RecipesUpdatedEvent event) {
        RecipeManager recipeManager = event.getRecipeManager();
        FTBSluiceRecipes.hammerableCache.addAll(recipeManager.getAllRecipesFor(FTBSluiceRecipes.HAMMER_TYPE).stream().map(e -> e.ingredient).collect(Collectors.toList()));
        FTBSluiceRecipes.createSluiceCaches();
    }

    public void sendIMC(InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("theoneprobe")) {
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TheOneProbeProvider::new);
        }
    }
}

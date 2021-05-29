package dev.ftb.mods.sluice;

import dev.ftb.mods.sluice.block.SluiceModBlockEntities;
import dev.ftb.mods.sluice.block.SluiceModBlocks;
import dev.ftb.mods.sluice.integration.kubejs.KubeJSIntegration;
import dev.ftb.mods.sluice.item.SluiceModItems;
import dev.ftb.mods.sluice.loot.HammerModifier;
import dev.ftb.mods.sluice.recipe.SluiceModRecipeSerializers;
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
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.stream.Collectors;


@Mod(SluiceMod.MOD_ID)
public class SluiceMod {
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

	public static SluiceMod instance;

	public SluiceMod() {
		instance = this;

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SluiceConfig.COMMON_CONFIG);

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		SluiceModBlocks.REGISTRY.register(bus);
		SluiceModItems.REGISTRY.register(bus);
		SluiceModBlockEntities.REGISTRY.register(bus);
		SluiceModRecipeSerializers.REGISTRY.register(bus);
		LOOT_MODIFIERS.register(bus);

		bus.addListener(this::clientSetup);

		MinecraftForge.EVENT_BUS.register(this);

		if (ModList.get().isLoaded("kubejs")) {
			KubeJSIntegration.init();
		}
	}

	private void clientSetup(FMLClientSetupEvent event) {
		SluiceClient.init();
	}

	@SubscribeEvent
	public void recipesSetup(RecipesUpdatedEvent event) {
		RecipeManager recipeManager = event.getRecipeManager();
		SluiceModRecipeSerializers.hammerableCache.addAll(recipeManager.getAllRecipesFor(SluiceModRecipeSerializers.HAMMER_TYPE).stream().map(e -> e.ingredient).collect(Collectors.toList()));
	}
}

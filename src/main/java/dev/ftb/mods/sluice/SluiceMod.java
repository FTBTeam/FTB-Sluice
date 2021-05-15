package dev.ftb.mods.sluice;

import dev.ftb.mods.sluice.block.SluiceModBlockEntities;
import dev.ftb.mods.sluice.block.SluiceModBlocks;
import dev.ftb.mods.sluice.integration.kubejs.KubeJSIntegration;
import dev.ftb.mods.sluice.item.SluiceModItems;
import dev.ftb.mods.sluice.recipe.SluiceModRecipeSerializers;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author LatvianModder
 */
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

	public static SluiceMod instance;

	public SluiceMod() {
		instance = this;

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SluiceConfig.COMMON_CONFIG);

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		SluiceModBlocks.REGISTRY.register(bus);
		SluiceModItems.REGISTRY.register(bus);
		SluiceModBlockEntities.REGISTRY.register(bus);
		SluiceModRecipeSerializers.REGISTRY.register(bus);

		bus.addListener(this::clientSetup);

		if (ModList.get().isLoaded("kubejs")) {
			KubeJSIntegration.init();
		}
	}

	private void clientSetup(FMLClientSetupEvent event) {
		SluiceClient.init();
	}
}

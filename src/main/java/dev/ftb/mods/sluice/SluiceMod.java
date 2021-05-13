package dev.ftb.mods.sluice;

import dev.ftb.mods.sluice.recipe.SluiceModRecipeSerializers;
import dev.ftb.mods.sluice.block.SluiceModBlockEntities;
import dev.ftb.mods.sluice.block.SluiceModBlocks;
import dev.ftb.mods.sluice.integration.kubejs.KubeJSIntegration;
import dev.ftb.mods.sluice.item.SluiceModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author LatvianModder
 */
@Mod(SluiceMod.MOD_ID)
public class SluiceMod {
	public static final String MOD_ID = "ftbsluice";

	public static SluiceMod instance;

	public static CreativeModeTab group;

	public SluiceMod() {
		instance = this;

		group = new CreativeModeTab(MOD_ID) {
			@Override
			@OnlyIn(Dist.CLIENT)
			public ItemStack makeIcon() {
				return new ItemStack(SluiceModItems.IRON_SLUICE.get());
			}
		};

		SluiceModBlocks.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		SluiceModItems.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		SluiceModBlockEntities.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		SluiceModRecipeSerializers.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());

		if (ModList.get().isLoaded("kubejs")) {
			KubeJSIntegration.init();
		}
	}
}

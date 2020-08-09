package dev.latvian.mods.sluice;

import dev.latvian.mods.sluice.block.SluiceModBlockEntities;
import dev.latvian.mods.sluice.block.SluiceModBlocks;
import dev.latvian.mods.sluice.item.SluiceModItems;
import dev.latvian.mods.sluice.recipe.SluiceModRecipeSerializers;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author LatvianModder
 */
@Mod(SluiceMod.MOD_ID)
public class SluiceMod
{
	public static final String MOD_ID = "sluice";

	public static SluiceMod instance;

	public static ItemGroup group;

	public SluiceMod()
	{
		instance = this;

		group = new ItemGroup(MOD_ID)
		{
			@Override
			@OnlyIn(Dist.CLIENT)
			public ItemStack createIcon()
			{
				return new ItemStack(SluiceModItems.SLUICE.get());
			}
		};

		SluiceModBlocks.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		SluiceModItems.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		SluiceModBlockEntities.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		SluiceModRecipeSerializers.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
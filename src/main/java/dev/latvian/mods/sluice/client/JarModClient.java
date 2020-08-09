package dev.latvian.mods.sluice.client;

import dev.latvian.mods.sluice.SluiceMod;
import dev.latvian.mods.sluice.block.SluiceModBlocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SluiceMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class JarModClient
{
	@SubscribeEvent
	public static void setup(FMLClientSetupEvent event)
	{
		RenderTypeLookup.setRenderLayer(SluiceModBlocks.SLUICE.get(), RenderType.getTranslucent());

		// ClientRegistry.bindTileEntityRenderer(JarModBlockEntities.SLUICE.get(), SluiceBlockEntityRenderer::new);
	}
}

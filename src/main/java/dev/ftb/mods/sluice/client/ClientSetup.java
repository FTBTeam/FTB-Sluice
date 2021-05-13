package dev.ftb.mods.sluice.client;

import dev.ftb.mods.sluice.SluiceMod;
import dev.ftb.mods.sluice.block.SluiceModBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SluiceMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
	@SubscribeEvent
	public static void setup(FMLClientSetupEvent event) {
		ItemBlockRenderTypes.setRenderLayer(SluiceModBlocks.OAK_SLUICE.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(SluiceModBlocks.IRON_SLUICE.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(SluiceModBlocks.DIAMOND_SLUICE.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(SluiceModBlocks.NETHERITE_SLUICE.get(), RenderType.cutout());

		// ClientRegistry.bindTileEntityRenderer(JarModBlockEntities.SLUICE.get(), SluiceBlockEntityRenderer::new);
	}
}

package dev.ftb.mods.sluice.client;

import dev.ftb.mods.sluice.FTBSluice;
import dev.ftb.mods.sluice.block.SluiceBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = FTBSluice.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(SluiceBlocks.OAK_SLUICE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(SluiceBlocks.IRON_SLUICE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(SluiceBlocks.DIAMOND_SLUICE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(SluiceBlocks.NETHERITE_SLUICE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(SluiceBlocks.EMPOWERED_SLUICE.get(), RenderType.cutout());

        // ClientRegistry.bindTileEntityRenderer(JarModBlockEntities.SLUICE.get(), SluiceBlockEntityRenderer::new);
    }
}

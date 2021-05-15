package dev.ftb.mods.sluice;

import dev.ftb.mods.sluice.block.SluiceModBlockEntities;
import dev.ftb.mods.sluice.renderers.SluiceRenderer;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class SluiceClient {
    public static void init() {
        ClientRegistry.bindTileEntityRenderer(SluiceModBlockEntities.OAK_SLUICE.get(), SluiceRenderer::new);
        ClientRegistry.bindTileEntityRenderer(SluiceModBlockEntities.IRON_SLUICE.get(), SluiceRenderer::new);
        ClientRegistry.bindTileEntityRenderer(SluiceModBlockEntities.DIAMOND_SLUICE.get(), SluiceRenderer::new);
        ClientRegistry.bindTileEntityRenderer(SluiceModBlockEntities.NETHERITE_SLUICE.get(), SluiceRenderer::new);
    }
}

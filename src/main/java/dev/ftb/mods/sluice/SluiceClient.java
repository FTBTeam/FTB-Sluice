package dev.ftb.mods.sluice;

import dev.ftb.mods.sluice.block.SluiceBlockEntities;
import dev.ftb.mods.sluice.block.SluiceBlocks;
import dev.ftb.mods.sluice.block.pump.PumpBlockEntity;
import dev.ftb.mods.sluice.block.pump.PumpBlockEntityRender;
import dev.ftb.mods.sluice.block.sluice.SluiceScreen;
import dev.ftb.mods.sluice.block.sluice.SluiceRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class SluiceClient {
    public static void init() {
        ClientRegistry.bindTileEntityRenderer(SluiceBlockEntities.OAK_SLUICE.get(), SluiceRenderer::new);
        ClientRegistry.bindTileEntityRenderer(SluiceBlockEntities.IRON_SLUICE.get(), SluiceRenderer::new);
        ClientRegistry.bindTileEntityRenderer(SluiceBlockEntities.DIAMOND_SLUICE.get(), SluiceRenderer::new);
        ClientRegistry.bindTileEntityRenderer(SluiceBlockEntities.NETHERITE_SLUICE.get(), SluiceRenderer::new);

        ClientRegistry.bindTileEntityRenderer(SluiceBlockEntities.PUMP.get(), PumpBlockEntityRender::new);

        ClientRegistry.bindTileEntityRenderer(SluiceBlockEntities.PUMP.get(), PumpBlockEntityRender::new);

        MenuScreens.register(FTBSluice.SLUICE_MENU.get(), SluiceScreen::new);

        ItemBlockRenderTypes.setRenderLayer(SluiceBlocks.PUMP.get(), RenderType.cutout());
    }
}

package dev.ftb.mods.sluice.block.sluice;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.sluice.FTBSluice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SluiceScreen extends AbstractContainerScreen<SluiceBlockContainer> {
    private static final ResourceLocation BG = new ResourceLocation(FTBSluice.MOD_ID, "textures/gui/sluice_upgrade_menu.png");

    public SluiceScreen(SluiceBlockContainer container, Inventory inv, Component text) {
        super(container, inv, text);
        this.imageHeight = 133;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(PoseStack pose, int x, int y, float partialTicks) {
        this.renderBackground(pose);
        super.render(pose, x, y, partialTicks);

        String string = new TranslatableComponent("ftbsluice.power_cost", String.format("%,d", this.menu.tile.lastPowerCost) + "FE").getString();
        Minecraft.getInstance().font.draw(pose, string, (width / 2f) - Minecraft.getInstance().font.width(string) + 80, (height / 2f) - 60, 0x4B4A4A);

        this.renderTooltip(pose, x, y);
    }

    // Yonk (hopper screen) thanks <3
    @Override
    protected void renderBg(PoseStack pose, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(BG);
        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        this.blit(pose, k, l, 0, 0, this.imageWidth, this.imageHeight);
    }
}

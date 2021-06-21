package dev.ftb.mods.sluice.block.sluice;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SluiceScreen extends AbstractContainerScreen<SluiceBlockContainer> {
    public SluiceScreen(SluiceBlockContainer container, Inventory inv, Component text) {
        super(container, inv, text);
    }

    @Override
    protected void renderBg(PoseStack arg, float f, int i, int j) {

    }

}

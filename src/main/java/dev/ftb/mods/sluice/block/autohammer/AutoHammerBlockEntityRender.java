package dev.ftb.mods.sluice.block.autohammer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

public class AutoHammerBlockEntityRender extends BlockEntityRenderer<AutoHammerBlockEntity> {
    public AutoHammerBlockEntityRender(BlockEntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public void render(AutoHammerBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int otherLight) {

    }
}

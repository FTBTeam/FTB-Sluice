package dev.ftb.mods.sluice.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.sluice.block.SluiceBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.animation.TileEntityRendererAnimation;

public class SluiceRenderer extends TileEntityRendererAnimation<SluiceBlockEntity> {
    public SluiceRenderer(BlockEntityRenderDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(SluiceBlockEntity te, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int otherlight) {
        ItemStack resource = te.inventory.getStackInSlot(0);
        if (te.processed <= 0 || resource.isEmpty()) {
            return;
        }

        matrix.pushPose();
        matrix.translate(.5F, .5F, .5F);
        Minecraft.getInstance().getItemRenderer().renderStatic(
            resource, ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, light, otherlight, matrix, renderer
        );

        matrix.popPose();
    }
}

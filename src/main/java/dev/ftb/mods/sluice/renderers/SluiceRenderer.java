package dev.ftb.mods.sluice.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.ftb.mods.sluice.block.SluiceBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.animation.TileEntityRendererAnimation;

public class SluiceRenderer extends TileEntityRendererAnimation<SluiceBlockEntity> {
    public SluiceRenderer(BlockEntityRenderDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(SluiceBlockEntity te, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int otherlight) {
        ItemStack resource = te.inventory.getStackInSlot(0);
        if (resource.isEmpty()) {
            return;
        }

        int progress = (te.processed * 100) / te.maxProcessed;
        float offset = progress == 0 || !te.isProcessing ? 0 : 100 - progress;

        float v = te.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot();
        matrix.pushPose();
        matrix.translate(.5F, .85F - (offset / 250F), .5F);
        matrix.scale(1.4F, 1.4F, 1.4F);
        matrix.mulPose(Vector3f.YN.rotationDegrees(45 + v));

        Minecraft.getInstance().getItemRenderer().renderStatic(
            resource, ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, light, otherlight, matrix, renderer
        );

        matrix.popPose();
    }
}

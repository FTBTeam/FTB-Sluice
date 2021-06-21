package dev.ftb.mods.sluice.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.ftb.mods.sluice.block.sluice.SluiceBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.animation.TileEntityRendererAnimation;
import net.minecraftforge.fluids.FluidStack;

import static net.minecraft.core.Direction.AxisDirection.NEGATIVE;
import static net.minecraft.core.Direction.AxisDirection.POSITIVE;

public class SluiceRenderer extends TileEntityRendererAnimation<SluiceBlockEntity> {
    public SluiceRenderer(BlockEntityRenderDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(SluiceBlockEntity te, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int otherlight) {
        if (!te.tank.isEmpty()) {
            this.renderFluid(te, partialTick, matrix, renderer, light, otherlight);
        }

        ItemStack resource = te.inventory.getStackInSlot(0);
        if (resource.isEmpty()) {
            return;
        }

        int progress = (te.processed * 100) / te.maxProcessed;
        float offset = te.processed < 0 ? 0 : progress;

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

    // Lats code from jars (simpler this way)
    private void renderFluid(SluiceBlockEntity te, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int otherlight) {
        Minecraft mc = Minecraft.getInstance();
        FluidStack fluid = te.tank.getFluid();

        VertexConsumer builder = renderer.getBuffer(RenderType.translucent()).getVertexBuilder();

        mc.getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
        TextureAtlasSprite sprite = mc.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(fluid.getFluid().getAttributes().getFlowingTexture(fluid));

        int color = fluid.getFluid().getAttributes().getColor(fluid);
        float r = ((color >> 16) & 255) / 255F;
        float g = ((color >> 8) & 255) / 255F;
        float b = ((color) & 255) / 255F;
        float a = 1F;

        float y1 = .5F;

        float u0top = sprite.getU(3D);
        float v0top = sprite.getV(3D);
        float u1top = sprite.getU(13D);
        float v1top = sprite.getV(13D);

        Direction value = te.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        float v = value.toYRot();

        matrix.pushPose();
        matrix.translate(.5, 0, .5);
        matrix.mulPose(Vector3f.YP.rotationDegrees(-v));

        Matrix3f n = matrix.last().normal();
        Matrix4f m = matrix.last().pose();
        builder.vertex(m, -.38F, y1, -.39F).color(r, g, b, a).uv(u0top, v0top).overlayCoords(otherlight).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
        builder.vertex(m, -.38F, .13F, .45F).color(r, g, b, a).uv(u0top, v1top).overlayCoords(otherlight).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
        builder.vertex(m, .38F, .13F, .45F).color(r, g, b, a).uv(u1top, v1top).overlayCoords(otherlight).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
        builder.vertex(m, .38F, y1, -.39F).color(r, g, b, a).uv(u1top, v0top).overlayCoords(otherlight).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
        matrix.popPose();

        // Second block fluid
        matrix.pushPose();
        matrix.translate(0, -.87F, 0);
        matrix.translate((value.getAxisDirection() == POSITIVE && value.getAxis() == Direction.Axis.X) || (value.getAxisDirection() == NEGATIVE && value.getAxis() == Direction.Axis.Z) ? 1F : 0, 0, value.getAxisDirection() == POSITIVE ? 1F : 0);

        if (value.getAxis() == Direction.Axis.Z) {
            matrix.mulPose(Vector3f.YP.rotationDegrees(value.getAxisDirection() != POSITIVE ? 180 : 0));
        } else {
            matrix.mulPose(Vector3f.YP.rotationDegrees(value.getAxisDirection() == POSITIVE ? 90 : -90));
        }

        n = matrix.last().normal();
        m = matrix.last().pose();
        builder.vertex(m, .1F, 1F, 0F).color(r, g, b, a).uv(u0top, v0top).overlayCoords(otherlight).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
        builder.vertex(m, .1F, 1F, 1F).color(r, g, b, a).uv(u0top, v1top).overlayCoords(otherlight).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
        builder.vertex(m, .9F, 1F, 1F).color(r, g, b, a).uv(u1top, v1top).overlayCoords(otherlight).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
        builder.vertex(m, .9F, 1F, 0F).color(r, g, b, a).uv(u1top, v0top).overlayCoords(otherlight).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
        matrix.popPose();
    }
}

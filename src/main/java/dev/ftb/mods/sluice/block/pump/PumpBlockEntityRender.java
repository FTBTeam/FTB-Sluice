package dev.ftb.mods.sluice.block.pump;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class PumpBlockEntityRender extends BlockEntityRenderer<PumpBlockEntity> {
    public PumpBlockEntityRender(BlockEntityRenderDispatcher arg) {
        super(arg);
    }

    @Override
    public void render(PumpBlockEntity pump, float f, PoseStack stack, MultiBufferSource renderer, int light, int otherlight) {
        if (pump == null || Minecraft.getInstance().player == null) {
            return;
        }

        Vec3 cameraPos = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition();
        BlockPos blockPos = pump.getBlockPos();
        double v = Mth.atan2(cameraPos.z() - (blockPos.getZ() + .5F), cameraPos.x() - (blockPos.getX() + .5F));
        double v1 = cameraPos.distanceToSqr(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()));

        stack.pushPose();
        stack.translate(.5F, v1 > 30 ? 1.009F : 1.4F, .5F);
        stack.scale(.020F, -.020F,.020F);
        stack.mulPose(Vector3f.YP.rotation((float) ((Math.PI / 2) - (float) v)));

        if (v1 > 30) {
            stack.mulPose(Vector3f.XP.rotationDegrees(90));
        }

        Screen.drawCenteredString(stack, Minecraft.getInstance().font, "Time left", 0, -5, 0xFFFFFFFF);
        Screen.drawCenteredString(stack, Minecraft.getInstance().font, getTimeString(pump.timeLeft), 0, 5, 0xFFFFFFFF);

        stack.popPose();
    }

    private static String getTimeString(int ticks) {
        int seconds = ticks / 20;

        return (seconds % 3600) / 60 + "m " + (seconds % 3600) % 60 + "s";
    }
}

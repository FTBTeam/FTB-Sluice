package dev.ftb.mods.sluice.integration;

import dev.ftb.mods.sluice.FTBSluice;
import dev.ftb.mods.sluice.block.sluice.SluiceBlock;
import dev.ftb.mods.sluice.block.sluice.SluiceBlockEntity;
import mcjty.theoneprobe.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class TheOneProbeProvider implements IProbeInfoProvider, Function<ITheOneProbe, Void> {
    @Override
    public Void apply(ITheOneProbe iTheOneProbe) {
        iTheOneProbe.registerProvider(this);
        return null;
    }

    @Override
    public String getID() {
        return new ResourceLocation(FTBSluice.MOD_ID, "top_integration").toString();
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
        if (blockState.getBlock() instanceof SluiceBlock) {
            BlockEntity blockEntity = level.getBlockEntity(iProbeHitData.getPos());

            if (blockEntity == null) {
                return;
            }

            SluiceBlockEntity entity = (SluiceBlockEntity) blockEntity;

            ItemStack stackInSlot = entity.inventory.getStackInSlot(0);
            Item item = blockState.getValue(SluiceBlock.MESH).meshItem.get();
            if (item != Items.AIR) {
                ItemStack itemStack = new ItemStack(item);
                iProbeInfo.horizontal().item(itemStack).vertical().padding(0, 5).itemLabel(itemStack);
            }

            if (entity instanceof SluiceBlockEntity.OakSluiceBlockEntity || entity instanceof SluiceBlockEntity.IronSluiceBlockEntity) {
                iProbeInfo.tankSimple(entity.tank.getCapacity(), entity.tank.getFluid(), iProbeInfo.defaultProgressStyle()
                        .numberFormat(NumberFormat.COMPACT)
                        .prefix(entity.tank.getFluid().getDisplayName().getString() + ": ")
                        .suffix("mB")
                        .filledColor(0xff0000dd)
                        .alternateFilledColor(0xff000043)
                );
            }

            int progress = (entity.processed * 100) / entity.maxProcessed;

            if (entity.processed != 0 || !stackInSlot.isEmpty()) {
                iProbeInfo.horizontal().item(stackInSlot).vertical().padding(0, 3).progress(progress, 100, iProbeInfo.defaultProgressStyle()
                        .suffix("%").width(78));
            }
        }
    }
}

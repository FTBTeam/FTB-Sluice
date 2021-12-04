package dev.ftb.mods.sluice.integration;

import dev.ftb.mods.sluice.FTBSluice;
import dev.ftb.mods.sluice.block.autohammer.AutoHammerBlock;
import dev.ftb.mods.sluice.block.autohammer.AutoHammerBlockEntity;
import dev.ftb.mods.sluice.block.sluice.SluiceBlock;
import dev.ftb.mods.sluice.block.sluice.SluiceBlockEntity;
import mcjty.theoneprobe.api.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.List;
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
        if (blockState.getBlock() instanceof AutoHammerBlock) {
            this.autoHammerInfo(iProbeInfo, level, blockState, iProbeHitData);
        }

        if (blockState.getBlock() instanceof SluiceBlock) {
            this.sluiceProbeInfo(iProbeInfo, level, blockState, iProbeHitData);
        }
    }

    private void autoHammerInfo(IProbeInfo iProbeInfo, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
        BlockEntity blockEntity = level.getBlockEntity(iProbeHitData.getPos());

        if (blockEntity == null) {
            return;
        }

        AutoHammerBlockEntity entity = (AutoHammerBlockEntity) blockEntity;
        if (entity.getTimeOut() > 0) {
            iProbeInfo.progress(entity.getTimeOut(), entity.getTimeoutDuration(), iProbeInfo.defaultProgressStyle().color(Color.rgb(0, 10, 60), Color.rgb(0, 20, 255), Color.rgb(0, 20, 150), Color.rgb(0, 0, 0)).prefix("Timeout: ")).padding(0, 2);
        } else {
            iProbeInfo.progress(entity.getProgress(), entity.getMaxProgress(), iProbeInfo.defaultProgressStyle().color(Color.rgb(60, 10, 0), Color.rgb(255, 20, 0), Color.rgb(150, 20, 0), Color.rgb(0, 0, 0)).prefix("Progress: ")).padding(0, 2);
        }

        Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        ItemStack itemStack = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, AutoHammerBlockEntity.getInputDirection(direction))
                .map(h -> h.getStackInSlot(0))
                .orElse(ItemStack.EMPTY);

        if (!itemStack.isEmpty() || !entity.getHeldItem().isEmpty()) {
            iProbeInfo.horizontal(iProbeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).text("Input: ").item(itemStack.isEmpty() ? entity.getHeldItem() : itemStack);
        }

        entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, AutoHammerBlockEntity.getOutputDirection(direction)).ifPresent(h -> {
            List<ItemStack> stacks = new ArrayList<>(h.getSlots());
            for (int i = 0; i < h.getSlots(); i++) {
                if (!h.getStackInSlot(i).isEmpty()) {
                    stacks.add(h.getStackInSlot(i));
                }
            }

            if (stacks.isEmpty()) {
                return;
            }

            IProbeInfo horizontal = null;
            IProbeInfo vertical = iProbeInfo.vertical(iProbeInfo.defaultLayoutStyle()).text("Output: ").padding(0, 5);
            int rows = 0;
            int idx = 0;
            for (ItemStack stack : stacks) {
                if (idx % 6 == 0) {
                    horizontal = vertical.horizontal(iProbeInfo.defaultLayoutStyle().spacing(0));
                    rows++;
                    if (rows > 4) {
                        break;
                    }
                }
                horizontal.item(stack);
                idx++;
            }
        });
    }

    private void sluiceProbeInfo(IProbeInfo iProbeInfo, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
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

        if (entity.sluiceConfig.allowsTank.get()) {
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

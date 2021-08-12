package dev.ftb.mods.sluice.block.sluice;

import dev.ftb.mods.sluice.FTBSluice;
import dev.ftb.mods.sluice.SluiceConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SluiceBlockItem extends BlockItem {
    SluiceProperties props;

    public SluiceBlockItem(Block block, SluiceProperties properties) {
        super(block, new Item.Properties().tab(FTBSluice.group));
        this.props = properties;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);

        if (!Screen.hasShiftDown()) {
            components.add(new TranslatableComponent("ftbsluice.tooltip.sluice_1_" + this.props.name().toLowerCase()).withStyle(ChatFormatting.GRAY));
            components.add(new TranslatableComponent("ftbsluice.tooltip.sluice_2_" + this.props.name().toLowerCase()).withStyle(this.props == SluiceProperties.OAK ? ChatFormatting.RED : ChatFormatting.LIGHT_PURPLE));
            components.add(new TranslatableComponent("ftbsluice.tooltip.hold_shift").withStyle(ChatFormatting.AQUA));
        } else {
            components.add(new TranslatableComponent("ftbsluice.tooltip.processing_time", this.props.processingTime.get()).withStyle(ChatFormatting.GRAY));
            components.add(new TranslatableComponent("ftbsluice.tooltip.fluid_usage", this.props.fluidUsage.get()).withStyle(ChatFormatting.GRAY));
            components.add(new TranslatableComponent("ftbsluice.tooltip.can_hold", this.props.tankCap.get()).withStyle(ChatFormatting.GRAY));
        }
    }
}

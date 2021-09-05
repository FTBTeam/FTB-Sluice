package dev.ftb.mods.sluice.item;

import dev.ftb.mods.sluice.FTBSluice;
import dev.ftb.mods.sluice.block.sluice.SluiceBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CanteenItem extends Item {
    boolean isFunctional;

    public CanteenItem(boolean isFunctional) {
        super(new Properties().stacksTo(1).tab(FTBSluice.group));
        this.isFunctional = isFunctional;
    }

    @Override
    public void appendHoverText(ItemStack p_77624_1_, @Nullable Level p_77624_2_, List<Component> text, TooltipFlag p_77624_4_) {
        text.add(new TranslatableComponent(!this.isFunctional ? "ftbsluice.tooltip.damaged_canteen" : "ftbsluice.tooltip.canteen").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.getLevel().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!this.isFunctional) {
            return brokenAction(ctx);
        }

        return functionalAction(ctx);
    }

    private InteractionResult functionalAction(UseOnContext ctx) {
        return InteractionResult.PASS;
    }

    private InteractionResult brokenAction(UseOnContext ctx) {
        BlockEntity blockEntity = ctx.getLevel().getBlockEntity(ctx.getClickedPos());
        if (blockEntity == null) {
            return InteractionResult.PASS;
        }

        if (blockEntity instanceof SluiceBlockEntity) {
            SluiceBlockEntity entity = (SluiceBlockEntity) blockEntity;
            entity.fluidOptional.ifPresent(e -> e.internalDrain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE));
        } else {
            // Empty all tanks of a compatible tank
            blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(handler -> {
                for (int i = 0; i < handler.getTanks(); i++) {
                    handler.drain(handler.getFluidInTank(i), IFluidHandler.FluidAction.EXECUTE);
                }
            });
        }

        return InteractionResult.SUCCESS;
    }
}

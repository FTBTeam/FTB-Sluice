package dev.ftb.mods.sluice.item;

import dev.ftb.mods.sluice.FTBSluice;
import dev.ftb.mods.sluice.SluiceConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UpgradeItem extends Item {
    private Upgrades upgrade;

    public UpgradeItem(Upgrades upgrade) {
        super(new Properties().tab(FTBSluice.group).stacksTo(SluiceConfig.GENERAL.maxUpgradeStackSize.get()));
        this.upgrade = upgrade;
    }

    public Upgrades getUpgrade() {
        return upgrade;
    }

    @Override
    public void appendHoverText(ItemStack p_77624_1_, @Nullable Level p_77624_2_, List<Component> text, TooltipFlag p_77624_4_) {
        super.appendHoverText(p_77624_1_, p_77624_2_, text, p_77624_4_);

        text.add(new TranslatableComponent("ftbsluice.tooltip." + this.upgrade.tooltip));
        text.add(new TranslatableComponent("ftbsluice.tooltip.upgrade_meta", SluiceConfig.GENERAL.exponentialCostBaseN.get()).withStyle(ChatFormatting.GRAY));
    }
}

package dev.ftb.mods.sluice.item;

import dev.ftb.mods.sluice.FTBSluice;
import dev.ftb.mods.sluice.SluiceConfig;
import net.minecraft.world.item.Item;

public class UpgradeItem extends Item {
    public UpgradeItem() {
        super(new Properties().tab(FTBSluice.group).stacksTo(SluiceConfig.GENERAL.maxUpgradeStackSize.get()));
    }


}

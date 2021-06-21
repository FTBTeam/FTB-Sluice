package dev.ftb.mods.sluice.item;

import dev.ftb.mods.sluice.FTBSluice;
import dev.ftb.mods.sluice.SluiceConfig;
import net.minecraft.world.item.Item;

public class UpgradeItem extends Item {
    private Upgrades upgrade;

    public UpgradeItem(Upgrades upgrade) {
        super(new Properties().tab(FTBSluice.group).stacksTo(SluiceConfig.GENERAL.maxUpgradeStackSize.get()));
        this.upgrade = upgrade;
    }

    public Upgrades getUpgrade() {
        return upgrade;
    }
}

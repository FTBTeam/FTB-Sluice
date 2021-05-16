package dev.ftb.mods.sluice.item;

import dev.ftb.mods.sluice.SluiceMod;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluids;

public class ClayBucket extends BucketItem {
    public ClayBucket() {
        super(() -> Fluids.EMPTY, new Properties().tab(SluiceMod.group));
    }
}

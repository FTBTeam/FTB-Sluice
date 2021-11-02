package dev.ftb.mods.sluice.block.sluice;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class QuickDropItemEntity extends ItemEntity {
    public QuickDropItemEntity(Level level, double x, double y, double z, ItemStack stack, int lifespan) {
        super(level, x, y, z, stack);

        this.lifespan = lifespan;
    }
}

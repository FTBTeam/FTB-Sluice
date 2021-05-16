package dev.ftb.mods.sluice.item;

import dev.ftb.mods.sluice.SluiceMod;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;

public class HammerItem extends DiggerItem {
    public HammerItem(Tiers tier, float attackBase, float attackSpeed) {
        super(attackBase, attackSpeed, tier, new HashSet<>(), new Properties().tab(SluiceMod.group));
    }

    @Override
    public float getDestroySpeed(ItemStack p_150893_1_, BlockState p_150893_2_) {
        return this.speed;
    }
}

package dev.ftb.mods.sluice.item;

import dev.ftb.mods.sluice.SluiceMod;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolType;

import java.util.HashSet;

public class HammerItem extends DiggerItem {
    private static final Properties props = new Properties().tab(SluiceMod.group);

    public HammerItem(Tiers tier, float attackBase, float attackSpeed) {
        this(tier, attackBase, attackSpeed, false);
    }

    public HammerItem(Tiers tier, float attackBase, float attackSpeed, boolean fireResistant) {
        super(attackBase, attackSpeed, tier, new HashSet<>(), !fireResistant
            ? props.addToolType(ToolType.PICKAXE, tier.getLevel()).addToolType(ToolType.SHOVEL, tier.getLevel()).durability(tier.getUses())
            : props.addToolType(ToolType.PICKAXE, tier.getLevel()).addToolType(ToolType.SHOVEL, tier.getLevel()).durability(tier.getUses()).fireResistant());
    }

    @Override
    public float getDestroySpeed(ItemStack p_150893_1_, BlockState p_150893_2_) {
        return this.speed;
    }
}

package dev.ftb.mods.sluice.item;

import dev.ftb.mods.sluice.FTBSluice;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolType;

import java.util.HashSet;

public class HammerItem extends DiggerItem {
    private static final Properties props = new Properties().tab(FTBSluice.group);

    public HammerItem(Tiers tier, float attackBase, float attackSpeed) {
        this(tier, attackBase, attackSpeed, false);
    }

    public HammerItem(Tiers tier, float attackBase, float attackSpeed, boolean fireResistant) {
        super(attackBase, attackSpeed, tier, new HashSet<>(), !fireResistant
                ? props.addToolType(ToolType.PICKAXE, tier.getLevel()).addToolType(ToolType.SHOVEL, tier.getLevel()).durability(tier.getUses())
                : props.addToolType(ToolType.PICKAXE, tier.getLevel()).addToolType(ToolType.SHOVEL, tier.getLevel()).durability(tier.getUses()).fireResistant());
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        ToolType harvestTool = state.getHarvestTool();
        if (harvestTool == ToolType.PICKAXE || harvestTool == ToolType.SHOVEL) {
            if (this.getTier().getLevel() >= state.getHarvestLevel()) {
                return true;
            }
        }
        // Mojank hardcoded shovel checks
        if (state.is(Blocks.SNOW) || state.is(Blocks.SNOW_BLOCK)) {
            return true;
        }
        // Mojank hardcoded pickaxe checks
        // ...god am I glad this is going away in 1.17
        Material material = state.getMaterial();
        return material == Material.STONE || material == Material.METAL || material == Material.HEAVY_METAL;
    }

    @Override
    public float getDestroySpeed(ItemStack p_150893_1_, BlockState p_150893_2_) {
        return this.speed;
    }
}

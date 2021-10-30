package dev.ftb.mods.sluice.block.autohammer;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolType;

import java.util.function.Supplier;

public class AutoHammerBlock extends Block {
    private final Supplier<Item> baseHammerItem;

    public AutoHammerBlock(Supplier<Item> baseHammerItem) {
        super(Properties.of(Material.STONE).harvestTool(ToolType.PICKAXE).harvestLevel(1).strength(1F, 1F));

        this.baseHammerItem = baseHammerItem;
    }


}

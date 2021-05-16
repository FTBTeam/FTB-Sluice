package dev.ftb.mods.sluice.item;

import dev.ftb.mods.sluice.SluiceMod;
import dev.ftb.mods.sluice.recipe.SluiceModRecipeSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class HammerItem extends Item {
    public HammerItem() {
        super(new Properties().tab(SluiceMod.group));
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState block, BlockPos pos, LivingEntity entity) {
        List<ItemStack> hammerDrops = SluiceModRecipeSerializers.getHammerDrops(level, stack, new ItemStack(block.getBlock()));
        hammerDrops.forEach(e -> Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), e));
        return false;
    }
}

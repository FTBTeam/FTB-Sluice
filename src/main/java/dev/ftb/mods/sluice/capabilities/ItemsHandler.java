package dev.ftb.mods.sluice.capabilities;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

/**
 * This provides two new consumers to allow us to modify the block based on the
 * item cap. We also by default protect the IO of the cap unless we've stated
 * that the block entity can be public.
 */
public class ItemsHandler extends ItemStackHandler {
    private final boolean isProtected;

    public ItemsHandler(boolean isProtected, int size) {
        super(size);
        this.isProtected = isProtected;
    }

    @NotNull
    public ItemStack internalInsert(int slot, @NotNull ItemStack stack, boolean simulate) {
        return super.insertItem(slot, stack, simulate);
    }

    @NotNull
    public ItemStack internalExtract(int slot, int amount, boolean simulate) {
        return super.extractItem(slot, amount, simulate);
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (this.isProtected) {
            return ItemStack.EMPTY;
        }

        return this.internalInsert(slot, stack, simulate);
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (this.isProtected) {
            return ItemStack.EMPTY;
        }

        return this.internalExtract(slot, amount, simulate);
    }
}

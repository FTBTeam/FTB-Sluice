package dev.ftb.mods.sluice.block.sluice;

import dev.ftb.mods.sluice.FTBSluice;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Objects;

public class SluiceBlockContainer extends AbstractContainerMenu {

    public static SluiceBlockContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data) {
        return new SluiceBlockContainer(windowId, inv, (SluiceBlockEntity) Objects.requireNonNull(inv.player.level.getBlockEntity(data.readBlockPos())));
    }

    public SluiceBlockContainer(int id, Inventory inv, SluiceBlockEntity tile) {
        super(FTBSluice.SLUICE_MENU.get(), id);

        ItemStackHandler handler = tile.upgradeInventory;

        addSlot(new SlotItemHandler(handler, 0, 132, 18));
        addSlot(new SlotItemHandler(handler, 1, 132, 36));
        addSlot(new SlotItemHandler(handler, 2, 132, 54));

        // Slots for the hotbar
        for (int row = 0; row < 9; ++ row) {
            int x = -12 + row * 18;
            int y = 70 + 86;
            addSlot(new Slot(inv, row, x, y));
        }

        // Slots for the main inventory
        for (int row = 1; row < 4; ++ row) {
            for (int col = 0; col < 9; ++ col) {
                int x = -12 + col * 18;
                int y = row * 18 + (70 + 10);
                addSlot(new Slot(inv, col + row * 9, x, y));
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack currentStack = slot.getItem();
            itemstack = currentStack.copy();

            if (index < 3) {
                if (! this.moveItemStackTo(currentStack, 3, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (! this.moveItemStackTo(currentStack, 0, this.slots.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (currentStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }
}

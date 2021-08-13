package dev.ftb.mods.sluice.block.sluice;

import dev.ftb.mods.sluice.FTBSluice;
import dev.ftb.mods.sluice.item.UpgradeItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Objects;

public class SluiceBlockContainer extends AbstractContainerMenu {

    public final SluiceBlockEntity tile;

    public static SluiceBlockContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data) {
        return new SluiceBlockContainer(windowId, inv, (SluiceBlockEntity) Objects.requireNonNull(inv.player.level.getBlockEntity(data.readBlockPos())));
    }

    public SluiceBlockContainer(int id, Inventory inv, SluiceBlockEntity tile) {
        super(FTBSluice.SLUICE_MENU.get(), id);

        ItemStackHandler handler = tile.upgradeInventory;
        this.tile = tile;

        // Yonk (Hopper)
        int n;
        for(n = 0; n < 3; ++n) {
            addSlot(new SlotItemHandler(handler, n, 62 + n * 18, 20));
        }

        for(n = 0; n < 3; ++n) {
            for(int m = 0; m < 9; ++m) {
                this.addSlot(new Slot(inv, m + n * 9 + 9, 8 + m * 18, n * 18 + 51));
            }
        }

        for(n = 0; n < 9; ++n) {
            this.addSlot(new Slot(inv, n, 8 + n * 18, 109));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack cur = slot.getItem();
            stack = cur.copy();

            if (index < 3) {
                if (!this.moveItemStackTo(cur, 3, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!(stack.getItem() instanceof UpgradeItem && this.moveItemStackTo(cur, 0, 3, false))) {
                return ItemStack.EMPTY;
            }

            if (cur.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return stack;
    }
}

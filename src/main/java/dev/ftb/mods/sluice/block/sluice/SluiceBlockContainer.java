package dev.ftb.mods.sluice.block.sluice;

import dev.ftb.mods.sluice.FTBSluice;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.Objects;

public class SluiceBlockContainer extends AbstractContainerMenu {

    public static SluiceBlockContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data) {
        return new SluiceBlockContainer(windowId, inv, (SluiceBlockEntity) Objects.requireNonNull(inv.player.level.getBlockEntity(data.readBlockPos())));
    }

    public SluiceBlockContainer(int id, Inventory inv, SluiceBlockEntity tile) {
        super(FTBSluice.SLUICE_MENU.get(), id);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}

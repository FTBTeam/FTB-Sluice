package dev.ftb.mods.sluice.item;

import dev.ftb.mods.sluice.FTBSluice;
import dev.ftb.mods.sluice.block.MeshType;
import net.minecraft.world.item.Item;


public class MeshItem extends Item {
    public final MeshType mesh;

    public MeshItem(MeshType m) {
        super(new Properties().tab(FTBSluice.group).stacksTo(16));
        this.mesh = m;
    }
}

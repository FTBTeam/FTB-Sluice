package dev.ftb.mods.sluice.item;

import dev.ftb.mods.sluice.SluiceMod;
import dev.ftb.mods.sluice.block.MeshType;
import net.minecraft.world.item.Item;

/**
 * @author LatvianModder
 */
public class MeshItem extends Item {
	public final MeshType mesh;

	public MeshItem(MeshType m) {
		super(new Properties().tab(SluiceMod.group).stacksTo(16));
		mesh = m;
	}
}

package dev.latvian.mods.sluice.item;

import dev.latvian.mods.sluice.SluiceMod;
import dev.latvian.mods.sluice.block.MeshType;
import net.minecraft.item.Item;

/**
 * @author LatvianModder
 */
public class MeshItem extends Item
{
	public final MeshType mesh;

	public MeshItem(MeshType m)
	{
		super(new Properties().group(SluiceMod.group).maxStackSize(16));
		mesh = m;
	}
}

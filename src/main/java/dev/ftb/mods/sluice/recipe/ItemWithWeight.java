package dev.ftb.mods.sluice.recipe;

import com.google.common.base.MoreObjects;
import net.minecraft.world.item.ItemStack;

public class ItemWithWeight {
	public static final ItemWithWeight NONE = new ItemWithWeight(ItemStack.EMPTY, 0);

	public final ItemStack item;
	public final double weight;

	public ItemWithWeight(ItemStack i, double w) {
		item = i;
		weight = w;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("item", item)
			.add("weight", weight)
			.toString();
	}
}

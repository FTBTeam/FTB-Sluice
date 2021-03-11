package dev.latvian.mods.sluice.recipe;

import net.minecraft.world.item.ItemStack;

public class ItemWithWeight {
	public static final ItemWithWeight NONE = new ItemWithWeight(ItemStack.EMPTY, 0);

	public final ItemStack item;
	public final int weight;

	public ItemWithWeight(ItemStack i, int w) {
		item = i;
		weight = w;
	}
}

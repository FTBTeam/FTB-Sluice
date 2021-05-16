package dev.ftb.mods.sluice.item;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum HammerTypes implements StringRepresentable {
	WOODEN("wooden", SluiceModItems.WOODEN_HAMMER.get()),
	STONE("stone", SluiceModItems.STONE_HAMMER.get()),
	IRON("iron", SluiceModItems.IRON_HAMMER.get()),
	GOLD("gold", SluiceModItems.GOLD_HAMMER.get()),
	DIAMOND("diamond", SluiceModItems.DIAMOND_HAMMER.get());

	public static HammerTypes[] VALUES = values();
	public static Map<String, HammerTypes> MAP = new HashMap<>();

	static {
		for (HammerTypes type : VALUES) {
			MAP.put(type.name, type);
		}
	}

	private final String name;
	private ItemStack hammer;

	HammerTypes(String name, Item hammer) {
		this.name = name;
		this.hammer = new ItemStack(hammer);
	}

	public static Optional<HammerTypes> getHammerFromItem(ItemStack item) {
		return Arrays.stream(HammerTypes.values()).filter(e -> e.hammer.sameItemStackIgnoreDurability(item)).findFirst();
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
}

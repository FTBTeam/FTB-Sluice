package dev.latvian.mods.sluice.block;

import dev.latvian.mods.sluice.item.SluiceModItems;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public enum MeshType implements StringRepresentable {
	NONE("none", () -> Items.AIR, () -> null),
	CLOTH("cloth", SluiceModItems.CLOTH_MESH, () -> ItemTags.bind("forge:string")),
	IRON("iron", SluiceModItems.IRON_MESH, () -> ItemTags.bind("forge:ingots/iron")),
	GOLD("gold", SluiceModItems.GOLD_MESH, () -> ItemTags.bind("forge:ingots/gold")),
	DIAMOND("diamond", SluiceModItems.DIAMOND_MESH, () -> ItemTags.bind("forge:gems/diamond"));

	public static MeshType[] VALUES = values();
	public static MeshType[] REAL_VALUES = {CLOTH, IRON, GOLD, DIAMOND};
	public static Map<String, MeshType> MAP = new HashMap<>();

	static {
		for (MeshType type : VALUES) {
			MAP.put(type.name, type);
		}
	}

	private final String name;
	public final Supplier<Item> meshItem;
	private final Supplier<Tag<Item>> ingredient;
	private Tag<Item> ingredient0;

	MeshType(String n, Supplier<Item> m, Supplier<Tag<Item>> i) {
		name = n;
		meshItem = m;
		ingredient = i;
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public ItemStack getItemStack() {
		return meshItem.get() == Items.AIR ? ItemStack.EMPTY : new ItemStack(meshItem.get());
	}

	public Tag<Item> getIngredient() {
		if (ingredient0 == null) {
			ingredient0 = ingredient.get();
		}

		return ingredient0;
	}
}
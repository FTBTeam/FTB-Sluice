package dev.ftb.mods.sluice.block;

import dev.ftb.mods.sluice.item.SluiceModItems;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public enum MeshType implements StringRepresentable {
	NONE("none", () -> Items.AIR, () -> null),
	CLOTH("cloth", SluiceModItems.CLOTH_MESH, () -> ItemTags.bind("forge:string")),
	IRON("iron", SluiceModItems.IRON_MESH, () -> ItemTags.bind("forge:ingots/iron")),
	GOLD("gold", SluiceModItems.GOLD_MESH, () -> ItemTags.bind("forge:ingots/gold")),
	DIAMOND("diamond", SluiceModItems.DIAMOND_MESH, () -> ItemTags.bind("forge:gems/diamond")),
	BLAZING("blazing", SluiceModItems.BLAZING_MESH, () -> Tags.Items.INGOTS_NETHERITE);

	public static MeshType[] VALUES = values();
	public static MeshType[] REAL_VALUES = {CLOTH, IRON, GOLD, DIAMOND, BLAZING};
	public static Map<String, MeshType> MAP = new HashMap<>();

	static {
		for (MeshType type : VALUES) {
			MAP.put(type.name, type);
		}
	}

	public final Supplier<Item> meshItem;
	private final String name;
	private final Supplier<Tag<Item>> ingredient;
	private Tag<Item> ingredient0;

	MeshType(String n, Supplier<Item> m, Supplier<Tag<Item>> i) {
		this.name = n;
		this.meshItem = m;
		this.ingredient = i;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

	public ItemStack getItemStack() {
		return this.meshItem.get() == Items.AIR ? ItemStack.EMPTY : new ItemStack(this.meshItem.get());
	}

	public Tag<Item> getIngredient() {
		if (this.ingredient0 == null) {
			this.ingredient0 = this.ingredient.get();
		}

		return this.ingredient0;
	}
}

package dev.latvian.mods.sluice.block;

import dev.latvian.mods.sluice.item.SluiceModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public enum MeshType implements IStringSerializable
{
	NONE("none", () -> ItemStack.EMPTY),
	CLOTH("cloth", () -> new ItemStack(SluiceModItems.CLOTH_MESH.get())),
	IRON("iron", () -> new ItemStack(SluiceModItems.IRON_MESH.get())),
	GOLD("gold", () -> new ItemStack(SluiceModItems.GOLD_MESH.get())),
	DIAMOND("diamond", () -> new ItemStack(SluiceModItems.DIAMOND_MESH.get()));

	public static MeshType[] VALUES = values();
	public static Map<String, MeshType> MAP = new HashMap<>();

	static
	{
		for (MeshType type : VALUES)
		{
			MAP.put(type.name, type);
		}
	}

	private final String name;
	public final Supplier<ItemStack> meshItem;

	MeshType(String n, Supplier<ItemStack> m)
	{
		name = n;
		meshItem = m;
	}

	@Override
	public String getString()
	{
		return name;
	}
}